package moodle.sync.util;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class UploadElementCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<UploadElementTableItem,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<UploadElementTableItem, Boolean> param)
    {
        TreeItem<UploadElementTableItem> item = param.getValue();
        UploadElementTableItem elem = item.getValue();
        item.getValue().selectedProperty();
        SimpleBooleanProperty booleanProp= (SimpleBooleanProperty) elem.selectedProperty();
        booleanProp.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                Boolean newValue) {
                elem.setSelected(newValue);
            }
        });
        return booleanProp;
        }
    }




