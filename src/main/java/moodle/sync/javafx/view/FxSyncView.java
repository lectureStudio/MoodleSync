package moodle.sync.javafx.view;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import moodle.sync.presenter.SyncPresenter;
import moodle.sync.util.UploadElement;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.view.SyncView;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import java.util.ArrayList;
import java.util.List;


@FxmlView(name = "main-sync", presenter =  SyncPresenter.class)
public class FxSyncView extends VBox implements SyncView, FxView {

    @FXML
    private TableView<UploadElementTableItem> syncItemsTableView;

    @FXML
    private Button finalSyncButton;

    @FXML
    private Button closeButton;

    public FxSyncView() {
        super();
    }


    @Override
    public void setOnClose(Action action){
        FxUtils.bindAction(closeButton, action);
    }

    @Override
    public void setOnSync(Action action){
        FxUtils.bindAction(finalSyncButton, action);
    }
    @Override
    public void setFiles(List<UploadElement> files){
        FxUtils.invoke(() -> {
            syncItemsTableView.getItems().clear();

            for (UploadElement file : files) {
                syncItemsTableView.getItems().add(new UploadElementTableItem(file));
            }
        });
    }

}
