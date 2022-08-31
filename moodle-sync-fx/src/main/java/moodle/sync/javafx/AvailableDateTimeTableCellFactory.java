package moodle.sync.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.util.TimeDateElement;
import moodle.sync.util.syncTableElement;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AvailableDateTimeTableCellFactory implements Callback<TableColumn<syncTableElement, TimeDateElement>, TableCell<syncTableElement, TimeDateElement>> {
    @Override
    public TableCell<syncTableElement, TimeDateElement> call(TableColumn<syncTableElement, TimeDateElement> p) {
        LocalDateTimeCell<syncTableElement, TimeDateElement> cell = new LocalDateTimeCell<syncTableElement, TimeDateElement>();
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}