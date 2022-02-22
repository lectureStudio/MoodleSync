package moodle.sync.web.client;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import moodle.sync.web.filter.LoggingFilter;
import moodle.sync.web.json.*;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import java.util.List;

@Path("/server.php")
@RegisterProviders({
		@RegisterProvider(LoggingFilter.class),
		@RegisterProvider(JsonConfigProvider.class),
})
public interface MoodleClient {

	//Draftarea für Upload
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	Draft getDraft(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function);

	//Kursübersicht
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	List<Course>  getCourses(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("userid") int userid);

	//Userid abrufen
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	UserId getUserId(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function);

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
	void setResource(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid, @QueryParam("sectionnum") int sectionnum, @QueryParam("itemid") int itemid, @QueryParam("displayname") String displayname, @QueryParam("beforemod") Integer beforemod);
}
