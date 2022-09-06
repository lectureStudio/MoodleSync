package moodle.sync.presenter;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import moodle.sync.config.DefaultConfiguration;
import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.util.FileService;
import moodle.sync.util.FileWatcherService.FileEvent;
import moodle.sync.util.FileWatcherService.FileListener;
import moodle.sync.util.FileWatcherService.FileWatcher;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.TimeDateElement;
import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.syncTableElement;
import moodle.sync.web.json.*;

import moodle.sync.web.json.Module;
import moodle.sync.web.service.MoodleUploadTemp;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.Observable;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.CloseApplicationCommand;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.web.service.MoodleService;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class StartPresenter extends Presenter<StartView> implements FileListener {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Providing the content of a course. Used for the section-combobox.
    private List<Section> courseContent;

    private ObservableList<syncTableElement> courseData;

    private FileWatcher watcher;

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

        view.setOnSync(this::onSync);
        view.setOnSettings(this::onSettings);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections( sections());
        view.setOnCourseChanged(this::onCourseChanged);
        view.setData(setData());


        //Display the course-sections after Moodle-course is choosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            config.setRecentSection(null);
            view.setSections(sections());
            view.setSection(new ObjectProperty<Section>());
            view.setData(setData());
        });

        config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
            view.setData(setData());
        });

        config.moodleUrlProperty().addListener((observable, oldUrl, newUrl) -> {
            config.setRecentCourse(null);
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
        /*if (!courses.isEmpty()) { TODO: alte Kurse ausblenden
            courses.removeIf(item -> (item.getEnddate() != 0 && item.getEnddate() < System.currentTimeMillis()));
        }*/
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
        try{
            List<Section> content = moodleService.getCourseContent(config.getMoodleToken(), config.getRecentCourse().getId());
            courseContent = content;
            return content;
        } catch (Exception e){

        }
        List<Section> content = new ArrayList<>();;
        return content;
    }

    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::updateCourses));
    }


    /**
     * Starts the sync-process.
     */
    private void onSync(){
        //Serveral security checks to prevent unwanted behaviour.
        if (config.getRecentCourse() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.course.message");
            return;
        }
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.path.message");
            return;
        }
        for (syncTableElement courseData : courseData) {
            if(courseData.isSelected()){
                if(courseData.getModuleType().equals("resource")){
                    if(courseData.getAction() == MoodleAction.MoodleUpload){
                        try {
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            //Upload of the file to the Moodle-platform.
                            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), config.getMoodleUrl(), config.getMoodleToken());
                            //Publish it in the Moodle-course.
                            if(courseData.getUnixTimeStamp() > System.currentTimeMillis()/1000L){
                                //Time in future
                                if(courseData.getVisible()){
                                    moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), courseData.getUnixTimeStamp(), true ,courseData.getModuleName(), courseData.getBeforemod());
                                } else {
                                    moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), courseData.getUnixTimeStamp(), false ,courseData.getModuleName(), courseData.getBeforemod());
                                }
                            }
                            else {
                                //Time not modified, time should be null
                                if(courseData.getVisible()){
                                    moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), null, true ,courseData.getModuleName(), courseData.getBeforemod());
                                } else {
                                    moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), null, false ,courseData.getModuleName(), courseData.getBeforemod());
                                }
                            }
                        } catch (Exception e) {
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                    MessageFormat.format(context.getDictionary().get("sync.sync.error.upload.message"), courseData.getModuleName()));
                        }
                    }
                    else if(courseData.getAction() == MoodleAction.MoodleSynchronize){
                        try{
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            //Upload of the new file to the Moodle-platform.
                            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), config.getMoodleUrl(), config.getMoodleToken());
                            //Publish it in the Moodle-course above the old course-module containing the old file.
                            if(courseData.getUnixTimeStamp() > System.currentTimeMillis()/1000L){
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            } else {
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), null, courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            }
                            //Removal of the old course-module.
                            moodleService.removeResource(config.getMoodleToken(), courseData.getCmid());
                        } catch (Exception e) {
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "sync.sync.error.title",
                                    "sync.sync.error.upload.message");
                        }
                    }
                }
            }
        }
        updateCourses();
    }

    /**
     * Method to update the displayed Moodle-Courses.
     */
    private void updateCourses() {
        view.setCourses(courses());
        view.setSections(sections());
        view.setData(setData());
    }

    private ObservableList<syncTableElement> setData() {
        if(isNull(courseContent) || isNull(config.recentCourseProperty().get())){
            ObservableList<syncTableElement> dummy = FXCollections.observableArrayList();
            return dummy;
        }
        /*if(courseData == null){
            ObservableList<syncTableElement> dummy = FXCollections.observableArrayList();
            return dummy;
        }*/
        try {
            if (watcher != null) {
                watcher.close();
            }
        }catch (Exception e){
        }

        if(!isNull(config.getRecentSection())){
            courseContent.clear();
            courseContent.add(config.getRecentSection());
        }

        List<Path> sectionList = List.of();

        try {
            if(isNull(config.getRecentSection())){
                //Zur Prüfung neuer Ordner, um sie hinzuzufügen
                Path sections = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
                FileService.directoryManager(sections);
                sectionList = FileService.getPathsInDirectory(sections);
            }
        }catch (Exception e){

        }


        ObservableList<syncTableElement> data = FXCollections.observableArrayList();
        for(Section section : courseContent){
            int sectionId = section.getSection();
            data.add(new syncTableElement(section.getName(), section.getId(), sectionId, "section",false, false, null, section.getVisible() == 1 ? true : false));
            int remove = -1;
            for(int i = 0; i < sectionList.size(); i++){
                if(sectionList.get(i).getFileName().toString().equals(section.getName())){
                    remove = i;
                    break;
                }
            }
            if(remove != -1){
                sectionList.remove(remove);
            }
            //Datenfpad zu Sektionsordner, wird erstellt falls nicht vorhanden
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + section.getName());
            FileService.directoryManager(execute);

            //Alle Dateien auslesen
            try {
                List<Path> fileList = FileService.getPathsInDirectory(execute);

            for(Module module : section.getModules()){
                if(module.getModname().equals("resource")){
                    boolean found = false;
                    for(int i = 0; i < fileList.size(); i++){
                        if(fileList.get(i).getFileName().toString().equals(module.getContents().get(0).getFilename())){

                            long onlinemodified = module.getContents().get(0).getTimemodified() * 1000;
                            long filemodified = Files.getLastModifiedTime(fileList.get(i)).toMillis();
                            //Check if local file is newer.
                            if (filemodified > onlinemodified) {
                                found = true;
                                if(module.getAvailability() != null){
                                    var JsonB = new JsonConfigProvider().getContext(null);
                                    JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                    LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT()*1000L), ZoneId.systemDefault());
                                    data.add(new syncTableElement(module.getName(), module.getId(), sectionId,module.getModname(), fileList.get(i), true,false, MoodleAction.MoodleSynchronize, getPriorityVisiblity(module.getVisible() == 1 ? true : false, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime())));
                                } else{
                                    data.add(new syncTableElement(module.getName(), module.getId(), sectionId,module.getModname(), fileList.get(i), true,false, MoodleAction.MoodleSynchronize, module.getVisible() == 1 ? true : false));
                                }
                                fileList.remove(i);
                                break;
                            } else {
                                found = true;
                                if(module.getAvailability() != null){
                                    var JsonB = new JsonConfigProvider().getContext(null);
                                    JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                    LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT()*1000L), ZoneId.systemDefault());
                                    data.add(new syncTableElement(module.getName(), module.getId(), sectionId, module.getModname(), fileList.get(i), false, false, MoodleAction.ExistingFile, getPriorityVisiblity(module.getVisible() == 1 ? true : false, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime())));
                                }
                                else {
                                    data.add(new syncTableElement(module.getName(), module.getId(), sectionId, module.getModname(), fileList.get(i), false, false, MoodleAction.ExistingFile, module.getVisible() == 1 ? true : false));
                                }
                                    fileList.remove(i);
                                break;
                            }
                        }
                    }
                    if(!found){
                        data.add(new syncTableElement(module.getName(), module.getId(), sectionId,module.getModname(), false,false, MoodleAction.NotLocalFile, module.getVisible() == 1 ? true : false));
                    }
                } else {
                    //Das sind die die nicht "resource" sind
                    data.add(new syncTableElement(module.getName(), module.getId(), sectionId,module.getModname(), false,false, MoodleAction.DatatypeNotKnown, module.getVisible() == 1 ? true : false));
                }
            }

            if(fileList.size() != 0){
                for (Path path : fileList){
                    data.add(new syncTableElement(path.getFileName().toString(), -1, sectionId,"resource", path, true,false, MoodleAction.MoodleUpload, true));
                }
            }
            } catch (Throwable e) {
                logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "sync.sync.error.title",
                        "sync.sync.error.message");
            }
        }

        if(!sectionList.isEmpty()){
            for(Path elem : sectionList){
                data.add(new syncTableElement(elem.getFileName().toString(), -1, -1, "section", elem,true, false, MoodleAction.UploadSection, true));
            }
        }
        courseData = data;

        //FileWatcher aktivieren
        watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname()));
        watcher.addListener(this);
        watcher.watch();

        return data;
    }

    @Override
    public void onCreated(FileEvent event) {
        System.out.println("Erstellt" + event.getFile().toString());
        event.getFile();
    }

    @Override
    public void onModified(FileEvent event) {
        System.out.println("Geändert");
    }

    @Override
    public void onDeleted(FileEvent event) {
        System.out.println("Gelöscht");
    }

    private Boolean getPriorityVisiblity(Boolean visible, Boolean availability){
        if(!visible || !availability) {
            return false;
        }
        return true;
    }
}
