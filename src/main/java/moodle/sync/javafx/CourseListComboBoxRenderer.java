package moodle.sync.javafx;

import moodle.sync.web.json.Course;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.nonNull;

public class CourseListComboBoxRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Course course = (Course) value;

        if (nonNull(course)) {
            setText(course.getDisplayname());
        }


        return this;
    }
}
