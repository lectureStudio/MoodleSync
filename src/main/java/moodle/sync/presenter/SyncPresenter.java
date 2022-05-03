package moodle.sync.presenter;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.fileserver.FileServerClientFTP;
import moodle.sync.fileserver.FileServerFile;
import moodle.sync.util.FileService;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.UploadData.UploadFolderElement;
import moodle.sync.view.SyncView;
import moodle.sync.web.service.MoodleUploadTemp;
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

/**
 * Class defining the logic of the "sync-page". The sync-process is prepared and executed inside this class.
 *
 * @author Daniel Schröter
 */
public class SyncPresenter extends Presenter<SyncView> {

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Course-modules of chooses Moodle-Course.
    private List<Module> modules;

    //Directories inside the choosen director.
    private List<Module> directories;

    //Content of the course-directory.
    private List<UploadData> uploadElements;

    //Already uploaded files on the fileserver.
    private List<FileServerFile> files = List.of();

    //Client used for communication with the fileserver.
    private FileServerClientFTP fileClient;

    @Inject
    SyncPresenter(ApplicationContext context, SyncView view, MoodleService moodleService) {
        super(context, view);

        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.moodleService = moodleService;
    }

    @Override
    public void initialize() {
        //Initialising all functions of the "sync-page" .
        uploadElements = prepareSync();

        view.setFiles(uploadElements);
        view.setOnSync(this::execute);
        view.setOnClose(this::close);
    }

    /**
     * Method used to prepare the sync-table.
     *
     * @return list containing UploadData which could be displayed in the sync-table.
     */
    public List<UploadData> prepareSync() {
        List<UploadData> fileList = new ArrayList<>();
        try {
            //Update recent course-section.
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));

            //Create Directory if not existed
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
            FileService.directoryManager(execute);

            //List containing all files in directory
            fileList = FileService.getFilesInDirectory(execute);

            //Check whether a fileserver is required depending on fileformats inside the local diretory.
            List<Path> pathList = FileService.fileServerRequired(execute);
            boolean fileServerRequired = false;

            for (Path file : pathList) {
                if (contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(file.getFileName().toString())) && !Files.isDirectory(file)) {
                    fileServerRequired = true;
                }
            }

            //If needed, establish connection to the fileserver and gathering all files regarding to the Moodle-course. Close connection afterwars.
            if (fileServerRequired) {
                String url = config.getFileserver();
                String user = config.getUserFileserver();
                String password = config.getPasswordFileserver();
                if (url == null || url.isEmpty() || url.isBlank() || user == null || user.isEmpty() || user.isBlank() || password == null || password.isEmpty() || password.isBlank()) {
                    showNotification(NotificationType.ERROR, "sync.sync.error.title",
                            "sync.sync.error.fileserver1.message");
                } else {
                    try {
                        fileClient = new FileServerClientFTP(config);
                        fileClient.connect();
                        files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> If there should be support for different upload-sections.
                        fileClient.disconnect();
                    } catch (Exception e) {
                        logException(e, "Sync failed");
                        showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                "sync.sync.error.fileserver2.message");

                    }
                }
            }

            //Gather all needed course-modules inside the Moodle-course.
            modules = FileService.getModulesByType("resource", config.getRecentSection());

            directories = FileService.getModulesByType("folder", config.getRecentSection());
            if (!directories.isEmpty()) {
                for (Module dir : directories) {
                    List<Content> dircon = dir.getContents();
                    for (Content con : dircon) {
                        List<Content> content = new ArrayList<>();
                        content.add(con);
                        modules.add(new Module(content));
                    }
                }
            }

            //manipulate the list containing elements of the type UploadData representing the local files based on gathered information.
            checkUploadData(fileList);

        } catch (Throwable e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                    "sync.sync.error.message");
        }
        return fileList;
    }

    /**
     * Method manipulate the list containing elements of the type UploadData representing the local files. Formats and already uploaded files are inspected.
     *
     * @param data list to manipulate.
     * @throws IOException
     */
    private void checkUploadData(List<UploadData> data) throws IOException {
        //List to remove not needed elements afterwards.
        List<UploadData> remove = new ArrayList<>();
        //Each element is handled individually.
        for (UploadData item : data) {
            //Recursive method-call if UplaodData is element of UploadFolderElement.
            if (item instanceof UploadFolderElement) {
                checkUploadData(((UploadFolderElement) item).getContent());
            } else if (item instanceof UploadElement) {
                Path path = item.getPath();
                //Treatment defined by fileformat.
                if (contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(path.getFileName().toString()))) {
                    UploadElement temp = FileService.CheckFileServerModule(files, path);
                    if (temp != null) {
                        ((UploadElement) item).setUploaded(temp.isUploaded());
                        ((UploadElement) item).setIfuploaded(temp.getIfuploaded());
                        ((UploadElement) item).setChecked(temp.getChecked().get());
                        ((UploadElement) item).setAction(temp.getAction());
                        ((UploadElement) item).setSelectable(temp.getSelectable());
                        ((UploadElement) item).setFileName(path.getFileName().toString());
                    } else {
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
                    } else {
                        remove.add(item);
                    }
                }
                //Case: type not found
                else {
                    if (config.showUnknownFormatsProperty().get()) {
                        ((UploadElement) item).setUploaded(false);
                        ((UploadElement) item).setIfuploaded(0);
                        ((UploadElement) item).setChecked(false);
                        ((UploadElement) item).setAction(MoodleAction.DatatypeNotKnown);
                        ((UploadElement) item).setSelectable(false);
                        ((UploadElement) item).setFileName(path.getFileName().toString());
                    } else {
                        remove.add(item);
                    }
                }
            }
        }
        //Removal of already uploaded data.
        for (UploadData rem : remove) {
            data.remove(rem);
        }

        //Removal of empty directories.
        List<UploadData> removeDirectories = new ArrayList<>();
        for (UploadData file : data) {
            if (file instanceof UploadFolderElement) {
                if (((UploadFolderElement) file).getContent().isEmpty()) {
                    removeDirectories.add(file);
                }
            }
        }
        for (UploadData rem : removeDirectories) {
            data.remove(rem);
        }
    }

    /**
     * Executes the synchronisation-process.
     */
    private void execute() {
        try {
            //Check if a fileserver is needed.
            List<UploadData> sync = uploadElements;
            boolean fileServerRequired = false;
            for (UploadData item : sync) {
                if (item instanceof UploadElement) {
                    if ((((UploadElement) item).getAction() == MoodleAction.FTPUpload && ((UploadElement) item).getChecked().get()) || (((UploadElement) item).getAction() == MoodleAction.FTPSynchronize && ((UploadElement) item).getChecked().get())) {
                        fileServerRequired = true;
                        break;
                    }
                }
            }
            //If needed connect to it.
            if (fileServerRequired) {
                fileClient.connect();
            }

            //Call of methods to execute the individual synchronisation.
            for (UploadData item : sync) {
                if (item instanceof UploadElement) {
                    UploadHandler((UploadElement) item);
                } else if (item instanceof UploadFolderElement) {
                    UploadHandlerFolder((UploadFolderElement) item);
                }
            }
            if (fileServerRequired) {
                fileClient.disconnect();
            }
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                    "sync.sync.error.message");
        }
        close();
    }


    /**
     * Executes the synchronisation of a single file.
     *
     * @param item file to synchronize.
     */
    private void UploadHandler(UploadElement item) {
        //Individual treatment based of the MoodleAction. Item must be checked.
        if (item.getAction() == MoodleAction.MoodleUpload && item.getChecked().get()) {
            MoodleUploadTemp uploader = new MoodleUploadTemp();
            try {
                //Upload of the file to the Moodle-platform.
                MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                //Publish it in the Moodle-course.
                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), item.getFileNameAsString());
            } catch (Exception e) {
                logException(e, "Sync failed");

                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"), item.getPath().getFileName().toString()));
            }
        } else if (item.getAction() == MoodleAction.MoodleSynchronize && item.getChecked().get()) {
            Module online = modules.get(item.getIfuploaded());
            try {
                MoodleUploadTemp uploader = new MoodleUploadTemp();
                //Upload of the new file to the Moodle-platform.
                MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/"/* + config.recentSectionProperty().get().getName() + "/" */ + item.getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                //Publish it in the Moodle-course above the old course-module containing the old file.
                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
                //Removal of the old course-module.
                moodleService.removeResource(config.getMoodleToken(), online.getId());
            } catch (Exception e) {
                logException(e, "Sync failed");

                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        "sync.sync.error.upload.message");
            }
        } else if (item.getAction() == MoodleAction.FTPUpload && item.getChecked().get()) {
            //Fileserver upload.
            String url = fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
            //Publish the new Url in the Moodle-course.
            moodleService.setUrl(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), item.getPath().getFileName().toString(), url);
        } else if ((item.getAction() == MoodleAction.FTPSynchronize && item.getChecked().get())) {
            //Update the file on the fileserver.
            fileClient.uploadFile(item, config.getRecentCourse().getDisplayname());
        }
    }

    /**
     * Method which executes the synchronization of directories with the Moodle-Platform.
     *
     * @param item UploadFolderElement to synchronize.
     * @throws IOException
     */
    private void UploadHandlerFolder(UploadFolderElement item) throws IOException {
        //Check if a file inside the element should be synchronized.
        boolean sync = item.getChecked().get();

        //Check if the directory should be newly created or updated.
        boolean isnew = true;
        for (Module dir : directories) {
            if (item.getPath().getFileName().toString().equals(dir.getName())) {
                item.setIfuploaded(dir.getId());
                isnew = false;
            }
        }


        //Create list of UploadData with files which correspond to the supported fileformats.
        List<UploadData> filesInDirectory = FileService.getFilesInDirectory(item.getPath());
        List<UploadData> filesAsContent = new ArrayList<UploadData>();
        for (UploadData itemInDirectory : filesInDirectory) {
            if (contains(config.getFormatsMoodle().split(","), FilenameUtils.getExtension(itemInDirectory.getPath().getFileName().toString()))) {
                filesAsContent.add(itemInDirectory);
            }
        }
        item.setContent(filesAsContent);
        //If directory should be newly created.
        if (sync && isnew) {
            try {
                MoodleUploadTemp uploader = new MoodleUploadTemp();
                //Upload all files in same area.
                MoodleUpload upload = uploader.upload(item.getContent().get(0).getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString() + "/" + item.getContent().get(0).getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                if (item.getContent().size() > 1) {
                    for (int i = 1; i < item.getContent().size(); i++) {
                        uploader.upload(item.getContent().get(i).getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString() + "/" + item.getContent().get(i).getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken() + "&itemid=" + upload.getItemid());
                    }
                }
                //Publish data by creating the course-module.
                moodleService.setFolder(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), item.getPath().getFileName().toString());
            } catch (Exception e) {
                logException(e, "Sync failed");

                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"), item.getPath().getFileName().toString()));
            }
        }
        //If directory should be updated.
        if (sync && !isnew) {
            try {
                MoodleUploadTemp uploader = new MoodleUploadTemp();
                //Upload all files in same area
                MoodleUpload upload = uploader.upload(item.getContent().get(0).getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString() + "/" + item.getContent().get(0).getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken());
                if (item.getContent().size() > 1) {
                    for (int i = 1; i < item.getContent().size(); i++) {
                        uploader.upload(item.getContent().get(i).getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" /*+ config.recentSectionProperty().get().getName() + "/"*/ + item.getPath().getFileName().toString() + "/" + item.getContent().get(i).getPath().getFileName().toString(), config.getMoodleUrl(), config.getMoodleToken() + "&itemid=" + upload.getItemid());
                    }
                }
                //Publish data by creating the course-module above old course-module.
                moodleService.setFolder(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), item.getPath().getFileName().toString(), item.getIfuploaded());
                //Remove old course-module.
                moodleService.removeResource(config.getMoodleToken(), item.getIfuploaded());
            } catch (Exception e) {
                logException(e, "Sync failed");

                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"), item.getPath().getFileName().toString()));
            }


        }
    }

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }
}