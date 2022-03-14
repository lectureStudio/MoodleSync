package moodle.sync.presenter;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.javafx.UploadList;
import moodle.sync.util.FileService;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.UploadElement;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.view.SyncView;
import moodle.sync.web.MoodleUploadTemp;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.MoodleUpload;
import moodle.sync.web.service.MoodleService;
import org.apache.tika.Tika;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.util.ListChangeListener;
import org.lecturestudio.core.util.ObservableList;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;
import org.lecturestudio.web.api.filter.RegexRule;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SyncPresenter extends Presenter<SyncView> {

    private final ViewContextFactory viewFactory;

    private final MoodleSyncConfiguration config;

    private final MoodleService moodleService;

    private List<Module> modules;

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
        prepareSync();
        UploadList uploadList = config.getUploadList();
        List<UploadElement> uploadElements = uploadList.getElements();

        view.setFiles(uploadElements);
        view.setOnSync(this::execute);
    }

    public void prepareSync() {
        try {
            //Create Directory if not existed
            FileService fileService = new FileService();
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName());
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

            List<UploadElement> elements = new ArrayList<>();

            //Every file inside the directory gets checked whether its filename is on Moodle or not and then is handled differently
            for (Path item : fileList) {
                Tika tika = new Tika();
                String mimeType = tika.detect(item);
                System.out.println(mimeType);
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
                            elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.MoodleSynchronize));
                            break;
                        }
                    }
                }
                if(uploaded == false){
                    elements.add(new UploadElement(item, uploaded, ifuploaded, false, MoodleAction.MoodleUpload));
                }
            }
            config.setUploadList(new UploadList(elements));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void execute() {
        try {
            List<UploadElement> sync = view.returnList();

            for (UploadElement item : sync) {
                if (item.getChecked()) {

                    //Case 1: file is uploaded; uploaded = true
                    if (item.isUploaded()) {
                        Module online = modules.get(item.getIfuploaded());
                        //Warum mal 1000?

                        MoodleUploadTemp uploader = new MoodleUploadTemp();
                        MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getPath().getFileName().toString());
                        moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
                        moodleService.removeResource(config.getMoodleToken(), online.getId());

                    }
                    //Case 2: file not uploaded; uploaded = false
                    else {
                        MoodleUploadTemp uploader = new MoodleUploadTemp();
                        MoodleUpload upload = uploader.upload(item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getPath().getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getPath().getFileName().toString());
                        moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename());
                    }
                }
            }
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }
        close();
    }


    private void toggleFile(UploadElement uploadElement) {
        //uploadElement.setExecute(true);
    }

    private void execute(List<UploadElement> uploads) {
        for (int i = 0; i < uploads.size(); i++) {
            System.out.println(uploads.get(i).getPath());
            System.out.println(uploads.get(i).getChecked());
        }
    }

}
