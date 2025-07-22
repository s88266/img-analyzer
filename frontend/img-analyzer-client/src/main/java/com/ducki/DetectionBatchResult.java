package com.ducki;

import java.util.List;
import java.util.Map;

public class DetectionBatchResult {
    private String filename;
    private String timestamp;
    private Map<String, List<DetectionResult>> detections;

    // Getter/Setter/Constructor
    public DetectionBatchResult(String filename, String timestamp, Map<String, List<DetectionResult>> detections) {
        this.filename = filename;
        this.timestamp = timestamp;
        this.detections = detections;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public Map<String, List<DetectionResult>> getDetections() {
        return detections;
    }
    public void setDetections(Map<String, List<DetectionResult>> detections) {
        this.detections = detections;
    }
}
