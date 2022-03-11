package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.javafx.UploadList;
import moodle.sync.util.FileService;
import moodle.sync.util.UploadElement;
import moodle.sync.web.MoodleUploadTemp;
import moodle.sync.web.json.*;

import moodle.sync.web.json.Module;
import okhttp3.*;
import org.apache.commons.text.StringEscapeUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.util.FileUtils;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;
import org.lecturestudio.web.api.exception.MatrixUnauthorizedException;
import org.lecturestudio.web.api.service.DLZRoomService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartPresenter extends Presenter<StartView> {

    private final ViewContextFactory viewFactory;

    private final MoodleService moodleService;

    private final MoodleSyncConfiguration config;

    private List<Section> coursecontent;


    @Inject
    StartPresenter(ApplicationContext context, StartView view,
                   ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();

    }

    @Override
    public void initialize() {
        String syncPath = config.getSyncRootPath();
        if (syncPath == null || syncPath.isEmpty() || syncPath.isBlank()) {
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            config.setSyncRootPath(defaultConfiguration.getSyncRootPath());
        }
        view.setOnExit(this::onExit);
        view.setOnSync(this::onSync);
        view.setOnSettings(this::onSettings);
        view.setCourse(course());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections(sections());
        view.setOnCourseChanged(this::onCourseChanged);
    }

    private void onCourseChanged(Course course) {
        view.setSections(sections());
    }

    private ObjectProperty<Course> course() {
        return config.recentCourseProperty();
    }

    private List<Course> courses() {
        List<Course> courses = List.of();
        try {
            courses = moodleService.getEnrolledCourses(config.getMoodleToken(), moodleService.getUserId(config.getMoodleToken()));
        } catch (Exception e) {
            config.setRecentCourse(null);
            handleException(e, "", e.getMessage());
        }
        return courses;
    }

    private List<Section> sections() {
        List<Section> content = moodleService.getCourseContent(config.getMoodleToken(), config.getRecentCourse().getId());
        coursecontent = content;
        return content;
    }

    private void onSettings() {
        context.getEventBus().post(new ShowPresenterCommand<>(SettingsPresenter.class));
    }

    private void onExit() {
        context.getEventBus().post(new CloseApplicationCommand());
    }


    private void onSync() {
        try {
            //Create Directory if not existed
            FileService fileService = new FileService();
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName());
            fileService.directoryManager(execute);

            //Update Recent Secton
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));

            //List containing modules in choosen section
            List<Module> modules = new ArrayList<>();
            for (int i = 0; i < config.getRecentSection().getModules().size(); i++) {
                if (/*config.getRecentSection().getModules().get(i).getModname().equals("url") ||*/ config.getRecentSection().getModules().get(i).getModname().equals("resource")) {
                    modules.add(config.getRecentSection().getModules().get(i));
                }
            }
            //List containing all files in directory
            List<Path> filelist = fileService.getFilesInDirectory(execute);

            List<UploadElement> elements = new ArrayList<>();

            //Every file inside the directory gets checked whether its filename is on Moodle or not and then is handled differently
            for (Path item : filelist) {
                int uploaded = 0;
                int ifuploaded = 0;
                for (int i = 0; i < modules.size(); i++) {
                    if (item.getFileName().toString().equals(modules.get(i).getContents().get(0).getFilename())) {
                        uploaded = 1;
                        ifuploaded = i;
                        break;
                    }
                    uploaded = 2;
                }
                elements.add(new UploadElement(item, uploaded, ifuploaded));
            }

            config.setUploadList(new UploadList(elements));
            context.getEventBus().post(new ShowPresenterCommand<>(SyncPresenter.class));

                /*
                //Case 1: file is uploaded; uploaded = true
                if (uploaded == 1) {
                    Module online = modules.get(ifuploaded);
                    //Warum mal 1000?
                    Long onlinemodified = online.getContents().get(0).getTimemodified() * 1000;
                    Long filemodified = Files.readAttributes(item, BasicFileAttributes.class).lastModifiedTime().toMillis();
                    if (filemodified > onlinemodified) {
                        Scanner eingabewert = new Scanner(System.in);

                        System.out.print("Aktualisierte Datei " + item.getFileName().toString() + " soll diese aktualisiert werden?");
                        int L = eingabewert.nextInt();
                        if (L == 1) {
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            MoodleUpload upload = uploader.upload(item.getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getFileName().toString());
                            moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename(), online.getId());
                            moodleService.removeResource(config.getMoodleToken(), online.getId());
                        }
                    }
                }
                //Case 2: file not uploaded; uploaded = false
                else if (uploaded == 2) {
                    MoodleUploadTemp uploader = new MoodleUploadTemp();
                    MoodleUpload upload = uploader.upload(item.getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getFileName().toString(), config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + config.recentSectionProperty().get().getName() + "/" + item.getFileName().toString());
                    moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getSection(), upload.getItemid(), upload.getFilename());
                }*/

        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }
    }

}
