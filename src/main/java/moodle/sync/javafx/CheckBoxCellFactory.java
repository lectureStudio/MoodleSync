package moodle.sync.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import moodle.sync.util.UploadElement;

public class CheckBoxCellFactory implements Callback {
    @Override
    public TableCell call(Object param) {
        CheckBoxTableCell<UploadElement,Boolean> checkBoxCell = new CheckBoxTableCell();
        return checkBoxCell;
    }
}
