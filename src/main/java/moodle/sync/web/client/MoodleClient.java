package moodle.sync.web.client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import moodle.sync.web.filter.LoggingFilter;
import moodle.sync.web.json.Course;
import moodle.sync.web.json.Courselist;
import moodle.sync.web.json.Draft;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import moodle.sync.web.json.JsonConfigProvider;

import java.util.List;

@Path("/server.php")
@RegisterProviders({
		@RegisterProvider(LoggingFilter.class),
		@RegisterProvider(JsonConfigProvider.class),
})
public interface MoodleClient {

	@GET
	@Path("/upload")
	String upload();

	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	Draft getDraft(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function);

	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	List<Course>  getCourses(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam("userid") int userid);
}
