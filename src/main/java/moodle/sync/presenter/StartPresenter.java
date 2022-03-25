package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.web.json.*;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.ChangeListener;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.Observable;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections(sections());
        view.setOnCourseChanged(this::onCourseChanged);

        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
                    view.setSections(sections());
                    view.setSection(new ObjectProperty<Section>());
        });
    }

    private void onCourseChanged(Course course) {
        view.setSections(sections());
    }

    private List<Course> courses() {
        String token = config.getMoodleToken();
        String url = config.getMoodleUrl();
        if(token == null || token.isEmpty() || token.isBlank() || url == null || url.isEmpty() || url.isBlank()){
            List<Course> dummy = new ArrayList<>();
          return dummy;
        }
        List<Course> courses = List.of();
        try {
            courses = moodleService.getEnrolledCourses(config.getMoodleToken(), moodleService.getUserId(config.getMoodleToken()));
        } catch (Exception e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.invalidurl.message");
            config.setRecentCourse(null);
        }
        return courses;
    }

    private List<Section> sections() {
        Course course = config.getRecentCourse();
        if(course == null){
            List<Section> dummy = new ArrayList<>();
            return dummy;
        }
        List<Section> content = moodleService.getCourseContent(config.getMoodleToken(), config.getRecentCourse().getId());
        coursecontent = content;
        return content;
    }

    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::updateCourses));
    }

    private void onExit() {
        context.getEventBus().post(new CloseApplicationCommand());
    }

    private void onSync() {
        if(config.getRecentCourse() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.course.message");
            return;
        }
        else if(config.getRecentSection() == null){
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.section.message");
            return;
        }
            if(Files.isDirectory(Paths.get(config.getSyncRootPath())) == false) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.path.message");
            return;
        }
        try {
            context.getEventBus().post(new ShowPresenterCommand<>(SyncPresenter.class));
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }
    }

    private void updateCourses(){
            view.setCourses(courses());
            view.setSections(sections());
    }

}
