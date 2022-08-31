package moodle.sync.util.FileWatcherService;

import moodle.sync.util.FileWatcherService.FileEvent;

import java.util.EventListener;

public interface FileListener extends EventListener {

    default void onCreated(FileEvent event) {
        System.out.println("Erstellt" + event.getFile().toString());
        event.getFile();
    }

    default void onModified(FileEvent event) {
        System.out.println("Geändert");
    }

    default void onDeleted(FileEvent event) {
        System.out.println("Gelöscht");
    }
}
