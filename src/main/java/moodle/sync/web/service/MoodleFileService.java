package moodle.sync.web.service;

import moodle.sync.web.client.MoodleFileClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.net.URI;

public class MoodleFileService {

    private final MoodleFileClient moodleFileClient;


    /**
     * Creates a new {@code MoodleService}.
     *
     * @param fileUrl The files URL.
     */
    @Inject
    public MoodleFileService(String fileUrl) {
        moodleFileClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(fileUrl))
                .build(MoodleFileClient.class);
    }

    public InputStream getDownload(){
        return moodleFileClient.getFile("31a1f216fd60a5be89c6a25debe82505");
    }
}
