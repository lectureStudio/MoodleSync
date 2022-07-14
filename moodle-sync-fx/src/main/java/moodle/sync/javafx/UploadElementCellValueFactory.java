package moodle.sync.javafx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import moodle.sync.util.UploadElementTableItem;

/**
 * Class used for determining the state of a CheckBox inside the "sync-page"-table.
 *
 * @author Daniel Schröter
 */
public class UploadElementCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<UploadElementTableItem,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<UploadElementTableItem, Boolean> param)
    {
        TreeItem<UploadElementTableItem> item = param.getValue();
        UploadElementTableItem elem = item.getValue();
        //selectedProperty should be used to determine the state.
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




