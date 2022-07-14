package moodle.sync.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.UploadData.UploadFolderElement;

import java.util.Objects;

/**
 * Class used to represent an entry inside the "snyc-page"-table.
 *
 * @author Daniel Schröter
 */
public class UploadElementTableItem {

    //The represented files name.
    private StringProperty fileName;

    //Representing the state of the corresponding CheckBox.
    private BooleanProperty selected;

    //Displayed Message.
    private StringProperty message;

    //The UploadElement this entry should represent.
    private UploadElement uploadElement;

    //If the corresponding CheckBox should be functional.
    private BooleanProperty selectable;

    public UploadElementTableItem(String fileName, boolean selected, String message, UploadElement uploadElement, boolean selectable) {
        this.fileName = new SimpleStringProperty(fileName);
        this.selected = new SimpleBooleanProperty(selected);
        this.message = new SimpleStringProperty(message);
        this.uploadElement = uploadElement;
        this.selectable = new SimpleBooleanProperty(selectable);
    }

    public UploadElementTableItem(UploadElement uploadElement) {
        this.fileName = uploadElement.getFileName();
        this.selected = uploadElement.getChecked();
        this.message = new SimpleStringProperty(uploadElement.getAction().message);
        this.uploadElement = uploadElement;
        this.selectable = new SimpleBooleanProperty(uploadElement.getSelectable());
    }

    public UploadElementTableItem(UploadFolderElement element, String message, boolean selectable) {
        this.fileName = new SimpleStringProperty(element.getPath().getFileName().toString());
        this.selected = element.getChecked();
        this.message = new SimpleStringProperty(message);
        this.uploadElement = new UploadElement();
        this.selectable = new SimpleBooleanProperty(selectable);
    }

    public UploadElementTableItem(String name, String message) {
        this(name, false, message, new UploadElement(), false);
    }

    /**
     * Providing the fileNameProperty.
     *
     * @return the fileNameProprty.
     */
    public StringProperty fileNameProperty() {
        return fileName;
    }

    /**
     * Providing the files name as a String.
     *
     * @return the files name as a String.
     */
    public String getFileName() {
        return this.fileName.get();
    }

    /**
     * Sets a new fileName.
     *
     * @param value the new fileName.
     */
    public void setFileName(String value) {
        this.fileName.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getMessage() {
        return this.message.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setMessage(String value) {
        this.message.set(value);
    }

    /**
     * Providing the selectedProperty.
     *
     * @return the selectedProperty.
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Proving whether the element is selected or not.
     *
     * @return True if the element is selected.
     */
    public boolean isSelected() {
        return this.selected.get();
    }

    /**
     * Sets the selectedProperty.
     *
     * @param value if the object is selected.
     */
    public void setSelected(boolean value) {
        this.selected.set(value);
    }

    /**
     * Providing the selectableProperty.
     *
     * @return the selectableProperty.
     */
    public BooleanProperty selectableProperty() {
        return selectable;
    }

    /**
     * Providing whether the object should be selectable.
     *
     * @return True if it should be selectable.
     */
    public boolean isSelectable() {
        return this.selectable.get();
    }

    /**
     * Sets the selectableProperty.
     *
     * @param value if the object should be selectable.
     */
    public void setSelectable(boolean value) {
        this.selectable.set(value);
    }

    /**
     * Provides the represented UploadElement.
     *
     * @return the represented UploadElement.
     */
    public UploadElement getUploadElement() {
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
