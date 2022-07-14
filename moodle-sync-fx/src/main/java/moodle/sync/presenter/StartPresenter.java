package moodle.sync.presenter;

import javax.inject.Inject;

import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.web.json.*;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class StartPresenter extends Presenter<StartView> {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Providing the content of a course. Used for the section-combobox.
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
        //Initialising all functions of the "start-page" with the help of the configuration.
        String syncPath = config.getSyncRootPath();
        //Check whether a default path should be used to prevent unwanted behavior.
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

        //Display the course-sections after Moodle-course is choosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            view.setSections(sections());
            view.setSection(new ObjectProperty<Section>());
        });
    }

    private void onCourseChanged(Course course) {
        view.setSections(sections());
    }


    /**
     * Execute an API-call to get users Moodle-courses.
     *
     * @return list containing users Moodle-courses.
     */
    private List<Course> courses() {
        String token = config.getMoodleToken();
        String url = config.getMoodleUrl();
        //Security checks to prevent unwanted behaviour.
        if (token == null || token.isEmpty() || token.isBlank() || url == null || url.isEmpty() || url.isBlank()) {
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

        //Do not show Moodle-courses which are already over.
        if (!courses.isEmpty()) {
            courses.removeIf(item -> (item.getEnddate() != 0 && item.getEnddate() < System.currentTimeMillis()));
        }
        return courses;
    }

    /**
     * Execute an API-call to get a choosen Moodle-courses course-sections.
     *
     * @return list containing course-sections.
     */
    private List<Section> sections() {
        Course course = config.getRecentCourse();
        if (course == null) {
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

    /**
     * Starts the sync-process.
     */
    private void onSync() {
        //Serveral security checks to prevent unwanted behaviour.
        if (config.getRecentCourse() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.course.message");
            return;
        } else if (config.getRecentSection() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.section.message");
            return;
        }
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.path.message");
            return;
        }
        try {
            //Open "sync-page".
            context.getEventBus().post(new ShowPresenterCommand<>(SyncPresenter.class));
        } catch (Throwable e) {
            logException(e, "Sync failed");

            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }
    }

    /**
     * Method to update the displayed Moodle-Courses.
     */
    private void updateCourses() {
        view.setCourses(courses());
        view.setSections(sections());
    }

}
