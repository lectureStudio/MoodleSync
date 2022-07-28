package moodle.sync.javafx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.util.syncTableElement;

/**
 * Class used for determining the state of a CheckBox inside the "sync-page"-table.
 *
 * @author Daniel Schröter
 */
public class UploadElementCellValueFactory implements Callback<TableColumn.CellDataFeatures<syncTableElement,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<syncTableElement, Boolean> param)
    {
        syncTableElement elem = param.getValue();
        //selectedProperty should be used to determine the state.
        param.getValue().selectedProperty();
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




