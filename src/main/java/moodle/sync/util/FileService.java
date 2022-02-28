package moodle.sync.util;

import moodle.sync.config.MoodleSyncConfiguration;
import org.lecturestudio.core.app.ApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {

    public FileService(){
    }
    //Check if a Path exists

    public void DirectoryManager(Path p){
        try {
            Files.createDirectories(p);
        }
        catch (Exception e){
        }
    }
}
