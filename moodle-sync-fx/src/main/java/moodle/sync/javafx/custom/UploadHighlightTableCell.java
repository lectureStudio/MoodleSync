package moodle.sync.javafx.custom;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.converter.DefaultStringConverter;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.model.syncTableElement;
import org.controlsfx.control.PopOver;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.javafx.beans.LectStringProperty;
import org.lecturestudio.javafx.control.SvgIcon;

import java.util.ArrayList;
import java.util.List;

public class UploadHighlightTableCell <U, B> extends TableCell<syncTableElement, String> {

    private Listener listener = new Listener();
    private PopOver popOver;

    @Override
    public void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        if(getTableRow().getItem() != null) getTableRow().getItem().selectedProperty().removeListener(listener);

        if(popOver != null){
            popOver = null;
            this.setOnMouseEntered(mouseEvent -> {
            });

            this.setOnMouseExited(mouseEvent -> {
            });
        }

        setGraphic(null);
        setVisible(true);
        getTableRow().getStyleClass().remove("headerstyle");

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getAction() == MoodleAction.ExistingSection ){
            if(!(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", "")).isEmpty()){
                Label textArea = new Label();
                textArea.setText(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", ""));
                textArea.setWrapText(true);
                textArea.setMaxWidth(200);
                textArea.setStyle("-fx-font-weight: normal");
                textArea.getStyleClass().add("popUpTextArea");
                VBox vBox = new VBox(textArea);
                vBox.setPadding(new Insets(5));
                popOver = new PopOver(vBox);
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

                this.setOnMouseEntered(mouseEvent -> {
                    //Show PopOver when mouse enters label
                    popOver.show(this);
                });
                this.setOnMouseExited(mouseEvent -> {
                    //Hide PopOver when mouse exits label
                    popOver.hide();
                });
            }
            setText(getTableRow().getItem().getModuleName());
            setStyle("-fx-font-weight: bold");


            getTableRow().getStyleClass().add("headerstyle");

        } else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload || getTableRow().getItem().getAction() == MoodleAction.FTPUpload ||getTableRow().getItem().getAction() == MoodleAction.UploadSection || getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown) {
            if((getTableRow().getItem().getAction() == MoodleAction.MoodleUpload && getTableRow().getItem() != null) ||(getTableRow().getItem().getAction() == MoodleAction.FTPUpload && getTableRow().getItem() != null)){
                if(!getTableRow().getItem().selectedProperty().get()){
                   setText(null);
                }
                getTableRow().getItem().selectedProperty().addListener(listener);
            }
            else {
                setText(null);
            }
        } else{
            setEditable(false);
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
            setText(item.replaceAll("\\u00a0\\n|&nbsp;\\r\\n", ""));
        }
    }

    public class Listener implements ChangeListener {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            setEditable(getTableRow().getItem().selectedProperty().get());
            if(getTableRow().getItem().selectedProperty().get()) setText(getTableRow().getItem().getExistingFileName());
            setVisible(getTableRow().getItem().selectedProperty().get());
        }
    }

}

