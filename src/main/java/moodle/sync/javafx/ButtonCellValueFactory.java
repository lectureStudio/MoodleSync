package moodle.sync.javafx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import static java.util.Objects.nonNull;

public class ButtonCellValueFactory implements Callback<TableColumn.CellDataFeatures<Object, Boolean>, ObservableValue<Boolean>> {

    @Override
    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Object, Boolean> param) {
        return new SimpleBooleanProperty(nonNull(param.getValue()));
    }

}