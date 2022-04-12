package moodle.sync.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import moodle.sync.util.UploadElementTableItem;

public class UploadCheckBoxCell extends CheckBoxTreeTableCell<UploadElementTableItem,Boolean> {

    @Override
    public void updateItem(Boolean item, boolean empty) {

        TreeTableRow<UploadElementTableItem> currentRow = getTableRow();
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        if (currentRow.getItem() != null && !empty) {
            this.setGraphic(null);
            if (!currentRow.getItem().isSelectable()) {
                this.setManaged(false);
                this.setVisible(false);
            }
        }
        super.updateItem(item,empty);
    }
}
