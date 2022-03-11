package moodle.sync.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import moodle.sync.util.UploadElement;

import java.nio.file.Path;
import java.util.Objects;

public class UploadElementTableItem {

    private final UploadElement uploadElement;

    private final StringProperty fileName;

    private Boolean checked;

    public UploadElementTableItem(UploadElement uploadElement){
        this.fileName = new SimpleStringProperty(uploadElement.getPath().getFileName().toString());
        this.uploadElement = uploadElement;
        this.checked = false;
    }


    public UploadElement getUploadElement() {
        return uploadElement;
    }

    public String getFilename() {
        return fileName.get();
    }

    public void setFileName(String name) {
        fileName.set(name);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checken) {
        this.checked = checken;
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
