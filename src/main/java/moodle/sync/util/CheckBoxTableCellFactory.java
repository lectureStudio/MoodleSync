package moodle.sync.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.UploadCheckBoxCell;


public class CheckBoxTableCellFactory implements Callback<TreeTableColumn<UploadElementTableItem, Boolean>, TreeTableCell<UploadElementTableItem, Boolean>> {
    public TreeTableCell<UploadElementTableItem, Boolean> call(TreeTableColumn<UploadElementTableItem, Boolean> param) {

        UploadCheckBoxCell cell = new UploadCheckBoxCell();
        cell.setStyle("-fx-alignment: CENTER;");
        return cell;
    }
}

