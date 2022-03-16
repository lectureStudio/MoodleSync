package moodle.sync.util;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import moodle.sync.javafx.UploadCheckBoxCell;


public class CheckBoxTableCellFactory implements Callback<TableColumn<UploadElementTableItem, Boolean>, TableCell<UploadElementTableItem, Boolean>> {
    public TableCell<UploadElementTableItem, Boolean> call(TableColumn<UploadElementTableItem, Boolean> param) {

        UploadCheckBoxCell cell = new UploadCheckBoxCell();
        cell.setStyle("-fx-alignment: CENTER;");
        return cell;
    }
}

