package moodle.sync.util;

public enum MoodleAction{
    MoodleUpload("Datei auf Moodle hochladen"), MoodleSynchronize("Datei auf Moodle aktualisieren"), FTPUpload("Datei auf Fileserver hochladen"), FTPSynchronize("Datei auf Fileserver aktualisieren"), DatatypeNotKnown("Dateiformat nicht gefunden, bitte in Einstellungen anpassen");

    public final String message;

    private MoodleAction(String message) {
        this.message = message;
    }
}