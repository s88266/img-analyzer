package com.ducki;

import java.io.File;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class App {

    public static void main(String[] args) throws Exception {

        // 1) Testbild – leg z.B. sample.jpg in den Projektordner
        File img = new File("sample.jpg");
        if (!img.exists()) {
            System.err.println("Testbild sample.jpg nicht gefunden!");
            return;
        }

        // 2) HTTP-POST vorbereiten
        HttpPost post = new HttpPost("http://127.0.0.1:8000/detect");
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", img)
                .build();
        post.setEntity(entity);

        // 3) Senden & Antwort ausgeben
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(post);
            String json = EntityUtils.toString(response.getEntity());
            System.out.println("Antwort vom Backend: " + json);
        }
    }
}
