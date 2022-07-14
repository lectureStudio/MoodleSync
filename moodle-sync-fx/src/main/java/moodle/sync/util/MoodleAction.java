package moodle.sync.util;

/**
 * Enumeration containing several entities describing actions to do with UploadData.
 *
 * @author Daniel Schröter
 */
public enum MoodleAction {
    MoodleUpload("Datei auf Moodle hochladen"), MoodleSynchronize("Datei auf Moodle aktualisieren"), FTPUpload("Datei auf Fileserver hochladen"), FTPSynchronize("Datei auf Fileserver aktualisieren"), DatatypeNotKnown("Dateiformat nicht gefunden, bitte in Einstellungen anpassen");

    public final String message;

    MoodleAction(String message) {
        this.message = message;
    }
}