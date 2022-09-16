package moodle.sync.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.syncTableElement;
import org.lecturestudio.javafx.control.SvgIcon;

public class UploadHighlightTableCell <U, B> extends TableCell<syncTableElement, String> {

    private TableCell tableCell;

    @Override
    protected void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        setGraphic(null);

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
        } else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload || getTableRow().getItem().getAction() == MoodleAction.FTPUpload ||getTableRow().getItem().getAction() == MoodleAction.UploadSection || getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown) {
            setText(null);
        } else{
            SvgIcon icon = new SvgIcon();
            setStyle("-fx-font-weight: normal");
            if(getTableRow().getItem().getModuleType().equals("section")){
                setStyle("-fx-font-weight: bold");
            } else if(getTableRow().getItem().getModuleType().equals("resource")) {
                icon.getStyleClass().add("file-icon");
            } else if(getTableRow().getItem().getModuleType().equals("forum")) {
                icon.getStyleClass().add("forum-icon");
            } else if(getTableRow().getItem().getModuleType().equals("folder")) {
                icon.getStyleClass().add("folder-icon");
            } else if(getTableRow().getItem().getModuleType().equals("label")) {
                icon.getStyleClass().add("label-icon");
            } else if(getTableRow().getItem().getModuleType().equals("quiz")) {
                icon.getStyleClass().add("quiz-icon");
            } else if(getTableRow().getItem().getModuleType().equals("assign")) {
                icon.getStyleClass().add("assignment-icon");
            } else if(getTableRow().getItem().getModuleType().equals("chat")) {
                icon.getStyleClass().add("chat-icon");
            } else if(getTableRow().getItem().getModuleType().equals("feedback")) {
                icon.getStyleClass().add("feedback-icon");
            } else if(getTableRow().getItem().getModuleType().equals("url")) {
                icon.getStyleClass().add("url-icon");
            } else if(getTableRow().getItem().getModuleType().equals("survey")) {
                icon.getStyleClass().add("survey-icon");
            } else{
                icon.getStyleClass().add("other-icon");
            }
            setGraphic(icon);
            setText(item);
        }
    }
}
