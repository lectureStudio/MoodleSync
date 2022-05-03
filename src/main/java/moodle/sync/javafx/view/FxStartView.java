package moodle.sync.javafx.view;

import moodle.sync.web.json.Course;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.javafx.beans.LectObjectProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;

import moodle.sync.presenter.StartPresenter;
import moodle.sync.view.StartView;

import java.util.List;

/**
 * Class implementing the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-start", presenter = StartPresenter.class)
public class FxStartView extends VBox implements StartView, FxView {

    @FXML
    private Button exitButton;

    @FXML
    private Button syncButton;

    @FXML
    private Button settingsButton;

    @FXML
    private ComboBox<Course> selectcourseCombo;

    @FXML
    private ComboBox<Section> selectsectionCombo;


    public FxStartView() {
        super();
    }


    /**
     * Exit the application
     *
     * @param action User presses button.
     */
    @Override
    public void setOnExit(Action action) {
        FxUtils.bindAction(exitButton, action);
    }

    /**
     * Start the synchronisation process.
     *
     * @param action User presses button.
     */
    @Override
    public void setOnSync(Action action) {
        FxUtils.bindAction(syncButton, action);
    }

    /**
     * User opens the "settings-page".
     *
     * @param action User presses button.
     */
    @Override
    public void setOnSettings(Action action) {
        FxUtils.bindAction(settingsButton, action);
    }

    /**
     * Method to set the elements of the Course-Combobox.
     *
     * @param courses Moodle-Courses to display.
     */
    @Override
    public void setCourses(List<Course> courses) {
        FxUtils.invoke(() -> selectcourseCombo.getItems().setAll(courses));
    }

    /**
     * Choosen Moodle-course.
     *
     * @param course choosen Moodle-course.
     */
    @Override
    public void setCourse(ObjectProperty<Course> course) {
        selectcourseCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(course));
    }

    /**
     * Method to set the elements of the Section-Combobox.
     *
     * @param sections Course-Sections to display.
     */
    @Override
    public void setSections(List<Section> sections) {
        FxUtils.invoke(() -> selectsectionCombo.getItems().setAll(sections));
    }

    /**
     * Choosen course-section.
     *
     * @param section choosen course-section.
     */
    @Override
    public void setSection(ObjectProperty<Section> section) {
        selectsectionCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(section));
    }

    /**
     * Method to initiate the display of the sections of a choosen Course.
     *
     * @param action User chooses Course.
     */
    @Override
    public void setOnCourseChanged(ConsumerAction<Course> action) {
        selectcourseCombo.valueProperty().addListener((observable, oldCourse, newCourse) -> {
            executeAction(action, newCourse);
        });
    }

}
