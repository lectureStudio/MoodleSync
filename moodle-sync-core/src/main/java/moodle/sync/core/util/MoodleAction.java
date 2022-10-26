package moodle.sync.core.util;

/**
 * Enumeration containing several entities describing actions to do with UploadData.
 *
 * @author Daniel Schröter
 */
public enum MoodleAction {
    MoodleUpload("Datei auf Moodle hochladen"), MoodleSynchronize("Datei auf Moodle aktualisieren"), FTPUpload("Datei auf Fileserver hochladen"), FTPSynchronize("Datei auf Fileserver aktualisieren"), FTPLink("Datei mit Moodle verlinken"), NotLocalFile("Datei nicht lokal verfügbar"),ExistingFile("Datei ist in aktuellen Zustand auf der MoodlePlattform"),DatatypeNotKnown("Dateiformat nicht gefunden, bitte in Einstellungen anpassen"),ExistingSection("Bestehende Section"),UploadSection("Neue Sektion anlegen");

    public final String message;

    MoodleAction(String message) {
        this.message = message;
    }
}