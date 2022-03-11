package moodle.sync.javafx.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button syncRootPathButton;

    public FxSettingsView() {
        super();
    }

    @Override
    public void setOnExit(Action action) {
        FxUtils.bindAction(closesettingsButton, action);
    }

    @Override
    public void setMoodleToken(StringProperty moodleToken) {
        tokenField.textProperty().bindBidirectional(new LectStringProperty(moodleToken));
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

