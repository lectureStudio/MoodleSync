package moodle.sync.cli;

import moodle.sync.cli.inject.ApplicationModule;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.MoodleUpload;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.web.service.MoodleUploadTemp;
import org.apache.commons.cli.*;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.app.dictionary.Dictionary;
import org.lecturestudio.core.inject.GuiceInjector;
import org.lecturestudio.core.inject.Injector;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class CommandLineInterface {

    private final MoodleService moodleService;

    private final Dictionary dictionary;

    private CommandLine cmd;

    private String url;

    private String token;

    private int userId;

    private Course course;

    private Section section;

    public static void main(String[] args) {

        Injector injector = new GuiceInjector(new ApplicationModule());

        CommandLineInterface cliTool = injector.getInstance(CommandLineInterface.class);

        cliTool.initialize(args);
    }

    @Inject
    public CommandLineInterface(MoodleService moodleService, Dictionary dictionary) {
        this.moodleService = moodleService;
        this.dictionary = dictionary;
    }

    public void initialize(String[] args) {

        allArgsCli(args);

    }

    private void allArgsCli(String[] args) {

        try {
            Options options = generateDefaultOptions();
            cmd = parseCommandLine(options, args);
            //Hiermit schauen ob Parameter überhaupt gegeben
            if (!cmd.hasOption("c") || !cmd.hasOption("s") || !cmd.hasOption("p") || !cmd.hasOption("l")) {
                System.err.println(dictionary.get("cli.args.not.complete"));
                printHelp();
                return;
            }
            //Falls Parameter gegeben aber kein Arg, wird automatisch exception geworfen
        } catch (Exception exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            printHelp();
            return;
        }

        //Check properties file

        if(!checkProperties(cmd.getOptionValue("l"))) return;

        //initialize the moodleService

        moodleService.setApiUrl(url);

        //Nach Überprüfung der Parameter -> Kursliste abfragen und checken ob Kurs vorhanden
        try {
            userId = moodleService.getUserId(token);
        } catch (Exception e) {
            System.err.println(dictionary.get("cli.moodle.connection.failed"));
            return;
        }


        //Getting the dedicated course
        try {
            course = findCourseById(cmd.getOptionValue("course"));
        } catch (Exception e) {
            System.err.println(dictionary.get("cli.moodle.external.service"));
            return;
        }
        if (course == null) {
            System.err.println(dictionary.get("cli.moodle.course"));
            return;
        }

        //Getting the dedicated section
        try {
            section = findSectionById(cmd.getOptionValue("section"));
        } catch (Exception e) {
            System.err.println(dictionary.get("cli.moodle.section"));
            return;
        }

        //Check filepath
        Path file = Path.of(cmd.getOptionValue("path"));
        try {
            if (!checkFilePath(file)) {
                System.err.println(dictionary.get("cli.path.unvalid"));
                return;
            }
        } catch (Exception e) {
            System.err.println(dictionary.get("cli.path.unvalid"));
            return;
        }

        //Execute file upload
        try {
            moodleService.setResource(token, course.getId(), section.getSection(), uploadFile(file).getItemid(), null, true, file.getFileName().toString(), -1);
        } catch (Exception e) {
            System.err.println(dictionary.get("cli.moodle.upload"));
        }
    }

    private CommandLine parseCommandLine(Options options, String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private Options generateDefaultOptions() {
        Options options = new Options();
        options.addOption("c", "course", true, dictionary.get("cli.option.course"));
        options.addOption("s", "section", true, dictionary.get("cli.option.section"));
        options.addOption("p", "path", true, dictionary.get("cli.option.path"));
        options.addOption("l", "log-in", true, dictionary.get("cli.option.login"));
        return options;
    }

    private MoodleUpload uploadFile(Path file) throws Exception {
        MoodleUploadTemp uploader = new MoodleUploadTemp();
        return uploader.upload(file.getFileName().toString(), file.toString(), url, token);
    }

    private Boolean checkFilePath(Path file) throws Exception {
        return Files.exists(file);
    }

    private Section findSectionById(String id) throws Exception {
        return moodleService.getCourseContentSection(token, course.getId(), Integer.parseInt(id)).get(0);
    }

    private Course findCourseById(String id) throws Exception {
        List<Course> courses = getCourseList();
        Course courseById = null;
        for (Course elem : courses) {
            if (elem.getId().toString().equals(id)) {
                courseById = elem;
                break;
            }
        }
        return courseById;
    }

    private List<Course> getCourseList() throws Exception {
        List<Course> courses = List.of();
        courses = moodleService.getEnrolledCourses(token, userId);
        return courses;
    }

    private boolean checkProperties(String path) {
        if (path == null) {
            System.err.println(dictionary.get("cli.properties.unvalid"));
            return false;
        }

        Properties moodleProps = new Properties();
        try {
            moodleProps.load(new FileInputStream(Path.of(path).toFile()));
        } catch (IOException e) {
            System.err.println(dictionary.get("cli.properties.buggy"));
            return false;
        }

        url = moodleProps.getProperty("url");
        token = moodleProps.getProperty("token");
        if (url == null || token == null) {
            System.err.println(dictionary.get("cli.properties.wrong"));
            return false;
        }

        return true;
    }

    private void printHelp(){
        String courseHeader = dictionary.get("cli.help.title");
        String footer = "";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(" ", courseHeader, generateDefaultOptions(), footer, true);
    }

}
