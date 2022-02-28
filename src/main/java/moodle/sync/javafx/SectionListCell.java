package moodle.sync.javafx;

import javafx.scene.control.ListCell;
import moodle.sync.web.json.Section;


import static java.util.Objects.isNull;

public class SectionListCell extends ListCell<Section> {

    @Override
    protected void updateItem(Section item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if (isNull(item) || empty) {
            setText("");
        }
        else {
            setText(item.getName());
        }
    }

}
