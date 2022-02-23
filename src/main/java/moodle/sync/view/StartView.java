package moodle.sync.view;

import moodle.sync.web.json.Course;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;

import java.util.List;

public interface StartView extends View {

	void setOnExit(Action action);

	void setOnSync(Action action);

	void setOnSettings(Action action);

	void setCourses(List<Course> courses);

	void setCourse(ObjectProperty<Course> course);
}
