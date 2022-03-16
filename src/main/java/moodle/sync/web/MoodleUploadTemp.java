package moodle.sync.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import moodle.sync.web.json.MoodleUpload;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor
public class MoodleUploadTemp {

    public MoodleUpload upload(String name, String uri, String pathname) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(name, uri,
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(pathname)))
                    .build();
            Request request = new Request.Builder()
                    .url("http://localhost/webservice/upload.php?token=f11d219efda839cb5c85bd4e420fa11c")
                    .method("POST", body)
                    .build();
            ResponseBody response = client.newCall(request).execute().body();
            String bodystring = response.string();
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("--------------------------------------" + bodystring);
            List<MoodleUpload> entity = objectMapper.readValue(bodystring , new TypeReference<List<MoodleUpload>>(){});
            System.out.println(entity);
            //String answer[] = response.body().string().split(",");
            return entity.get(0);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
