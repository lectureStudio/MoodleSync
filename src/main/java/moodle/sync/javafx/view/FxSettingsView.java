package moodle.sync.javafx.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.presenter.StartPresenter;
import moodle.sync.view.SettingsView;
import moodle.sync.view.StartView;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

@FxmlView(name = "main-settings", presenter = SettingsPresenter.class)
public class FxSettingsView extends VBox implements SettingsView, FxView {

    @FXML
    private Button closesettingsButton;

    public FxSettingsView() {
        super();
    }

    @Override
    public void setOnExit(Action action) {
        FxUtils.bindAction(closesettingsButton, action);
    }

}

