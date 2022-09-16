package moodle.sync.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.syncTableElement;

public class StatusTableCell <U, B> extends TableCell<syncTableElement, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setStyle("-fx-background-color: TRANSPARENT");
        } else {
            if(getTableRow().getItem().getAction() == MoodleAction.MoodleSynchronize){
                setText(item);
                setStyle("-fx-background-color: PALEGREEN");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload){
                setText(item);
                setStyle("-fx-background-color: SKYBLUE");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.UploadSection){
                setText(item);
                setStyle("-fx-font-weight: bold");
            }
            /*else if(getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown){
                setText(item);
                setStyle("-fx-font-weight: bold");
            }*/
            else{
                setText(item);
                setStyle("-fx-background-color: TRANSPARENT");
            }
        }
    }
}

