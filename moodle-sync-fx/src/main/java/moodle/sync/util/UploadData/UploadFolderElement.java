package moodle.sync.util.UploadData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.util.MoodleAction;

import java.nio.file.Path;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
/**
 * Class representing a directory in the sync. process.
 *
 * @author Daniel Schröter
 */
public class UploadFolderElement extends UploadData {

    //Directories content.
    private List<UploadData> content;

    //Local path.
    private Path path;

    //Declares how to handle the whole directory during the sync. process.
    private MoodleAction action;

    //If the directory is uploaded, this identifies the corresponding course-module.
    private int ifuploaded;

    //Property used for the CheckBoxes in the "sync-page".
    private BooleanProperty checked;

    public UploadFolderElement(List<UploadData> elements, Path path, MoodleAction action, boolean checked) {
        this.content = elements;
        this.path = path;
        this.action = action;
        this.checked = new SimpleBooleanProperty(checked);
    }

    /**
     * Provides the checked property.
     *
     * @return checked property.
     */
    public BooleanProperty getChecked() {
        return this.checked;
    }

    /**
     * Sets the checked property.
     *
     * @param checked True if the file is checked.
     */
    public void setChecked(final java.lang.Boolean checked) {
        this.checked = new SimpleBooleanProperty(checked);
    }
}
