package moodle.sync.util.UploadData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.util.MoodleAction;

import java.nio.file.Path;

@Getter
@Setter
@NoArgsConstructor
public class UploadElement extends UploadData{

    private Path path;

    private boolean uploaded;

    private int ifuploaded;

    private BooleanProperty checked;

    private MoodleAction action;

    private boolean selectable;

    private StringProperty fileName;


    public UploadElement(Path path){
        this.path = path;
    }

    public UploadElement(Path path, boolean uploaded, int ifuploaded, boolean checked, MoodleAction action, boolean selectable){
        this.path = path;
        this.uploaded = uploaded;
        this.ifuploaded = ifuploaded;
        this.checked = new SimpleBooleanProperty(checked);
        this.action = action;
        this.selectable = selectable;
        this.fileName = new SimpleStringProperty(path.getFileName().toString());
    }

    public BooleanProperty getChecked() {
        return this.checked;
    }

    public void setChecked(final java.lang.Boolean checked) {
        this.checked=new SimpleBooleanProperty(checked);
    }

    public java.lang.Boolean getSelectable(){
        return this.selectable;
    }

    public StringProperty getFileName() {
        return this.fileName;
    }

    public void setFileName(final java.lang.String fileName) {
        this.fileName=new SimpleStringProperty(fileName);
    }

    public String getFileNameAsString(){
        return this.fileName.get();
    }


}
