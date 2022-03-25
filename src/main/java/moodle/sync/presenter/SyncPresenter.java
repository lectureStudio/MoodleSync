package moodle.sync.presenter;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.fileserver.FileServerClientFTP;
import moodle.sync.util.FileServerFile;
import moodle.sync.util.FileService;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.UploadElement;
import moodle.sync.view.SyncView;
import moodle.sync.web.MoodleUploadTemp;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.MoodleUpload;
import moodle.sync.web.service.MoodleService;
import org.apache.commons.io.FilenameUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.NotificationType;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyncPresenter extends Presenter<SyncView> {

    private final MoodleSyncConfiguration config;

    private final MoodleService moodleService;

    private List<Module> modules;

    private List<UploadElement> uploadElements;


    @Inject
    SyncPresenter(ApplicationContext context, SyncView view, MoodleService moodleService) {
        super(context, view);

        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.moodleService = moodleService;
    }

    @Override
    public void initialize() {

        uploadElements = prepareSync();

        view.setFiles(uploadElements);
        view.setOnSync(this::execute);
        view.setOnClose(this::close);
    }

    public List<UploadElement> prepareSync() {
        List<UploadElement> elements = new ArrayList<>();
        try {
            //Update Recent Secton
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));

            //Create Directory if not existed

            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
            FileService.directoryManager(execute);


            //List containing all files in directory
            List<Path> fileList = FileService.getFilesInDirectory(execute);

            //Sort by filetype
            List<Path> fileListMoodle = new ArrayList<>();
            List<Path> fileListFileserver = new ArrayList<>();
            List<Path> fileTypeNotFound = new ArrayList<>();

            for (Path file : fileList) {
                if (contains(config.getFormatsMoodle().split(","), FilenameUtils.getExtension(file.getFileName().toString()))) {
                    fileListMoodle.add(file);
                } else if (contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(file.getFileName().toString()))) {
                    fileListFileserver.add(file);
                } else {
                    fileTypeNotFound.add(file);
                }
            }

            //Handling for Filetype "format moodle"
            //Every file inside the directory gets checked whether its filename is on Moodle or not and then is handled differently
            //List containing resource modules in choosen section
            modules = FileService.getModulesByType("resource", config.getRecentSection());
            for (Path item : fileListMoodle) {
                UploadElement temp = FileService.CheckMoodleModule(modules, item);
                if (temp != null) {
                    elements.add(temp);
                }
            }

            //Handling for Filetype "format fileserver"
            //Only connect to fileserver if there are files which could be uploaded
            if (fileListFileserver.size() != 0) {
                String url = config.getFileserver();
                String user = config.getUserFileserver();
                String password = config.getPasswordFileserver();
                if(url == null || url.isEmpty() || url.isBlank() || user == null || user.isEmpty() || user.isBlank() || password == null || password.isEmpty() || password.isBlank()){
                    showNotification(NotificationType.ERROR, "sync.sync.error.title",
                            "sync.sync.error.fileserver1.message");
                }
                else {
                    List<FileServerFile> files = List.of();
                    try {
                        FileServerClientFTP fileClient = new FileServerClientFTP(config);
                        fileClient.connect();
                        files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> Directory angeben, wahrscheinlich nach Kursen
                        fileClient.disconnect();

                        for (Path item : fileListFileserver) {
                            UploadElement temp = FileService.CheckFileServerModule(files, item);
                            if (temp != null) {
                                elements.add(temp);
                            }
                        }
                    }
                    catch (Exception e){
                        logException(e, "Sync failed");
                        showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                "sync.sync.error.fileserver2.message");

                    }
                }
            }

            //Handling for filetype "filetypenotfound"
            for (Path item : fileTypeNotFound) {
                elements.add(new UploadElement(item, false, 0, false, MoodleAction.DatatypeNotKnown, false));
            }
        } catch (Throwable e) {
            logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        "sync.sync.error.message");
        }
        return elements;
    }

    private void execute() {
        try {
            List<UploadElement> sync = uploadElements;
            List<UploadElement> syncmoodle = new ArrayList<>();
            List<UploadElement> syncftp = new ArrayList<>();

            for (UploadElement item : sync) {
                if (item.getAction() == MoodleAction.MoodleUpload || item.getAction() == MoodleAction.MoodleSynchronize) {
                    syncmoodle.add(item);
                } else if ((item.getAction() == MoodleAction.FTPUpload && item.getChecked().get()) || (item.getAction() == MoodleAction.FTPSynchronize && item.getChecked().get())) {
                    syncftp.add(item);
                }
            }

            for (UploadElement item : syncmoodle) {
                if (item.getChecked().get()) {
                    //Moodle lookup
                    if (item.getAction() == MoodleAction.MoodleUpload) {
                        //Case 2: file not uploaded; uploaded = false
                        MoodleUploadTemp uploader = new MoodleUploadTemp();
                        try {
                            MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                            moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename());
                        }
                        catch (Exception e){
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                    MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"),item.getPath().getFileName().toString()));
                        }

                    } else if (item.getAction() == MoodleAction.MoodleSynchronize) {
                        Module online = modules.get(item.getIfuploaded());
                        try {
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/"/* + config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                            moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
                            moodleService.removeResource(config.getMoodleToken(), online.getId());
                        }
                        catch (Exception e){
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                    "sync.sync.error.upload.message");
                        }
                    }
                }
            }

            if (syncftp.size() > 0) {
                    FileServerClientFTP fileClient = new FileServerClientFTP(config);
                    fileClient.connect();
                    for (UploadElement item : syncftp) {
                            if (item.getAction() == MoodleAction.FTPUpload) {
                                String url = fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
                                moodleService.setUrl(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), item.getPath().getFileName().toString(), url);
                            } else if (item.getAction() == MoodleAction.FTPSynchronize) {
                                //Depends on fileserver -> does the url change?
                                fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
                            }
                    }
                    fileClient.disconnect();
            }
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                    "sync.sync.error.message");
        }
        close();
    }

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }
}