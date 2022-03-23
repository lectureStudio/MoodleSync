package moodle.sync.util;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.Section;
import org.lecturestudio.core.app.ApplicationContext;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    //Check if a Path exists, else create

    public static void directoryManager(Path p){
        try {
            Files.createDirectories(p);
        }
        catch (Exception e){
        }
    }


    //Returns List with all Paths inside a directory
    public static List<Path> getFilesInDirectory(Path p) throws IOException {
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

    //Returns List with all Moodle-Moduls of a choosen Module-Type
    public static List<Module> getModulesByType(String type1, Section section){
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < section.getModules().size(); i++) {
            if (section.getModules().get(i).getModname().equals(type1)) {
                modules.add(section.getModules().get(i));
            }
        }
        return modules;
    }

    //Method, checks whether File by Format "MoodleFormat" is uploaded, if yes, check if its newer if not uploaded add it
    public static UploadElement CheckMoodleModule(List<Module> module, Path path) throws IOException {
        boolean uploaded = false;
        int ifuploaded = 0;
        for (int i = 0; i < module.size(); i++) {
            if (path.getFileName().toString().equals(module.get(i).getContents().get(0).getFilename())) {
                uploaded = true;
                ifuploaded = i;
                Long onlinemodified = module.get(i).getContents().get(0).getTimemodified() * 1000;
                Long filemodified = Files.getLastModifiedTime(path).toMillis();
                if (filemodified > onlinemodified) {
                    return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.MoodleSynchronize, true);

                }
            }
        }
        if(uploaded == false) {
            return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.MoodleUpload, true);
        }

        return null;
    }

    //Method, checks whether File by Format "MoodleFormat" is uploaded, if yes, check if its newer if not uploaded add it
    public static UploadElement CheckFileServerModule(List<FileServerFile> files, Path path) throws IOException {
        boolean uploaded = false;
        int ifuploaded = 0;
        for (int i = 0; i < files.size(); i++) {
            if (path.getFileName().toString().equals(files.get(i).getFilename())) {
                uploaded = true;
                ifuploaded = i;
                Long onlinemodified = files.get(i).getLastTimeModified();
                Long filemodified = Files.getLastModifiedTime(path).toMillis();
                if (filemodified > onlinemodified) {
                    return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.FTPSynchronize, true);

                }
            }
        }
        if(uploaded == false) {
            return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.FTPUpload, true);
        }

        return null;
    }
}

