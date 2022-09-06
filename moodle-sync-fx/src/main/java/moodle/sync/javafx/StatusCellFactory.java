package moodle.sync.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.syncTableElement;

public class StatusCellFactory implements Callback<TableColumn<syncTableElement, String>, TableCell<syncTableElement, String>> {
    @Override
    public TableCell<syncTableElement, String> call(TableColumn<syncTableElement, String> p){
        StatusTableCell<syncTableElement, String> cell = new StatusTableCell<>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}