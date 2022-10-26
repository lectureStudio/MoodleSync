package moodle.sync.javafx.custom;

import javafx.beans.InvalidationListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.web.WebView;
import javafx.util.converter.DefaultStringConverter;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.model.syncTableElement;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.javafx.control.SvgIcon;

public class UploadHighlightTableCell <U, B> extends TextFieldTableCell<syncTableElement, String> {

    @Override
    public void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        setConverter(new DefaultStringConverter());
        setGraphic(null);
        //setText(null);

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getAction() == MoodleAction.ExistingSection ){
            TitledPane titlePlane = new TitledPane();
            WebView fontWebView = new WebView();
            fontWebView.getEngine().loadContent(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", ""));
            titlePlane.setText(getTableRow().getItem().getModuleName());
            titlePlane.setContent(fontWebView);
            titlePlane.expandedProperty().setValue(false);
            if(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", "").isEmpty()){
                titlePlane.setPrefHeight(0);
                titlePlane.setCollapsible(false);
            } else {
                titlePlane.setPrefHeight(70);
            }
            setGraphic(titlePlane);
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload || getTableRow().getItem().getAction() == MoodleAction.FTPUpload ||getTableRow().getItem().getAction() == MoodleAction.UploadSection || getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown) {
            if((getTableRow().getItem().getAction() == MoodleAction.MoodleUpload && getTableRow().getItem() != null) ||(getTableRow().getItem().getAction() == MoodleAction.FTPUpload && getTableRow().getItem() != null)){
                if(!getTableRow().getItem().selectedProperty().get()){
                    setVisible(false);
                }
                getTableRow().getItem().selectedProperty().addListener((observable, oldBool, newBool) -> {
                        managedProperty().bind(visibleProperty());
                        setEditable(getTableRow().getItem().selectedProperty().get());
                        setText(getTableRow().getItem().getExistingFileName());
                        setVisible(getTableRow().getItem().selectedProperty().get());
                });
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
            setText(item);
        }
    }
}

