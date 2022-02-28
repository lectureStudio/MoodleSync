package moodle.sync.javafx.view;

import moodle.sync.web.json.Course;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.camera.AspectRatio;
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

	@Override
	public void setOnExit(Action action) {
		FxUtils.bindAction(exitButton, action);
	}

	@Override
	public void setOnSync(Action action) {
		FxUtils.bindAction(syncButton, action);
	}

	@Override
	public void setOnSettings(Action action) {
		FxUtils.bindAction(settingsButton, action);
	}

	@Override
	public void setCourses(List<Course> courses) {
		FxUtils.invoke(() -> selectcourseCombo.getItems().setAll(courses));
	}

	@Override
	public void setCourse(ObjectProperty<Course> course){
		selectcourseCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(course));
	}

	@Override
	public void setSections(List<Section> sections) {
		FxUtils.invoke(() -> selectsectionCombo.getItems().setAll(sections));
	}

	@Override
	public void setSection(ObjectProperty<Section> section){
		selectsectionCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(section));
	}

	@Override
	public void setOnCourseChanged(ConsumerAction<Course> action) {
		selectcourseCombo.valueProperty().addListener((observable, oldCourse, newCourse) -> {
			executeAction(action, newCourse);
		});
	}

}
