package moodle.sync.web.service;

import java.net.URI;
import java.security.KeyStore;
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

	public Draft getDraft(String token) {
		return moodleClient.getDraft("json", token, "core_files_get_unused_draft_itemid");
	}

	public List<Course> getEnrolledCourses(String token, int userid){
		return moodleClient.getCourses("json",token,"core_enrol_get_users_courses", userid);
	}

	public int getUserId(String token) {
		SiteInfo info = moodleClient.getSiteInfo("json", token,"core_webservice_get_site_info");
		return info.getUserid();
	}

	public List<Section> getCourseContent(String token, int courseid){
		return moodleClient.getCourseContent("json",token,"core_course_get_contents", courseid);
	}

	public List<Section> getCourseContentSection(String token, int courseid, int sectionid){
		return moodleClient.getCourseContentSection("json",token,"core_course_get_contents", courseid, "sectionid", sectionid);
	}

	public void setMoveModule(String token, int cmid, int sectionid, int beforemod){
		moodleClient.setMoveModule("json",token,"local_course_move_module_to_specific_position", cmid, sectionid, beforemod);
	}

	public void setUrl(String token, int courseid, int section, String urlname, String url){
		moodleClient.setUrl("json", token ,"local_course_add_new_course_module_url", courseid , section, urlname, url, null);
	}

	public void setResource(String token, int courseid, int sectionid, Long itemid, String name){
		moodleClient.setResource("json", token, "local_course_add_new_course_module_resource", courseid,  sectionid,  itemid,  name, null);
	}

	public void setResource(String token, int courseid, int sectionid, Long itemid, String name, int beforemod){
		moodleClient.setResource("json", token, "local_course_add_new_course_module_resource", courseid,  sectionid,  itemid,  name, beforemod);
	}

	public void removeResource(String token, int cmids){
		moodleClient.removeResource("json", token, "core_course_delete_modules", cmids);
	}

}
