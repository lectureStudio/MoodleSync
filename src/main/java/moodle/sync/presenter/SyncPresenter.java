package moodle.sync.presenter;

import javafx.beans.property.SimpleStringProperty;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.fileserver.FileServerClientFTP;
import moodle.sync.util.FileServerFile;
import moodle.sync.util.FileService;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.UploadData.UploadFolderElement;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.view.SyncView;
import moodle.sync.web.MoodleUploadTemp;
import moodle.sync.web.json.Content;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.MoodleUpload;
import moodle.sync.web.service.MoodleService;
import org.apache.commons.io.FilenameUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.NotificationType;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
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

    private List<Module> directories;

    private List<UploadData> uploadElements;

    private List<FileServerFile> files = List.of();


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

    public List<UploadData> prepareSync() {
        List<UploadData> fileList = new ArrayList<>();
        try {
            //Update Recent Secton
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));

            //Create Directory if not existed

            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
            FileService.directoryManager(execute);


            //List containing all files in directory
            fileList = FileService.getFilesInDirectory(execute);

            List<Path> pathList =  FileService.fileServerRequired(execute);
            Boolean fileServerRequired = false;

            for(int i = 0; i < pathList.size(); i++){
                Path file = pathList.get(i);
                if(Files.isDirectory(file)){
                    fileList.addAll(FileService.getFilesInDirectory(file));
                }
                else if (contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(file.getFileName().toString()))) {
                    fileServerRequired = true;
                }
            }

            if(fileServerRequired){
                String url = config.getFileserver();
                String user = config.getUserFileserver();
                String password = config.getPasswordFileserver();
                if(url == null || url.isEmpty() || url.isBlank() || user == null || user.isEmpty() || user.isBlank() || password == null || password.isEmpty() || password.isBlank()){
                    showNotification(NotificationType.ERROR, "sync.sync.error.title",
                            "sync.sync.error.fileserver1.message");
                }
                else {
                    try {
                        FileServerClientFTP fileClient = new FileServerClientFTP(config);
                        fileClient.connect();
                        files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> Directory angeben, wahrscheinlich nach Kursen
                        fileClient.disconnect();
                    }catch (Exception e){
                        logException(e, "Sync failed");
                        showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                "sync.sync.error.fileserver2.message");

                    }
                }
            }
            modules = FileService.getModulesByType("resource", config.getRecentSection());

            directories = FileService.getModulesByType("folder", config.getRecentSection());
            if(directories.isEmpty() == false){
                for(Module dir : directories){
                    List<Content> dircon = dir.getContents();
                    for(Content con : dircon){
                        List<Content> content = new ArrayList<>();
                        content.add(con);
                        modules.add(new Module(content));
                    }
                }
            }

            //Iteriertes vorgehen über jedes Element von List<UploadData>
            checkUploadData(fileList);

        } catch (Throwable e) {
            logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        "sync.sync.error.message");
        }
        return fileList;
    }

    private List<UploadData> checkUploadData(List<UploadData> data) throws IOException {
        List<UploadData> remove = new ArrayList<>();
        for(UploadData item : data) {
            if (item instanceof UploadFolderElement) {
                checkUploadData(((UploadFolderElement) item).getContent());
            } else if (item instanceof UploadElement) {
                Path path = item.getPath();
                if (contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(path.getFileName().toString()))) {
                    UploadElement temp = FileService.CheckFileServerModule(files, path);
                    if (temp != null) {
                        ((UploadElement) item).setUploaded(temp.isUploaded());
                        ((UploadElement) item).setIfuploaded(temp.getIfuploaded());
                        ((UploadElement) item).setChecked(temp.getChecked().get());
                        ((UploadElement) item).setAction(temp.getAction());
                        ((UploadElement) item).setSelectable(temp.getSelectable());
                        ((UploadElement) item).setFileName(path.getFileName().toString());
                    }
                    else{
                        remove.add(item);
                    }
                } else if (contains(config.getFormatsMoodle().split(","), FilenameUtils.getExtension(path.getFileName().toString()))) {
                    UploadElement temp = FileService.CheckMoodleModule(modules, path);
                    if (temp != null) {
                        ((UploadElement) item).setUploaded(temp.isUploaded());
                        ((UploadElement) item).setIfuploaded(temp.getIfuploaded());
                        ((UploadElement) item).setChecked(temp.getChecked().get());
                        ((UploadElement) item).setAction(temp.getAction());
                        ((UploadElement) item).setSelectable(temp.getSelectable());
                        ((UploadElement) item).setFileName(path.getFileName().toString());
                    }
                    else{
                        remove.add(item);
                    }
                }
                //Case: type not found
                else {
                    ((UploadElement) item).setUploaded(false);
                    ((UploadElement) item).setIfuploaded(0);
                    ((UploadElement) item).setChecked(false);
                    ((UploadElement) item).setAction(MoodleAction.DatatypeNotKnown);
                    ((UploadElement) item).setSelectable(false);
                    ((UploadElement) item).setFileName(path.getFileName().toString());
                }
            }
        }
        for(UploadData rem : remove){
            data.remove(rem);
        }
        return data;
    }

    private void execute() {
       // try {

//            List<UploadData> sync = uploadElements;
//            List<UploadElement> syncmoodle = new ArrayList<>();
//            List<UploadElement> syncftp = new ArrayList<>();
//
//            for (UploadData item : sync) {
//                if (item.getAction() == MoodleAction.MoodleUpload || item.getAction() == MoodleAction.MoodleSynchronize) {
//                    syncmoodle.add(item);
//                } else if ((item.getAction() == MoodleAction.FTPUpload && item.getChecked().get()) || (item.getAction() == MoodleAction.FTPSynchronize && item.getChecked().get())) {
//                    syncftp.add(item);
//                }
//            }
//
//            for (UploadElement item : syncmoodle) {
//                if (item.getChecked().get()) {
//                    //Moodle lookup
//                    if (item.getAction() == MoodleAction.MoodleUpload) {
//                        //Case 2: file not uploaded; uploaded = false
//                        MoodleUploadTemp uploader = new MoodleUploadTemp();
//                        try {
//                            MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
//                            moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), item.getFileNameAsString());
//                        }
//                        catch (Exception e){
//                            logException(e, "Sync failed");
//
//                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
//                                    MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"),item.getPath().getFileName().toString()));
//                        }
//
//                    } else if (item.getAction() == MoodleAction.MoodleSynchronize) {
//                        Module online = modules.get(item.getIfuploaded());
//                        try {
//                            MoodleUploadTemp uploader = new MoodleUploadTemp();
//                            MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/"/* + config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
//                            moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
//                            moodleService.removeResource(config.getMoodleToken(), online.getId());
//                        }
//                        catch (Exception e){
//                            logException(e, "Sync failed");
//
//                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
//                                    "sync.sync.error.upload.message");
//                        }
//                    }
//                }
//            }
//
//            if (syncftp.size() > 0) {
//                    FileServerClientFTP fileClient = new FileServerClientFTP(config);
//                    fileClient.connect();
//                    for (UploadElement item : syncftp) {
//                            if (item.getAction() == MoodleAction.FTPUpload) {
//                                String url = fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
//                                moodleService.setUrl(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), item.getPath().getFileName().toString(), url);
//                            } else if (item.getAction() == MoodleAction.FTPSynchronize) {
//                                //Depends on fileserver -> does the url change?
//                                fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
//                            }
//                    }
//                    fileClient.disconnect();
//            }
//        } catch (Throwable e) {
//            logException(e, "Sync failed");
//
//            showNotification(NotificationType.ERROR, "sync.sync.error.title",
//                    "sync.sync.error.message");
//        }
//        close();
    }

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }
}