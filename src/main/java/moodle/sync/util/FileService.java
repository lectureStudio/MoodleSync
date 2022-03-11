package moodle.sync.util;

import moodle.sync.config.MoodleSyncConfiguration;
import org.lecturestudio.core.app.ApplicationContext;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    public FileService(){
    }

    //Check if a Path exists, else create

    public void directoryManager(Path p){
        try {
            Files.createDirectories(p);
        }
        catch (Exception e){
        }
    }

    //Returns List with all Filenames inside a directory
    public List<String> getFileNamesInDirectory(Path p){
        List<String> fileNames = new ArrayList<>();
        try {
            DirectoryStream<Path> filesInDirectory = Files.newDirectoryStream(p);
            filesInDirectory.forEach(path -> fileNames.add(path.getFileName().toString()));
        }
        catch (Exception e){
        }
        return fileNames;
    }

    public List<Path> getFilesInDirectory(Path p) throws IOException {
        List<Path> result = new ArrayList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry: stream) {
                result.add(entry);
            }
        }
        catch (DirectoryIteratorException ex){
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }
}
