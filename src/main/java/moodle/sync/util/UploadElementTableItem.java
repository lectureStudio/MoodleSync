package moodle.sync.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class UploadElementTableItem {

    private StringProperty fileName;

    private BooleanProperty selected;

    private StringProperty message;

    private UploadElement uploadElement;

    public UploadElementTableItem(String fileName, boolean selected, String message, UploadElement uploadElement){
        this.fileName = new SimpleStringProperty(fileName);
        this.selected = new SimpleBooleanProperty(selected);
        this.message = new SimpleStringProperty(message);
        this.uploadElement = uploadElement;
    }

    public UploadElementTableItem(UploadElement uploadElement){
        this.fileName = new SimpleStringProperty(uploadElement.getPath().getFileName().toString());
        this.selected = new SimpleBooleanProperty(uploadElement.getChecked());
        this.message = new SimpleStringProperty(uploadElement.getAction().message);
        this.uploadElement = uploadElement;
    }
    public UploadElementTableItem(){
        this(null, false,null, null);
    }

    public StringProperty fileNameProperty() { return fileName; }

    public String getFileName() { return this.fileName.get(); }

    public void setFileName(String value) { this.fileName.set(value); }

    public StringProperty messageProperty() { return message; }

    public String getMessage() { return this.message.get(); }

    public void setMessage(String value) { this.message.set(value); }

    public BooleanProperty selectedProperty() { return selected; }

    public boolean isSelected() { return this.selected.get(); }

    public void setSelected(boolean value) { this.selected.set(value); }

    public UploadElement getUploadElement(){
        uploadElement.setChecked(isSelected());
        return uploadElement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        UploadElementTableItem other = (UploadElementTableItem) obj;

        return Objects.equals(fileName, other.fileName);
    }
}
