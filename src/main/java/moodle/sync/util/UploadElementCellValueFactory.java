package moodle.sync.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class UploadElementCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<UploadElementTableItem,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<UploadElementTableItem, Boolean> param)
    {
        TreeItem<UploadElementTableItem> item = param.getValue();
        return item.getValue().selectedProperty();
    }
}
