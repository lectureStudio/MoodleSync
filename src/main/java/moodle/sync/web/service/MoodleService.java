package moodle.sync.web.service;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import moodle.sync.web.json.*;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import moodle.sync.web.client.MoodleClient;

public class MoodleService {

	private final MoodleClient moodleClient;


	/**
	 * Creates a new {@code MoodleService}.
	 *
	 * @param apiUrl The API service connection URL.
	 */
	@Inject
	public MoodleService(@Named("moodle.api.url") String apiUrl) {
		moodleClient = RestClientBuilder.newBuilder()
				.baseUri(URI.create(apiUrl))
				.build(MoodleClient.class);
	}

	public Draft getDraft() {
		return moodleClient.getDraft("json", "45047a7ae8ceef74553e6da702106396", "core_files_get_unused_draft_itemid");
	}

	public List<Course> getEnrolledCourses(){
		return moodleClient.getCourses("json","45047a7ae8ceef74553e6da702106396","core_enrol_get_users_courses", 2);
	}

	public int getUserId() {
		return moodleClient.getUserId("json","45047a7ae8ceef74553e6da702106396","core_webservice_get_site_info").getUserid();
	}

	public List<Section> getCourseContent(){
		return moodleClient.getCourseContent("json","45047a7ae8ceef74553e6da702106396","core_course_get_contents", 3);
	}

	public void setMoveModule(){
		moodleClient.setMoveModule("json","f11d219efda839cb5c85bd4e420fa11c","local_course_move_module_to_specific_position", 8, 13, 79);
	}

	public void setUrl(){
		moodleClient.setUrl("json", "f11d219efda839cb5c85bd4e420fa11c" ,"local_course_add_new_course_module_url", 3 , 1, "Test aus Java","http://www.moodle.com" , null);
	}

	public void setResource(){
		moodleClient.setResource("json", "f11d219efda839cb5c85bd4e420fa11c", "local_course_add_new_course_module_resource", 3, 1, 113747439, "Resource aus Java", null);
	}

}
