package com.ducki;

public class DetectionResult {
    private final String filename;
    private final String label;
    private final double confidence;

    public DetectionResult(String filename, String label, double confidence) {
        this.filename = filename;
        this.label = label;
        this.confidence = confidence;
    }

    public String getFilename() {
        return filename;
    }

    public String getLabel() {
        return label;
    }

    public double getConfidence() {
        return confidence;
    }
}
