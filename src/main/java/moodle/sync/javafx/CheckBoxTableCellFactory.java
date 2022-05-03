package moodle.sync.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import moodle.sync.util.UploadElementTableItem;

/**
 * Class implementing a Checkbox as the content of a TableCell.
 *
 * @author Daniel Schröter
 */
public class CheckBoxTableCellFactory implements Callback<TreeTableColumn<UploadElementTableItem, Boolean>, TreeTableCell<UploadElementTableItem, Boolean>> {
    @Override
    public TreeTableCell<UploadElementTableItem, Boolean> call(TreeTableColumn<UploadElementTableItem, Boolean> p) {
        UploadCheckBoxCell<UploadElementTableItem, Boolean> cell = new UploadCheckBoxCell<UploadElementTableItem, Boolean>();
        cell.setAlignment(Pos.CENTER);

        return cell;
    }
}