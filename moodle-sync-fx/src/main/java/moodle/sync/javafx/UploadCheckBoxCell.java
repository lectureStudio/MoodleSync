package moodle.sync.javafx;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.StringConverter;
import moodle.sync.util.UploadElementTableItem;

/**
 * Class used to display the selctedProperty-value inside a CheckBoxTreeTableCell.
 *
 * @author Daniel Schröter
 */
public class UploadCheckBoxCell<U, B> extends CheckBoxTreeTableCell<UploadElementTableItem, Boolean> {

    private CheckBox checkBox;

    private boolean showLabel;

    private ObservableValue<Boolean> booleanProperty;


    @Override
    public void updateItem(Boolean item, boolean empty) {
        this.checkBox = new CheckBox();

        super.updateItem(item, empty);

        if (booleanProperty instanceof BooleanProperty) {
            checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            booleanProperty = null;
        }
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (getTableRow() != null) {
            if (getTableRow().getTreeItem() != null) {
                setDisable(!getTableRow().getTreeItem().getValue().isSelectable());
            }
        } else {
            StringConverter<Boolean> c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }
            setGraphic(checkBox);

            ObservableValue<?> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = (ObservableValue<Boolean>) obsValue;
                checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }

            checkBox.disableProperty().bind(Bindings.not(
                    getTreeTableView().editableProperty().and(
                            getTableColumn().editableProperty()).and(
                            editableProperty())
            ));


        }
    }


    private ObservableValue<?> getSelectedProperty() {
        return getSelectedStateCallback() != null ?
                getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());
    }
}
