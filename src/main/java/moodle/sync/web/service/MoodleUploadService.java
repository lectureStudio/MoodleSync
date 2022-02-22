package moodle.sync.web.service;

import moodle.sync.web.client.MoodleUploadClient;
import moodle.sync.web.client.MultipartBody;
import moodle.sync.web.json.MoodleUpload;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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

    public String setFile(InputStream file){
        return moodleUploadClient.setFile("2e43a0cc7c9f536e26df55db90d2afdb", file);
    }
}
