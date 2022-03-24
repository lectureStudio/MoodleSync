package moodle.sync.web.service;

import java.net.URI;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.web.json.*;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import moodle.sync.web.client.MoodleClient;
import org.lecturestudio.core.beans.ChangeListener;
import org.lecturestudio.core.beans.Observable;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.web.api.net.OwnTrustManager;

public class MoodleService {

	private MoodleClient moodleClient;


	/**
	 * Creates a new {@code MoodleService}.
	 *
	 * @param apiUrl The API service connection URL.
	 */
	public MoodleService(StringProperty apiUrl) {
		setApiUrl(apiUrl.get());
	}

	public void setApiUrl(String apiUrl){

		if(apiUrl == null || apiUrl.isEmpty() || apiUrl.isBlank()){
			return;
		}
		RestClientBuilder builder = RestClientBuilder.newBuilder();
		builder.baseUri(URI.create(apiUrl));

		if (apiUrl.startsWith("https")) {
			builder.sslContext(createSSLContext());
			builder.hostnameVerifier((hostname, sslSession) -> hostname
					.equalsIgnoreCase(sslSession.getPeerHost()));
		}

		moodleClient = builder.build(MoodleClient.class);
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

	private static SSLContext createSSLContext() {
		SSLContext sslContext;

		try {
			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, new TrustManager[] { tm }, null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return sslContext;
	}
}
