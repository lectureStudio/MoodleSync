package moodle.sync.javafx;

import javafx.scene.control.TableCell;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.syncTableElement;

public class UploadHighlightTableCell <U, B> extends TableCell<syncTableElement, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
        } else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload || getTableRow().getItem().getAction() == MoodleAction.UploadSection) {
            setText(null);
        } else{
            setText(item);
            if(getTableRow().getItem().getModuleType().equals("section")){
                setStyle("-fx-font-weight: bold");
            } else{
                setStyle("-fx-font-weight: normal");
            }

        }
    }
}
