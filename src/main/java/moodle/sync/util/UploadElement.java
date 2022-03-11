package moodle.sync.util;

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

    private boolean checked;

    public UploadElement(Path path, boolean uploaded, int ifuploaded, boolean checked){
        this.path = path;
        this.uploaded = uploaded;
        this.ifuploaded = ifuploaded;
        this.checked = checked;
    }

    public java.lang.Boolean getChecked() {
        return this.checked;
    }

    public void setChecked(final java.lang.Boolean checked) {
        this.checked=checked;
    }
}
