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

/**
 * Class representing a file in the sync. process.
 *
 * @author Daniel Schröter
 */
public class UploadElement extends UploadData {

    //Local filepath.
    private Path path;

    //True if this file is already uploaded.
    private boolean uploaded;

    //If the file is uploaded, this identifies the corresponding course-module.
    private int ifuploaded;

    //Property used for the CheckBoxes in the "sync-page".
    private BooleanProperty checked;

    //Depending on fileformat, this declares how to handle a file.
    private MoodleAction action;

    //Used for the CheckBoxes in the "sync-page". Declares whether this file should be checkable.
    private boolean selectable;

    //Local filename.
    private StringProperty fileName;

    public UploadElement(Path path) {
        this.path = path;
    }

    public UploadElement(Path path, boolean uploaded, int ifuploaded, boolean checked, MoodleAction action, boolean selectable) {
        this.path = path;
        this.uploaded = uploaded;
        this.ifuploaded = ifuploaded;
        this.checked = new SimpleBooleanProperty(checked);
        this.action = action;
        this.selectable = selectable;
        this.fileName = new SimpleStringProperty(path.getFileName().toString());
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

    /**
     * Check if the file should be selectable.
     *
     * @return True if it should be selectable.
     */
    public java.lang.Boolean getSelectable() {
        return this.selectable;
    }

    /**
     * Provides the fileName property.
     *
     * @return the fileName property.
     */
    public StringProperty getFileName() {
        return this.fileName;
    }

    /**
     * Sets the fileName property.
     *
     * @param fileName the fileName to set.
     */
    public void setFileName(final java.lang.String fileName) {
        this.fileName = new SimpleStringProperty(fileName);
    }

    /**
     * Provides the filename as a string.
     *
     * @return filename as a string.
     */
    public String getFileNameAsString() {
        return this.fileName.get();
    }


}
