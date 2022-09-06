package moodle.sync.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeDateElement {
    private ObjectProperty<LocalDate> localDate;
    private ObjectProperty<LocalTime> localTime;

    public TimeDateElement(LocalDate localDate, LocalTime localTime) {
        this.localDate = new SimpleObjectProperty(localDate);
        this.localTime = new SimpleObjectProperty(localTime);
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate.set(localDate);
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime.set(localTime);
    }

    public LocalDate getLocalDate() {
        return localDate.get();
    }

    public LocalTime getLocalTime() {
        return localTime.get();
    }

    public ObjectProperty<LocalDate> LocalDateProperty() {
        return localDate;
    }

    public ObjectProperty<LocalTime> LocalTimeProperty() {
        return localTime;
    }
}