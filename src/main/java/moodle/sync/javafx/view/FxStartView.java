package moodle.sync.javafx.view;

import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import moodle.sync.presenter.StartPresenter;
import moodle.sync.view.StartView;

@FxmlView(name = "main-start", presenter = StartPresenter.class)
public class FxStartView extends VBox implements StartView, FxView {

	@FXML
	private Button exitButton;

	@FXML
	private Button syncButton;

	@FXML
	private Button settingsButton;


	public FxStartView() {
		super();
	}

	@Override
	public void setOnExit(Action action) {
		FxUtils.bindAction(exitButton, action);
	}

	@Override
	public void setOnSync(Action action) {
		FxUtils.bindAction(syncButton, action);
	}

	@Override
	public void setOnSettings(Action action) {
		FxUtils.bindAction(settingsButton, action);
	}
}
