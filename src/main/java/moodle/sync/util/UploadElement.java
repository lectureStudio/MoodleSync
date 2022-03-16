package moodle.sync.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@NoArgsConstructor
public class UploadElement {

    private Path path;

    private boolean uploaded;

    private int ifuploaded;

    private BooleanProperty checked;

    private MoodleAction action;

    private boolean selectable;

    public UploadElement(Path path, boolean uploaded, int ifuploaded, boolean checked, MoodleAction action, boolean selectable){
        this.path = path;
        this.uploaded = uploaded;
        this.ifuploaded = ifuploaded;
        this.checked = new SimpleBooleanProperty(checked);
        this.action = action;
        this.selectable = selectable;
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
}
