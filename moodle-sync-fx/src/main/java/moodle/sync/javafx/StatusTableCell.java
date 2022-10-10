package moodle.sync.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.syncTableElement;
import org.lecturestudio.javafx.control.SvgIcon;

public class StatusTableCell <U, B> extends TableCell<syncTableElement, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

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
            else if(getTableRow().getItem().getAction() == MoodleAction.FTPUpload){
                SvgIcon icon = new SvgIcon();
                icon.getStyleClass().add("ftp-icon");
                setGraphic(icon);
                setText(item);
                setStyle("-fx-background-color: ORANGE");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.UploadSection){
                setText(item);
                setStyle("-fx-font-weight: bold");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown){
                setText(item);
                setStyle("-fx-font-weight: bold");
            }
            else{
                setText(item);
                setStyle("-fx-background-color: TRANSPARENT");
            }
        }
    }
}

