package moodle.sync.web.service;

import moodle.sync.web.client.MoodleUploadClient;
import moodle.sync.web.json.MoodleUpload;
import moodle.sync.web.json.MultipartBody;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URI;

public class MoodleUploadService {

    private final MoodleUploadClient moodleUploadClient;


    /**
     * Creates a new {@code MoodleService}.
     *
     * @param uploadUrl The files URL.
     */
    @Inject
    public MoodleUploadService(@Named("moodle.upload.url") String uploadUrl) {
        moodleUploadClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(uploadUrl))
                .build(MoodleUploadClient.class);
    }

    public String setFile(File file) throws Exception {
        try (ByteArrayInputStream test = new ByteArrayInputStream(FileUtils.readFileToByteArray(file))) {
            MultipartBody body = new MultipartBody();
            System.out.println(test.available());
            body.addFormData("file",
                    test, MediaType.MULTIPART_FORM_DATA_TYPE, "Fileupload.pdf");

            return moodleUploadClient.setFile("2e43a0cc7c9f536e26df55db90d2afdb", body);
        }


    }
}
