package moodle.sync.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;


public class CheckBoxTableCellFactory implements Callback<TableColumn<UploadElementTableItem, Boolean>, TableCell<UploadElementTableItem, Boolean>> {
    public TableCell<UploadElementTableItem, Boolean> call(TableColumn<UploadElementTableItem, Boolean> param) {
        return new CheckBoxTableCell<UploadElementTableItem,Boolean>();
    }
}
