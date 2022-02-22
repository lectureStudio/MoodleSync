package moodle.sync.web.client;

import moodle.sync.web.filter.LoggingFilter;
import moodle.sync.web.json.JsonConfigProvider;
import moodle.sync.web.json.MoodleUpload;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

@Path("upload.php")
@RegisterProviders({
        @RegisterProvider(LoggingFilter.class),
        @RegisterProvider(JsonConfigProvider.class),
})

public interface MoodleUploadClient {
    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    String setFile(@QueryParam("token") String token, @FormParam("filename") InputStream data);
}
