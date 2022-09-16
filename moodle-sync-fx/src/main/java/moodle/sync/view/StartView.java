package moodle.sync.view;

import javafx.collections.ObservableList;
import moodle.sync.util.syncTableElement;
import moodle.sync.web.json.Course;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.Observable;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.core.view.View;
import org.lecturestudio.javafx.util.FxUtils;

import java.util.List;

/**
 * Interface defining the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
public interface StartView extends View {


    void setOnSync(Action action);

    void setOnSettings(Action action);

    void setOnFolder(Action action);

    void setCourses(List<Course> courses);

    void setCourse(ObjectProperty<Course> course);

    void setSections(List<Section> sections);

    void setSection(ObjectProperty<Section> section);

    void setOnCourseChanged(ConsumerAction<Course> action);

    void setData(ObservableList<syncTableElement> data);
}
