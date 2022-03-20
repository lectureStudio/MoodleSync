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
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SyncPresenter extends Presenter<SyncView> {

    private final ViewContextFactory viewFactory;

    private final MoodleSyncConfiguration config;

    private final MoodleService moodleService;

    private List<Module> modules;

    private List<UploadElement> uploadElements;


    @Inject
    SyncPresenter(ApplicationContext context, SyncView view,
                  ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.viewFactory = viewFactory;
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
            //Create Directory if not existed
            FileService fileService = new FileService();
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
            fileService.directoryManager(execute);

            //Update Recent Secton
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));

            //List containing resource modules in choosen section
            modules = new ArrayList<>();
            for (int i = 0; i < config.getRecentSection().getModules().size(); i++) {
                if (/*config.getRecentSection().getModules().get(i).getModname().equals("url") ||*/ config.getRecentSection().getModules().get(i).getModname().equals("resource")) {
                    modules.add(config.getRecentSection().getModules().get(i));
                }
            }
            //List containing all files in directory
            List<Path> fileList = fileService.getFilesInDirectory(execute);

            //Sort by filetype
            List<Path> fileListMoodle = new ArrayList<>();
            List<Path> fileListFileserver = new ArrayList<>();
            List<Path> fileTypeNotFound = new ArrayList<>();

            for(Path file : fileList){
                if(contains(config.getFormatsMoodle().split(","), FilenameUtils.getExtension(file.getFileName().toString()))){
                    fileListMoodle.add(file);
                }
                else if(contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(file.getFileName().toString()))){
                    fileListFileserver.add(file);
                }
                else{
                    fileTypeNotFound.add(file);
                }
            }

            //Handling for Filetype "upload to moodle"
            //Every file inside the directory gets checked whether its filename is on Moodle or not and then is handled differently
            for (Path item : fileListMoodle) {
                boolean uploaded = false;
                int ifuploaded = 0;
                for (int i = 0; i < modules.size(); i++) {
                    if (item.getFileName().toString().equals(modules.get(i).getContents().get(0).getFilename())) {
                        uploaded = true;
                        ifuploaded = i;
                        Module online = modules.get(i);
                        Long onlinemodified = online.getContents().get(0).getTimemodified() * 1000;
                        Long filemodified = Files.getLastModifiedTime(item).toMillis();
                        if (filemodified > onlinemodified) {
                            elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.MoodleSynchronize, true));
                            break;
                        }
                    }
                }
                if(uploaded == false){
                    elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.MoodleUpload, true));
                }
            }

            if(fileListFileserver.size() != 0) {
                List<FileServerFile> files = null;
                try {
                    FileServerClientFTP fileClient = new FileServerClientFTP(config);

                    fileClient.connect();

                    files = fileClient.getFiles(/*config.getRecentSection().getName()*/ "");

                    fileClient.disconnect();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                //Handling for filetype fileListServer -> already uploaded
                for (Path item : fileListFileserver) {
                    boolean uploaded = false;
                    int ifuploaded = 0;
                    for(int i = 0; i < files.size(); i++){
                        if(item.getFileName().toString().equals(files.get(i).getFilename())) {
                            uploaded = true;
                            ifuploaded = i;
                            Long filemodified = Files.getLastModifiedTime(item).toMillis();
                            Long onlinemodified = files.get(i).getLastTimeModified();
                            if (filemodified > onlinemodified) {
                                elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.FTPSynchronize, true));
                                break;
                            }
                        }
                    }
                    if(uploaded == false) {
                        elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.FTPUpload, true));
                    }
                }
            }

            //Handling for filetype "filetypenotfound"
            for (Path item : fileTypeNotFound) {
                boolean uploaded = false;
                int ifuploaded = 0;
                elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.DatatypeNotKnown, false));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return elements;
    }

    private void execute() {
        try {
            List<UploadElement> sync = uploadElements;
            List<UploadElement> syncmoodle = new ArrayList<>();
            List<UploadElement> syncftp = new ArrayList<>();

            for(UploadElement item : sync){
                if(item.getAction() == MoodleAction.MoodleUpload || item.getAction() == MoodleAction.MoodleSynchronize){
                    syncmoodle.add(item);
                }
                else if(item.getAction() == MoodleAction.FTPUpload || item.getAction() == MoodleAction.FTPSynchronize){
                    syncftp.add(item);
                }
            }

            for (UploadElement item : syncmoodle) {
                if (item.getChecked().get()) {
                    //Moodle lookup
                    if (item.getAction() == MoodleAction.MoodleUpload) {
                        //Case 2: file not uploaded; uploaded = false
                        MoodleUploadTemp uploader = new MoodleUploadTemp();
                        MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/"/* + config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getMoodleToken());
                        moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename());
                    } else if (item.getAction() == MoodleAction.MoodleSynchronize) {
                        Module online = modules.get(item.getIfuploaded());
                        //Warum mal 1000? -> erklären

                        MoodleUploadTemp uploader = new MoodleUploadTemp();
                        MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/"/* + config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getMoodleToken());
                        moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
                        moodleService.removeResource(config.getMoodleToken(), online.getId());
                    }
                }
            }
                if(syncftp.size() > 0) {
                    try {
                        FileServerClientFTP fileClient = new FileServerClientFTP(config);

                        fileClient.connect();


                        for (UploadElement item : syncftp) {
                            if (item.getChecked().get()) {
                                if (item.getAction() == MoodleAction.FTPUpload || item.getAction() == MoodleAction.FTPSynchronize) {
                                    fileClient.uploadFile(item, config.getRecentSection().getName());
                                }
                            }
                        }
                        fileClient.disconnect();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }
        close();
    }
    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }


}
