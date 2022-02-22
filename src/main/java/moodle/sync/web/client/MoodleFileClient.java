package moodle.sync.web.client;

import moodle.sync.web.filter.LoggingFilter;
import moodle.sync.web.json.JsonConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import javax.ws.rs.*;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

@Path("")
@RegisterProviders({
        @RegisterProvider(LoggingFilter.class),
        @RegisterProvider(JsonConfigProvider.class),
})
public interface MoodleFileClient {
    @GET
    @Path("")
    @Produces(APPLICATION_OCTET_STREAM)
    InputStream getFile(@QueryParam("token") String token);

}
