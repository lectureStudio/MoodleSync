package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.presenter.StartPresenter;
import moodle.sync.util.UserInputValidations;
import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.beans.LectStringProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@FxmlView(name = "main-settings", presenter = SettingsPresenter.class)
public class FxSettingsView extends VBox implements SettingsView, FxView {

    @FXML
    private Button closesettingsButton;

    @FXML
    private TextField tokenField;

    @FXML
    private TextField syncRootPath;

    @FXML
    private TextField ftpField;

    @FXML
    private TextField ftpUser;

    @FXML
    private TextField ftpPassword;

    @FXML
    private TextField moodleField;

    @FXML
    private TextArea formatsMoodle;

    @FXML
    private TextField ftpPort;

    @FXML
    private TextArea formatsFileserver;

    @FXML
    private Button syncRootPathButton;

    public FxSettingsView() {
        super();
    }

    @Override
    public void setOnExit(Action action) {
        //ftpfield.setTextFormatter(UserInputValidations.urlTextFormatter());
        FxUtils.bindAction(closesettingsButton, action);
    }

    @Override
    public void setMoodleField(StringProperty moodleURL){
        moodleField.textProperty().bindBidirectional(new LectStringProperty(moodleURL));
        //moodleField.setTextFormatter(new TextFormatter<Object>());
        moodleField.textProperty().addListener(event -> {
            moodleField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                            !moodleField.getText().matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
            );
        });
    }

    @Override
    public void setMoodleToken(StringProperty moodleToken) {
        tokenField.textProperty().bindBidirectional(new LectStringProperty(moodleToken));
        tokenField.textProperty().addListener(event -> {
            tokenField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    (tokenField.getText().isEmpty())
            );
        });
    }

    @Override
    public void setFtpField(StringProperty ftpURL){
        ftpField.textProperty().bindBidirectional(new LectStringProperty(ftpURL));
        ftpField.textProperty().addListener(event -> {
            ftpField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    (!ftpField.getText().isEmpty() &&
                            !ftpField.getText().matches("^(((https?|ftp)://)|(ftp\\.))[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
            );
        });
    }

    @Override
    public void setFtpPort(StringProperty ftpport){
        ftpPort.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(),0,UserInputValidations.numberValidationFormatter));
        ftpPort.textProperty().bindBidirectional(new LectStringProperty(ftpport));
    }

    @Override
    public void setFtpUser(StringProperty ftpuser){
        ftpUser.textProperty().bindBidirectional(new LectStringProperty(ftpuser));
    }

    @Override
    public void setFtpPassword(StringProperty ftppassword){
        ftpPassword.textProperty().bindBidirectional(new LectStringProperty(ftppassword));
    }

    @Override
    public void setFormatsMoodle(StringProperty moodleformats) {
        formatsMoodle.textProperty().bindBidirectional(new LectStringProperty(moodleformats));
        formatsMoodle.textProperty().addListener(event -> {
            formatsMoodle.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    formatsMoodle.getText().matches("\\s")
            );
        });
    }

    @Override
    public void setFormatsFileserver(StringProperty fileserverformats) {
        formatsFileserver.textProperty().bindBidirectional(new LectStringProperty(fileserverformats));
    }

    @Override
    public void setSyncRootPath(StringProperty path){
        syncRootPath.textProperty().bindBidirectional(new LectStringProperty(path));
        syncRootPath.textProperty().addListener(event -> {
            syncRootPath.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    !syncRootPath.getText().isEmpty() &&
                            !Files.isDirectory(Paths.get(syncRootPath.getText()))
            );
        });

    }

    @Override
    public void setSelectSyncRootPath(Action action) {
        FxUtils.bindAction(syncRootPathButton, action);
    }


}

