package moodle.sync.web.client;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import moodle.sync.web.filter.AuthorizationFilter;
import moodle.sync.web.json.Draft;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import moodle.sync.web.json.JsonConfigProvider;

@Path("/api")
@RegisterProviders({
        @RegisterProvider(JsonConfigProvider.class),
        @RegisterProvider(AuthorizationFilter.class),
})
public interface CourseClient {

    @POST
    @Path("/wsfunction=core_files_get_unused_draft_itemid")
    Draft getDraft();

}
