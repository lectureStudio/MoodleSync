package moodle.sync.javafx;

import javafx.scene.control.ListCell;
import moodle.sync.web.json.Course;


import static java.util.Objects.isNull;

/**
 * Class used to display the name of a Course inside a ListCell.
 *
 * @author Daniel Schröter
 */
public class CourseListCell extends ListCell<Course> {

    @Override
    protected void updateItem(Course item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if (isNull(item) || empty) {
            setText("");
        } else {
            setText(item.getDisplayname());
        }
    }

}
