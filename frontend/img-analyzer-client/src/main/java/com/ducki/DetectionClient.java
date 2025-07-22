package com.ducki;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.File;
import java.util.*;

public class DetectionClient {

    public static Map<String, Map<String, List<ImageWithOverlay.Detection>>> detectBatchAll(List<File> imageFiles) {
        Map<String, Map<String, List<ImageWithOverlay.Detection>>> resultMap = new HashMap<>();

        try {
            // baue Multipart korrekt mit mehreren "files"-Feldern
            var multipart = Unirest.post("http://localhost:8000/detect_batch_all")
                    .field("files", imageFiles.get(0));
            for (int i = 1; i < imageFiles.size(); i++) {
                multipart.field("files", imageFiles.get(i));
            }

            HttpResponse<JsonNode> response = multipart.asJson();

            // falls es fehler gibt , damit den batchresult sieht
            // System.out.println("SERVER-RESPONSE:");
            // System.out.println(response.getBody().toPrettyString());

            if (response.getStatus() == 200) {
                JSONObject jsonObject = response.getBody().getObject();
                JSONArray batchResults = jsonObject.getJSONArray("batch_results");

                for (int i = 0; i < batchResults.length(); i++) {
                    JSONObject fileResult = batchResults.getJSONObject(i);
                    String filename = fileResult.getString("filename");
                    JSONObject detectionsByModel = fileResult.getJSONObject("detections");

                    Map<String, List<ImageWithOverlay.Detection>> modelDetections = new HashMap<>();

                    for (Iterator<String> it = detectionsByModel.keys(); it.hasNext();) {
                        String modelName = it.next();
                        JSONArray detectionsArray = detectionsByModel.getJSONArray(modelName);

                        List<ImageWithOverlay.Detection> detectionsList = new ArrayList<>();

                        for (int j = 0; j < detectionsArray.length(); j++) {
                            JSONObject obj = detectionsArray.getJSONObject(j);
                            JSONArray bbox = obj.getJSONArray("bbox");
                            double x1 = bbox.getDouble(0);
                            double y1 = bbox.getDouble(1);
                            double x2 = bbox.getDouble(2);
                            double y2 = bbox.getDouble(3);

                            double width = x2 - x1;
                            double height = y2 - y1;

                            detectionsList.add(new ImageWithOverlay.Detection(
                                    x1, y1, width, height,
                                    obj.getString("label"),
                                    obj.getDouble("confidence")
                            ));
                        }

                        modelDetections.put(modelName, detectionsList);
                    }

                    resultMap.put(filename, modelDetections);
                }

                System.out.println("=== Ergebnis-Keys ===");
                resultMap.keySet().forEach(System.out::println);
                System.out.println("======================");
            } else {
                System.err.println("Fehler beim Batch-Request: " + response.getStatusText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
