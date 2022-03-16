package moodle.sync.javafx;

import javafx.scene.control.TableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import moodle.sync.util.UploadElementTableItem;

public class UploadCheckBoxCell extends CheckBoxTableCell<UploadElementTableItem,Boolean> {

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item,empty);

        TableRow<UploadElementTableItem> currentRow = getTableRow();
        if (currentRow.getItem() != null && !empty) {
            System.out.println(currentRow.getItem().isSelectable());
            if (!currentRow.getItem().isSelectable()) {
                this.setManaged(false);
                this.setVisible(false);
            }
        }
    }
}
