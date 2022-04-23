package moodle.sync.javafx.view;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import moodle.sync.presenter.SyncPresenter;
import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.UploadData.UploadFolderElement;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.view.SyncView;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import java.util.List;


@FxmlView(name = "main-sync", presenter =  SyncPresenter.class)
public class FxSyncView extends VBox implements SyncView, FxView {

    @FXML
    private TreeTableView<UploadElementTableItem> syncItemsTableView;

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
    public void setFiles(List<UploadData> files){
        FxUtils.invoke(() -> {
           //syncItemsTableView.getItems().clear();
            TreeItem<UploadElementTableItem> root = new TreeItem<UploadElementTableItem>(new UploadElementTableItem("", ""));
            filesHandler(files, root);
            syncItemsTableView.setRoot(root);
        });
    }

    private void filesHandler(List<UploadData> files, TreeItem<UploadElementTableItem> root){
        for (UploadData file : files) {
            if (file instanceof UploadElement) {
                root.getChildren().add(new TreeItem<UploadElementTableItem>(new UploadElementTableItem((UploadElement) file)));
            } else if (file instanceof UploadFolderElement) {
                TreeItem<UploadElementTableItem> dir = new TreeItem<UploadElementTableItem>(new UploadElementTableItem((UploadFolderElement) file, "Mit Moodle Synchronisieren", true));
                filesHandler(((UploadFolderElement) file).getContent(), dir);
                root.getChildren().add(dir);
            }
        }
    }


}
