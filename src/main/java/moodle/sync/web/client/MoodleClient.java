package moodle.sync.web.client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import moodle.sync.web.filter.LoggingFilter;
import moodle.sync.web.json.*;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import java.util.List;

@Path("/webservice/rest/server.php")
@RegisterProviders({
		@RegisterProvider(LoggingFilter.class),
		@RegisterProvider(JsonConfigProvider.class),
})
public interface MoodleClient {

	//Kursübersicht
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	List<Course>  getCourses(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("userid") int userid);

	//Siteinfo abrufen
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	SiteInfo getSiteInfo(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function);

	//Kursinhalte Abrufen
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	List<Section> getCourseContent(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid);

	//Material bewegen, mit beforemod, ohne einfach null übergeben
	@POST
	@Path("")
	void setMoveModule(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("cmid") int cmid, @QueryParam("sectionid") int sectionid, @QueryParam("beforemod") Integer beforemod);

	//Url veröffentlichen
	@POST
	@Path("")
	void setUrl(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid, @QueryParam("sectionnum") int sectionnum, @QueryParam("urlname") String urlname, @QueryParam("url") String url, @QueryParam("beforemod") Integer beforemod);

	//Resource veröffentlichen - nicht hochladen!
	@POST
	@Path("")
    void setResource(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid, @QueryParam("sectionnum") int sectionnum, @QueryParam("itemid") long itemid, @QueryParam("displayname") String displayname, @QueryParam("beforemod") Integer beforemod);

	@POST
	@Path("")
	void setFolder(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid, @QueryParam("sectionnum") int sectionnum, @QueryParam("itemid") long itemid, @QueryParam("displayname") String displayname, @QueryParam("beforemod") Integer beforemod);

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    List<Section> getCourseContentSection(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid, @QueryParam("options[0][name]") String s, @QueryParam("options[0][value]") int sectionid);

	@POST
	@Path("")
    void removeResource(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("cmids[0]") int cmid);
}
