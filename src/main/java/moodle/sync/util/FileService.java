package moodle.sync.util;

import moodle.sync.fileserver.FileServerFile;
import moodle.sync.util.UploadData.UploadData;
import moodle.sync.util.UploadData.UploadElement;
import moodle.sync.util.UploadData.UploadFolderElement;
import moodle.sync.web.json.Module;
import moodle.sync.web.json.Section;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing several methods in terms of file handling and comparison.
 *
 * @author Daniel Schröter
 */
public class FileService {


    /**
     * Secures that a directory given in a given path exists. Therefore the directory could be created.
     *
     * @param p Path of the directory.
     */
    public static void directoryManager(Path p) {
        try {
            Files.createDirectories(p);
        } catch (Exception e) {
        }
    }


    /**
     * Obtaining a list containing all paths inside a directory.
     *
     * @param p Path of the directory.
     * @return list of all paths inside the directory.
     */
    public static List<Path> fileServerRequired(Path p) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an IOException.
            throw ex.getCause();
        }
        return result;
    }

    /**
     * Recursive method to retain a list representing a directories-structure.
     *
     * @param p Path of the directory.
     * @return list containing objects of the type UploadData.
     */
    public static List<UploadData> getFilesInDirectory(Path p) throws IOException {
        List<UploadData> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                //When an element is a directory, a recursive-call of this method is made.
                if (Files.isDirectory(entry)) {
                    if (!getFilesInDirectory(entry).isEmpty()) {
                        result.add(new UploadFolderElement(getFilesInDirectory(entry), entry, MoodleAction.DatatypeNotKnown, false));
                    }
                } else {
                    result.add(new UploadElement(entry));
                }
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }

    //Returns List with all Moodle-Moduls of a choosen Module-type

    /**
     * Method to sort the Modules inside a Section by Module-type.
     *
     * @param type1   Module-type searched for.
     * @param section Section containing a list of Modules.
     * @return a list of Modules of the searched Module-type.
     */
    public static List<Module> getModulesByType(String type1, Section section) {
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < section.getModules().size(); i++) {
            if (section.getModules().get(i).getModname().equals(type1)) {
                modules.add(section.getModules().get(i));
            }
        }
        return modules;
    }


    /**
     * Method constructing a UploadElement based of a given local file matching the fileformat for a Moodle-upload.
     *
     * @param module list with Modules to check whether the given file is already uploaded.
     * @param path   Path of the file.
     * @return created UploadElement.
     */
    public static UploadElement CheckMoodleModule(List<Module> module, Path path) throws IOException {
        boolean uploaded = false;
        int ifuploaded = 0;
        for (int i = 0; i < module.size(); i++) {
            //Check if file is already uploaded.
            if (path.getFileName().toString().equals(module.get(i).getContents().get(0).getFilename())) {
                uploaded = true;
                ifuploaded = i;
                long onlinemodified = module.get(i).getContents().get(0).getTimemodified() * 1000;
                long filemodified = Files.getLastModifiedTime(path).toMillis();
                //Check if local file is newer.
                if (filemodified > onlinemodified) {
                    return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.MoodleSynchronize, true);
                }
            }
        }
        if (!uploaded) {
            return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.MoodleUpload, true);
        }
        return null;
    }

    /**
     * Method constructing a UploadElement based of a given local file matching the fileformat for a Fileserver-upload.
     *
     * @param files list containing objects of type FileServerFile to check whether the given file is already uploaded.
     * @param path  Path of the file.
     * @return created UploadElement.
     */
    public static UploadElement CheckFileServerModule(List<FileServerFile> files, Path path) throws IOException {
        boolean uploaded = false;
        int ifuploaded = 0;
        for (int i = 0; i < files.size(); i++) {
            //Check if file is already uploaded.
            if (path.getFileName().toString().equals(files.get(i).getFilename())) {
                uploaded = true;
                ifuploaded = i;
                long onlinemodified = files.get(i).getLastTimeModified();
                long filemodified = Files.getLastModifiedTime(path).toMillis();
                //Check if local file is newer.
                if (filemodified > onlinemodified) {
                    return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.FTPSynchronize, true);
                }
            }
        }
        if (!uploaded) {
            return new UploadElement(path, uploaded, ifuploaded, false, MoodleAction.FTPUpload, true);
        }
        return null;
    }
}

