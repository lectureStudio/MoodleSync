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

    private int uploaded;

    private int ifuploaded;

    private boolean checked;

    public UploadElement(Path path, int uploaded, int ifuploaded){
        this.path = path;
        this.uploaded = uploaded;
        this.ifuploaded = ifuploaded;
        this.checked = false;
    }

    public java.lang.Boolean getChecked() {
        return this.checked;
    }

    public void setChecked(final java.lang.Boolean checked) {
        this.checked=checked;
    }
}
