package moodle.sync.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import moodle.sync.web.json.MoodleUpload;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

@NoArgsConstructor
public class MoodleUploadTemp {


    public MoodleUpload upload(String name, String pathname, String moodleUrl, String token){
        try {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (moodleUrl.startsWith("https")) {
                X509TrustManager trustManager;
                SSLSocketFactory sslSocketFactory;
                try{
                    trustManager = createTrustmanager();
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch(GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                builder.sslSocketFactory(sslSocketFactory, trustManager);
                builder.hostnameVerifier((hostname, sslSession) -> hostname
                        .equalsIgnoreCase(sslSession.getPeerHost()));

            }

            OkHttpClient client = builder.build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(name, pathname,
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(pathname)))
                    .build();
            Request request = new Request.Builder()
                    .url(moodleUrl + "/webservice/upload.php?token=" + token)
                    .method("POST", body)
                    .build();
            ResponseBody response = client.newCall(request).execute().body();
            String bodystring = response.string();
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("--------------------------------------" + bodystring);
            List<MoodleUpload> entity = objectMapper.readValue(bodystring , new TypeReference<List<MoodleUpload>>(){});
            System.out.println(entity);
            return entity.get(0);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static X509TrustManager createTrustmanager() {
        X509TrustManager tm;
        try {
            tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

            };
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tm;
    }
}
