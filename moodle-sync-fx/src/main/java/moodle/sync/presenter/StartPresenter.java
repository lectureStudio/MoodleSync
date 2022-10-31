package moodle.sync.presenter;

import javax.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.model.json.*;
import moodle.sync.core.fileserver.FileServerClientFTP;
import moodle.sync.core.fileserver.FileServerFile;
import moodle.sync.core.model.json.Module;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.core.util.FileService;
import moodle.sync.core.util.FileWatcherService.FileEvent;
import moodle.sync.core.util.FileWatcherService.FileListener;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.model.TimeDateElement;
import moodle.sync.core.model.syncTableElement;

import moodle.sync.core.web.service.MoodleUploadTemp;
import org.apache.commons.io.FilenameUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.core.web.service.MoodleService;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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

    private boolean fileServerRequired;

    private FileServerClientFTP fileClient;

    private BooleanProperty selectAll;

    @Inject
    StartPresenter(ApplicationContext context, StartView view,
                   ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.selectAll = new BooleanProperty(false);
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

        view.setOnUpdate(this::updateCourses);
        view.setOnSync(this::onSync);
        view.setOnSettings(this::onSettings);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections(sections());
        view.setOnCourseChanged(this::onCourseChanged);
        view.setData(setData());
        view.setOnFolder(this::openCourseDirectory);
        view.setSelectAll(selectAll);


        //Display the course-sections after Moodle-course is choosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            config.setRecentSection(null);
            view.setData(setData());
        });

        config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
            view.setData(setData());
        });

        config.moodleUrlProperty().addListener((observable, oldUrl, newUrl) -> {
            config.setRecentCourse(null);
        });

        selectAll.addListener((observable, oldUrl, newUrl) -> {
            if(newUrl){
                for(syncTableElement elem : courseData){
                    if(elem.isSelectable()){
                        elem.selectedProperty().setValue(true);
                    }
                }
            } else {
                for(syncTableElement elem : courseData){
                    if(elem.isSelectable()){
                        elem.selectedProperty().setValue(false);
                    }
                }
            }
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
            courses.removeIf(item -> (item.getEnddate() != 0 && (item.getEnddate() < System.currentTimeMillis()/1000)));
        }
        //Sort Courses if Possible
        /*if(courses.get(0).getShortname().contains("SoSe") || courses.get(0).getShortname().contains("WiSe")){
            if(courses.get(0).getShortname().contains("20")){

            }
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
        try {
            List<Section> content = moodleService.getCourseContent(config.getMoodleToken(), config.getRecentCourse().getId());
            content.add(0, new Section(-2, this.context.getDictionary().get("start.sync.showall"), 1, "alle", -1, -1, -1, true, null));
            courseContent = content;
            return content;
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::updateCourses));
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
        }
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.path.message");
            return;
        }
        for (syncTableElement courseData : courseData) {
            if (courseData.isSelected()) {
                if (courseData.getModuleType().equals("resource")) {
                    if (courseData.getAction() == MoodleAction.MoodleUpload) {
                        try {
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            //Upload of the file to the Moodle-platform.
                            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), config.getMoodleUrl(), config.getMoodleToken());
                            //Publish it in the Moodle-course.
                            if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                                //Time in future
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            } else {
                                //Time not modified, time should be null
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), null, courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            }
                        } catch (Exception e) {
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "start.sync.error.title",
                                    MessageFormat.format(context.getDictionary().get("start.sync.error.upload.message"), courseData.getModuleName()));
                        }
                    } else if (courseData.getAction() == MoodleAction.MoodleSynchronize) {
                        try {
                            MoodleUploadTemp uploader = new MoodleUploadTemp();
                            //Upload of the new file to the Moodle-platform.
                            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), config.getMoodleUrl(), config.getMoodleToken());
                            //Publish it in the Moodle-course above the old course-module containing the old file.
                            if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            } else {
                                moodleService.setResource(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), upload.getItemid(), null, courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
                            }
                            //Removal of the old course-module.
                            moodleService.removeResource(config.getMoodleToken(), courseData.getCmid());
                        } catch (Exception e) {
                            logException(e, "Sync failed");

                            showNotification(NotificationType.ERROR, "start.sync.error.title",
                                    "start.sync.error.upload.message");
                        }
                    } else {
                        moodleService.setMoveModule(config.getMoodleToken(), courseData.getCmid(), courseData.getSectionId(), courseData.getBeforemod());
                    }
                } else if (courseData.getAction() == MoodleAction.FTPUpload) {
                    //url = fileservice.....
                    String url = "https://wikipedia.org";
                    if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                        moodleService.setUrl(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), courseData.getModuleName(), url, courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getBeforemod());
                    } else {
                        moodleService.setUrl(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getSection(), courseData.getModuleName(), url, null, courseData.getVisible(), courseData.getBeforemod());
                    }
                } else {
                    if(courseData.getAction() != MoodleAction.UploadSection) moodleService.setMoveModule(config.getMoodleToken(), courseData.getCmid(), courseData.getSectionId(), courseData.getBeforemod());
                }
            }
        }
        for (syncTableElement courseData : courseData) {
            if (courseData.getAction() == MoodleAction.UploadSection && courseData.isSelected()) {
                //Logic for Section-Upload
                try {
                    moodleService.setSection(config.getMoodleToken(), config.getRecentCourse().getId(), courseData.getModuleName(), courseData.getSection());
                    if (!courseData.getExistingFileName().split("_", 2)[0].matches("\\d+")) {
                        System.out.println(courseData.getExistingFile());
                        File temp = new File(courseData.getExistingFile());
                        temp.renameTo(new File(Path.of(courseData.getExistingFile()).getParent().toString() + "/" + courseData.getSection() + "_" + courseData.getExistingFileName()));
                    }
                } catch (Exception e) {
                    logException(e, "Sync failed");

                    showNotification(NotificationType.ERROR, "start.sync.error.title",
                            "start.sync.error.upload.message");
                }
            }
        }
        updateCourses();
    }

    /**
     * Method to update the displayed Moodle-Courses.
     */
    private void updateCourses() {
        if(!isNull(config.getRecentSection()) && config.getRecentSection().getId() != -2){
            config.setRecentSection(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));
        }
        view.setData(setData());
    }

    private ObservableList<syncTableElement> setData() {
        if (isNull(courseContent) || isNull(config.recentCourseProperty().get())) {
            ObservableList<syncTableElement> dummy = FXCollections.observableArrayList();
            return dummy;
        }

        try {
            if (watcher != null) {
                watcher.close();
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }

        if (!isNull(config.getRecentSection()) && config.getRecentSection().getId() != -2) {
            courseContent.clear();
            courseContent.add(moodleService.getCourseContentSection(config.getMoodleToken(), config.getRecentCourse().getId(), config.getRecentSection().getId()).get(0));
        }

        List<Path> sectionList = List.of();

        try {
            if (isNull(config.getRecentSection()) || config.getRecentSection().getId() == -2) {
                //Zur Prüfung neuer Ordner, um sie hinzuzufügen
                Path sections = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname());
                FileService.directoryManager(sections);
                sectionList = FileService.getPathsInDirectory(sections);
                courseContent = sections();
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.message");
        }

        ObservableList<syncTableElement> data = FXCollections.observableArrayList();

        for (Section section : courseContent) {
            if (section.getId() != -2) {
            int sectionnum = section.getSection();
            int sectionId = section.getId();
            data.add(new syncTableElement(section.getName(), section.getId(), sectionnum, sectionId, data.size(), section.getSummary(), false, false, MoodleAction.ExistingSection, section.getVisible() == 1));
            int remove = -1;
            for (int i = 0; i < sectionList.size(); i++) {
                String[] check = sectionList.get(i).getFileName().toString().split("_", 2);
                if (check.length > 1 && check[1].equals(section.getName())) {
                    if(!check[0].equals(section.getSection().toString())){
                        File temp = new File(sectionList.get(i).toString());
                        temp.renameTo(new File((sectionList.get(i).getParent().toString() + "/" + section.getSection() + "_" + section.getName())));
                    }
                    remove = i;
                    break;
                }
            }
            //Element der Sektion wird vom Stapel gelöscht
            if (remove != -1) {
                sectionList.remove(remove);
            }
            //Datenfpad zu Sektionsordner, wird erstellt falls nicht vorhanden
            Path execute = Paths.get(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname() + "/" + section.getSection() + "_" + section.getName());
            FileService.directoryManager(execute);

            //initalisierung der fileServerRequired Variable
            fileServerRequired = false;
            List<FileServerFile> files = List.of();

            watcher = new FileWatcher(new File(execute.toString()));
            watcher.addListener(this);
            watcher.watch();

            //Alle Dateien auslesen
            try {
                List<Path> fileList = FileService.getPathsInDirectory(execute);
                for (Module module : section.getModules()) {
                    if (module.getModname().equals("resource")) {
                        boolean found = false;
                        for (int i = 0; i < fileList.size(); i++) {
                            if (fileList.get(i).getFileName().toString().equals(module.getContents().get(0).getFilename())) {
                                long onlinemodified = module.getContents().get(0).getTimemodified() * 1000;
                                long filemodified = Files.getLastModifiedTime(fileList.get(i)).toMillis();
                                //Check if local file is newer.
                                if (filemodified > onlinemodified) {
                                    found = true;
                                    if (module.getAvailability() != null) {
                                        var JsonB = new JsonConfigProvider().getContext(null);
                                        JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                                        data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size() ,module.getModname(), fileList.get(i), true, false, MoodleAction.MoodleSynchronize, getPriorityVisiblity(module.getVisible() == 1, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime()), module.getId()));
                                    } else {
                                        data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), fileList.get(i), true, false, MoodleAction.MoodleSynchronize, module.getVisible() == 1, module.getId()));
                                    }
                                    fileList.remove(i);
                                    break;
                                } else {
                                    found = true;
                                    if (module.getAvailability() != null) {
                                        var JsonB = new JsonConfigProvider().getContext(null);
                                        JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                                        data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), fileList.get(i), false, false, MoodleAction.ExistingFile, getPriorityVisiblity(module.getVisible() == 1, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime())));
                                    } else {
                                        data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), fileList.get(i), false, false, MoodleAction.ExistingFile, module.getVisible() == 1));
                                    }
                                    fileList.remove(i);
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile, module.getVisible() == 1));
                        }
                    } else if (module.getModname().equals("url") && !config.getFormatsFileserver().isEmpty()) {
                        System.out.println(module.getName());
                        boolean found = false;
                        for (Path file : fileList) {
                            if (module.getName().equals(file.getFileName().toString())) {
                                found = true;
                                //Datei ist auf Moodle veröffentlicht
                                //einmalig durchgeführte initialisierung der Fileserver-Dateien Liste
                                if (!fileServerRequired) {
                                    //Bei erstmaligen bekanntwerden, dass der Fileserver benötigt wird, wird die Verbindung hergestellt und eine liste mit allen, dem Kurs
                                    // zugehörigen Dateien abgerufen. Diese wird "länger" gespeichert, um nur einen Verbindunscyclus zu haben
                                    String url = config.getFileserver();
                                    String user = config.getUserFileserver();
                                    String password = config.getPasswordFileserver();
                                    if (url == null || url.isEmpty() || url.isBlank() || user == null || user.isEmpty() || user.isBlank()
                                            || password == null || password.isEmpty() || password.isBlank()) {
                                        showNotification(NotificationType.ERROR, "start.sync.error.title",
                                                "start.sync.error.fileserver1.message");
                                    } else {
                                        try {
                                            fileClient = new FileServerClientFTP(config);
                                            fileClient.connect();
                                            files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> If there should be support for different upload-sections.
                                            fileClient.disconnect();
                                        } catch (Exception e) {
                                            logException(e, "Sync failed");
                                            showNotification(NotificationType.ERROR, "start.sync.error.title",
                                                    "start.sync.error.fileserver2.message");
                                        }
                                    }
                                    fileServerRequired = true;
                                }
                                for (FileServerFile fileServerFile : files) {
                                    if (fileServerFile.getFilename().equals(file.getFileName().toString())) {
                                        //Datei zusätzlich auf FileServer
                                        if (fileServerFile.getLastTimeModified() < Files.getLastModifiedTime(file).toMillis()) {
                                            //Datei auf Fileserver ist nicht aktuell -> FTPSynchronize
                                            if (module.getAvailability() != null) {
                                                var JsonB = new JsonConfigProvider().getContext(null);
                                                JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                                LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                                                data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), file, true, false, MoodleAction.FTPSynchronize, getPriorityVisiblity(module.getVisible() == 1, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime()), module.getId()));
                                            } else {
                                                data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), file, true, false, MoodleAction.FTPSynchronize, module.getVisible() == 1));
                                            }
                                            fileList.remove(file);
                                            break;
                                        } else {
                                            //Datei auf Fileserver aber aktuell
                                            if (module.getAvailability() != null) {
                                                var JsonB = new JsonConfigProvider().getContext(null);
                                                JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                                                LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                                                data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), file, false, false, MoodleAction.ExistingFile, getPriorityVisiblity(module.getVisible() == 1, JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class).getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime())));
                                            } else {
                                                data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), file, false, false, MoodleAction.ExistingFile, module.getVisible() == 1));
                                            }
                                            fileList.remove(file);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!found) {
                            data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile, module.getVisible() == 1));
                        }
                    } else {
                        //Das sind die die nicht "resource" sind
                        data.add(new syncTableElement(module.getName(), module.getId(), sectionnum, sectionId, data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile, module.getVisible() == 1));
                    }
                }

                if (fileList.size() != 0) {
                    for (Path path : fileList) {
                        if (contains(config.getFormatsMoodle().split(","), FilenameUtils.getExtension(path.getFileName().toString()))) {
                            data.add(new syncTableElement(path.getFileName().toString(), -1, sectionnum, sectionId, data.size(), "resource", path, true, false, MoodleAction.MoodleUpload, true));
                        }

                        //More Complicated: all Files for the Fileserver-Upload (if new upload oder update) are found here:
                        else if ((contains(config.getFormatsFileserver().split(","), FilenameUtils.getExtension(path.getFileName().toString()))) && !config.getFormatsFileserver().isBlank()) {
                            //alle die hier sind, sind lokal vorhanden aber nicht auf Moodle veröffentlicht
                            if (!fileServerRequired) {
                                //Bei erstmaligen bekanntwerden, dass der Fileserver benötigt wird, wird die Verbindung hergestellt und eine liste mit allen, dem Kurs
                                // zugehörigen Dateien abgerufen. Diese wird "länger" gespeichert, um nur einen Verbindunscyclus zu haben
                                String url = config.getFileserver();
                                String user = config.getUserFileserver();
                                String password = config.getPasswordFileserver();
                                if (url == null || url.isEmpty() || url.isBlank() || user == null || user.isEmpty() || user.isBlank()
                                        || password == null || password.isEmpty() || password.isBlank()) {
                                    showNotification(NotificationType.ERROR, "start.sync.error.title",
                                            "start.sync.error.fileserver1.message");
                                } else {
                                    try {
                                        fileClient = new FileServerClientFTP(config);
                                        fileClient.connect();
                                        files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> If there should be support for different upload-sections.
                                        fileClient.disconnect();
                                    } catch (Exception e) {
                                        logException(e, "Sync failed");
                                        showNotification(NotificationType.ERROR, "start.sync.error.title",
                                                "start.sync.error.fileserver2.message");
                                    }
                                }
                                fileServerRequired = true;
                            }
                            //Checken ob Modul vorhanden oder ob Datei hochgeladen?
                            //Änderungsdatum mit FTP-Datei vergleichen
                            //Hier auf jeden Fall gegeben: Array "Files" enthält alle hochgeladenen Files auf dem Fileserver (Name u Änderungsdatum)
                            for (FileServerFile fileServerFile : files) {
                                if (fileServerFile.getFilename().equals(path.getFileName())) {
                                    if (fileServerFile.getLastTimeModified() < Files.getLastModifiedTime(path).toMillis()) {
                                        //Datei ist nicht auf Moodle aber auf FTP, muss aber aktualisiert werden -> neuer Upload
                                        data.add(new syncTableElement(path.getFileName().toString(), -1, sectionnum, sectionId, data.size(), "url", path, true, false, MoodleAction.FTPUpload, true));
                                    } else {
                                        data.add(new syncTableElement(path.getFileName().toString(), -1, sectionnum, sectionId, data.size(), "url", path, true, false, MoodleAction.FTPLink, true));
                                    }
                                }
                            }
                            //Datei nicht auf Moodle und nicht auf dem FileServer
                            data.add(new syncTableElement(path.getFileName().toString(), -1, sectionnum, sectionId, data.size(), "url", path, true, false, MoodleAction.FTPUpload, true));

                        } else {
                            data.add(new syncTableElement(path.getFileName().toString(), -1, sectionnum, sectionId, data.size(), "resource", path, false, false, MoodleAction.DatatypeNotKnown, false));
                        }
                    }
                }
            } catch (Throwable e) {
                logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "start.sync.error.title",
                        "start.sync.error.message");
            }
        }
            }

            if (!sectionList.isEmpty()) {
                for (Path elem : sectionList) {
                    data.add(new syncTableElement(elem.getFileName().toString(), -1, -1, -1, data.size(), "section", elem, true, false, MoodleAction.UploadSection, true));
                }
            }

            /**for(syncTableElement listener : data){
                listener.deleteProperty().addListener((observable, oldSection, newSection) -> {
                    if(newSection){
                        deleteModule(listener);
                    }
                });
            }*/

            courseData = data;

            //FileWatcher aktivieren
            watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + config.recentCourseProperty().get().getDisplayname()));
            watcher.addListener(this);
            watcher.watch();

            /*data.addListener((ListChangeListener<syncTableElement>) change -> {
                if(change.next()){
                if(change.wasUpdated()){
                    ObservableList<syncTableElement> dummy = FXCollections.observableArrayList();
                    dummy = (ObservableList<syncTableElement>) change.getList();
                    for(syncTableElement test : dummy){
                        System.out.println(test.isSelected());
                    }
                }
            }});*/
            return data;

    }

    /*private void deleteModule(syncTableElement element){
        if(element.getAction() == MoodleAction.ExistingFile || element.getAction() == MoodleAction.NotLocalFile || element.getAction() == MoodleAction.MoodleSynchronize || element.getAction() == MoodleAction.FTPSynchronize){
            if(element.getDelete()){
                //Hier nochmal Nutzerdialog
                context.getEventBus().post(new ShowPresenterCommand<>(ConfirmDeleteModulePresenter.class));
            }
        }
    }*/

    @Override
    public void onCreated(FileEvent event) {
        view.setData(setData());
    }

    @Override
    public void onModified(FileEvent event) {
        view.setData(setData());
    }

    @Override
    public void onDeleted(FileEvent event) {
        view.setData(setData());
    }

    private Boolean getPriorityVisiblity(Boolean visible, Boolean availability){
        if(!visible || !availability) {
            return false;
        }
        return true;
    }

    private void openCourseDirectory(){
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(config.getSyncRootPath() + "/" + config.getRecentCourse().getDisplayname());
            desktop.open(dirToOpen);
        } catch (Throwable e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title",
                    "start.sync.error.path.unknown.message");
        }
    }

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }

}
