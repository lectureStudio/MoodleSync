package moodle.sync.util.UploadData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import moodle.sync.util.MoodleAction;
import moodle.sync.util.UploadData.UploadData;

import java.nio.file.Path;
import java.util.List;

@Getter
@Setter
public class UploadFolderElement extends UploadData {

    private List<UploadData> content;

    private Path path;

    private MoodleAction action;

    private int ifuploaded;

    private BooleanProperty checked;

    public UploadFolderElement(List<UploadData> elements, Path path, MoodleAction action, boolean checked){
        this.content = elements;
        this.path = path;
        this.action = action;
        this.checked = new SimpleBooleanProperty(checked);
    }

    public UploadFolderElement(){
    }

    public BooleanProperty getChecked() {
        return this.checked;
    }

    public void setChecked(final java.lang.Boolean checked) {
        this.checked=new SimpleBooleanProperty(checked);
    }
}
