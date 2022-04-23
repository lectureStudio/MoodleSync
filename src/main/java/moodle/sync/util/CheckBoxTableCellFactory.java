package moodle.sync.util;

import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import moodle.sync.javafx.UploadCheckBoxCell;


public class CheckBoxTableCellFactory implements Callback<TreeTableColumn<UploadElementTableItem,Boolean>,TreeTableCell<UploadElementTableItem,Boolean>> {
    @Override
    public TreeTableCell<UploadElementTableItem, Boolean> call(TreeTableColumn<UploadElementTableItem, Boolean> p) {
        UploadCheckBoxCell<UploadElementTableItem, Boolean> cell = new UploadCheckBoxCell<UploadElementTableItem, Boolean>();
        cell.setAlignment(Pos.CENTER);

        return cell;
    }

}