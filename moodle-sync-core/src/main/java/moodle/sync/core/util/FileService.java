package moodle.sync.core.util;

import moodle.sync.core.model.json.Module;
import moodle.sync.core.model.json.Section;

import java.io.File;
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


    public static List<Path> formatSectionFolder(List<Path> sectionList, Section section){
        int remove = -1;
        for(int i = 0; i < sectionList.size(); i++){
            String[] sectionFolder = sectionList.get(i).getFileName().toString().split("_", 2);
            if(sectionFolder[sectionFolder.length-1].equals(section.getName())){
                File temp = new File(sectionList.get(i).toString());
                temp.renameTo(new File((sectionList.get(i).getParent().toString() + "/" + section.getSection() + "_" + section.getName())));
                remove = i;
                break;
            }
        }
        if(remove != -1){
            sectionList.remove(remove);
        }
        return sectionList;
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


    public static List<Path> getPathsInDirectory(Path p) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                //When an element is a directory, a recursive-call of this method is made.
                result.add(entry);
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
}

