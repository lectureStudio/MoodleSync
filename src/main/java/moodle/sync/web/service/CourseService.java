package moodle.sync.web.service;

import moodle.sync.web.json.Draft;


public class CourseService {

    public static void GetEnrolledCourses(int UserId){
        String domain = "http://localhost/webservice/rest/server.php?&moodlewsrestformat=json";

        MoodleService client = new MoodleService(domain);
        Draft test = client.getDraft();
        System.out.println(test.getItemid());
    }
}
