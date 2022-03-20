package moodle.sync.javafx.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.presenter.StartPresenter;
import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.beans.LectStringProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

@FxmlView(name = "main-settings", presenter = SettingsPresenter.class)
public class FxSettingsView extends VBox implements SettingsView, FxView {

    @FXML
    private Button closesettingsButton;

    @FXML
    private TextField tokenField;

    @FXML
    private TextField syncRootPath;

    @FXML
    private TextField ftpfield;

    @FXML
    private TextField ftpuser;

    @FXML
    private TextField ftppassword;

    @FXML
    private TextField moodleField;

    @FXML
    private TextArea formatsmoodle;

    @FXML
    private TextField ftpport;

    @FXML
    private TextArea formatsfileserver;

    @FXML
    private Button syncRootPathButton;

    public FxSettingsView() {
        super();
    }

    @Override
    public void setOnExit(Action action) {
        FxUtils.bindAction(closesettingsButton, action);
    }

    @Override
    public void setMoodleField(StringProperty moodleURL){
        moodleField.textProperty().bindBidirectional(new LectStringProperty(moodleURL));
    }

    @Override
    public void setMoodleToken(StringProperty moodleToken) {
        tokenField.textProperty().bindBidirectional(new LectStringProperty(moodleToken));
    }

    @Override
    public void setFtpField(StringProperty ftpURL){
        ftpfield.textProperty().bindBidirectional(new LectStringProperty(ftpURL));
    }

    @Override
    public void setFtpPort(StringProperty ftpPort){
        ftpport.textProperty().bindBidirectional(new LectStringProperty(ftpPort));
    }

    @Override
    public void setFtpUser(StringProperty ftpUser){
        ftpuser.textProperty().bindBidirectional(new LectStringProperty(ftpUser));
    }

    @Override
    public void setFtpPassword(StringProperty ftpPassword){
        ftppassword.textProperty().bindBidirectional(new LectStringProperty(ftpPassword));
    }

    @Override
    public void setFormatsMoodle(StringProperty moodleformats) {
        formatsmoodle.textProperty().bindBidirectional(new LectStringProperty(moodleformats));
    }

    @Override
    public void setFormatsFileserver(StringProperty fileserverformats) {
        formatsfileserver.textProperty().bindBidirectional(new LectStringProperty(fileserverformats));
    }

    @Override
    public void setSyncRootPath(StringProperty path){
        syncRootPath.textProperty().bindBidirectional(new LectStringProperty(path));
    }

    @Override
    public void setSelectSyncRootPath(Action action) {
        FxUtils.bindAction(syncRootPathButton, action);
    }


}

