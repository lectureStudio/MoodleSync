package moodle.sync.javafx;

import javafx.scene.control.ListView;
import javafx.util.Callback;
import moodle.sync.web.json.Course;


public class CourseCellFactory implements Callback<ListView<Course>, CourseListCell> {

    @Override
    public CourseListCell call(ListView<Course> param) {
        return new CourseListCell();
    }

}
