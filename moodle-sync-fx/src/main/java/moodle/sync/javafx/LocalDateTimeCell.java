package moodle.sync.javafx;

import com.dlsc.gemsfx.TimePicker;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.TimeDateElement;
import moodle.sync.util.syncTableElement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeCell<S, U> extends TableCell<syncTableElement, TimeDateElement> {

    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public void updateItem(TimeDateElement item, boolean empty) {
        this.datePicker = new DatePicker();
        this.timePicker = new TimePicker();
        setAlignment(Pos.CENTER);
        datePicker.setMaxWidth(100);
        timePicker.setMaxWidth(100);
        HBox hbox = new HBox(datePicker, timePicker);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        setGraphic(hbox);

        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (getTableRow() != null && getTableRow().getItem() != null) {
            datePicker.valueProperty().unbindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalDateProperty());
            timePicker.timeProperty().unbindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalTimeProperty());
            if (getTableRow().getItem() != null && (!getTableRow().getItem().isSelectable() || getTableRow().getItem().getAction() == MoodleAction.UploadSection)) {
                setDisable(false);
                setGraphic(null);
            }
            else {
                if (getTableRow().getItem() != null && getTableRow().getItem().isSelectable()) {
                    datePicker.valueProperty().bindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalDateProperty());
                    timePicker.timeProperty().bindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalTimeProperty());
                }
            }
        }
    }
}
