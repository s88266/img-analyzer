package com.ducki;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetectionClient {

    public static List<ImageWithOverlay.Detection> detectObjects(File imageFile, ModelType model) {
        List<ImageWithOverlay.Detection> detections = new ArrayList<>();

        try {
            String url = switch (model) {
                case MOBILENET -> "http://localhost:8000/detect_tf";
                case FASTER_R_CNN -> "http://localhost:8000/detect_frcnn";
                case SSD -> "http://localhost:8000/detect_ssd";
                default -> "http://localhost:8000/detect"; // YOLO
            };

            HttpResponse<JsonNode> response = Unirest.post(url)
                    .field("file", imageFile)
                    .asJson();

            if (response.getStatus() == 200) {
                JSONObject responseObject = response.getBody().getObject();
                JSONArray arr = responseObject.getJSONArray("detections");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String label = obj.getString("label");
                    double confidence = obj.getDouble("confidence");

                    JSONArray bbox = obj.getJSONArray("bbox"); // Einheitlich bei allen Modellen
                    double x1 = bbox.getDouble(0);
                    double y1 = bbox.getDouble(1);
                    double x2 = bbox.getDouble(2);
                    double y2 = bbox.getDouble(3);

                    double width = x2 - x1;
                    double height = y2 - y1;

                    detections.add(new ImageWithOverlay.Detection(x1, y1, width, height, label, confidence));
                }
            } else {
                System.err.println("Fehler beim Senden: " + response.getStatusText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return detections;
    }
}
