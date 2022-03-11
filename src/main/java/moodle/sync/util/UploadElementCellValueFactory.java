package moodle.sync.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class UploadElementCellValueFactory implements Callback<TableColumn.CellDataFeatures<UploadElementTableItem,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UploadElementTableItem, Boolean> param)
    {
        return param.getValue().selectedProperty();
    }
}
