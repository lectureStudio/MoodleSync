package moodle.sync.javafx;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.StringConverter;
import moodle.sync.util.UploadElementTableItem;
import moodle.sync.util.syncTableElement;

/**
 * Class used to display the selctedProperty-value inside a CheckBoxTreeTableCell.
 *
 * @author Daniel Schröter
 */
public class UploadCheckBoxCell<U, B> extends CheckBoxTableCell<syncTableElement, Boolean> {

    private CheckBox checkBox;

    private boolean showLabel;

    private ObservableValue<Boolean> booleanProperty;


    @Override
    public void updateItem(Boolean item, boolean empty) {
        this.checkBox = new CheckBox();
        this.checkBox.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
        setGraphic(checkBox);

        super.updateItem(item, empty);

        if (booleanProperty instanceof BooleanProperty) {
            checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            booleanProperty = null;
        }
        if (empty) {
            checkBox.setAlignment(Pos.CENTER);
            setText(null);
            setGraphic(null);
        } else if (getTableRow() != null) {
            if(getTableRow().getItem() != null && !getTableRow().getItem().isSelectable()) {
                checkBox.setAlignment(Pos.CENTER);
                setDisable(false);
                setGraphic(null);
            }
        } else {
            StringConverter<Boolean> c = getConverter();

            if (showLabel) {
                checkBox.setAlignment(Pos.CENTER);
                setText(c.toString(item));
            }
            setGraphic(checkBox);

            ObservableValue<?> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = (ObservableValue<Boolean>) obsValue;
                checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }

            checkBox.disableProperty().bind(Bindings.not(
                    getTableView().editableProperty().and(
                            getTableColumn().editableProperty()).and(
                            editableProperty())
            ));


        }

        checkBox.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
    }


    private ObservableValue<?> getSelectedProperty() {
        return getSelectedStateCallback() != null ?
                getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());
    }
}
