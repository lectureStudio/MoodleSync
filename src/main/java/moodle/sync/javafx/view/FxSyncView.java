package moodle.sync.javafx.view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import moodle.sync.presenter.SyncPresenter;
import moodle.sync.util.UploadElement;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.view.SyncView;
import moodle.sync.web.json.Course;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.javafx.event.CellButtonActionEvent;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;
import org.lecturestudio.web.api.filter.RegexRule;

import java.util.ArrayList;
import java.util.List;


@FxmlView(name = "main-sync", presenter =  SyncPresenter.class)
public class FxSyncView extends VBox implements SyncView, FxView {

    @FXML
    private TableView<UploadElementTableItem> syncItemsTableView;

    @FXML
    private Button finalSyncButton;

    public FxSyncView() {
        super();
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

    public List<UploadElement> returnList(){
        List<UploadElement> synclist = new ArrayList<>();
        for(int i = 0; i < syncItemsTableView.getItems().size(); i++){
            synclist.add(syncItemsTableView.getItems().get(i).getUploadElement());
        }
        return synclist;
    }


    /*@FXML
    private void initialize() {
        // Set table column resize policy.
        ObservableList<TableColumn<FilesTableItem, ?>> columns = quizRegexTableView.getColumns();

        // Set table column edit policy.
        @SuppressWarnings("unchecked")
        TableColumn<FilesTableItem, String> regexColumn = (TableColumn<FilesTableItem, String>) columns.get(0);
        regexColumn.setOnEditCommit(event -> {
            FilesTableItem item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setQuizRegex(event.getNewValue());
        });
    }*/
}
