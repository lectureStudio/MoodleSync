package moodle.sync.web.service;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import moodle.sync.web.json.Course;
import moodle.sync.web.json.Courselist;
import moodle.sync.web.json.Draft;
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

	public String upload() {
		return moodleClient.upload();
	}

	public Draft getDraft() {
		return moodleClient.getDraft("json", "45047a7ae8ceef74553e6da702106396", "core_files_get_unused_draft_itemid");
	}

	public List<Course> getEnrolledCourses(){
		return moodleClient.getCourses("json","45047a7ae8ceef74553e6da702106396","core_enrol_get_users_courses", 2);
	}
}
