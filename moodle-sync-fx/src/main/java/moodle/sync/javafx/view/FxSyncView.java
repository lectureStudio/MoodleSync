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

/**
 * Class implementing the functions of the "sync-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-sync", presenter = SyncPresenter.class)
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


    /**
     * Closes the "sync-page" and returns to "start-page".
     *
     * @param action User presses button.
     */
    @Override
    public void setOnClose(Action action) {
        FxUtils.bindAction(closeButton, action);
    }

    /**
     * Starts the execution of the sync-process.
     *
     * @param action User presses button.
     */
    @Override
    public void setOnSync(Action action) {
        FxUtils.bindAction(finalSyncButton, action);
    }

    /**
     * Displays the UploadData.
     *
     * @param files UploadData to show.
     */
    @Override
    public void setFiles(List<UploadData> files) {
        FxUtils.invoke(() -> {
            //syncItemsTableView.getItems().clear();
            TreeItem<UploadElementTableItem> root = new TreeItem<>(new UploadElementTableItem("root", ""));
            filesHandler(files, root);
            syncItemsTableView.setRoot(root);
        });
    }

    /**
     * Recursive method needed to show directories as a TreeTable.
     *
     * @param files UploadData inside a directory.
     * @param root  Root-element for this branch.
     */
    private void filesHandler(List<UploadData> files, TreeItem<UploadElementTableItem> root) {
        for (UploadData file : files) {
            if (file instanceof UploadElement) {
                root.getChildren().add(new TreeItem<>(new UploadElementTableItem((UploadElement) file)));
            } else if (file instanceof UploadFolderElement) {
                TreeItem<UploadElementTableItem> dir = new TreeItem<>(new UploadElementTableItem((UploadFolderElement) file, "Mit Moodle synchronisieren", true));
                filesHandler(((UploadFolderElement) file).getContent(), dir);
                root.getChildren().add(dir);
            }
        }
    }


}
