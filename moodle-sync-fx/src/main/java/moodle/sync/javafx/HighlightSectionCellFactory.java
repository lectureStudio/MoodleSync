package moodle.sync.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.util.syncTableElement;

public class HighlightSectionCellFactory implements Callback<TableColumn<syncTableElement, String>, TableCell<syncTableElement, String>> {
    @Override
    public TableCell<syncTableElement, String> call(TableColumn<syncTableElement, String> p) {
        UploadHighlightTableCell<syncTableElement, String> cell = new UploadHighlightTableCell<syncTableElement, String>();
        //cell.setStyle("-fx-font-weight: bold");
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}
