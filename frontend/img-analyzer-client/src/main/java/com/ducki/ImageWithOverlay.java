package com.ducki;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class ImageWithOverlay extends StackPane {

    public static class Detection {
        public String label;
        public double score;
        public double[] bbox;

        public Detection(double x, double y, double width, double height, String label, double score) {
            this.label = label;
            this.score = score; // Beispielwert
            this.bbox = new double[] { x, y, width, height };
        }
    }

    private final ImageView imageView;
    private final Canvas canvas;

    public ImageWithOverlay(Image image, List<Detection> detections) {
        this.imageView = new ImageView(image);
        this.imageView.setPreserveRatio(true);

        double width = (int) image.getWidth();
        double height = (int) image.getHeight();

        this.canvas = new Canvas(width, height);

        this.setPrefSize(width, height);
        this.setMaxSize(width, height);

        canvas.setWidth(width);
        canvas.setHeight(height);

        this.getChildren().addAll(imageView, canvas);

        drawDetections(detections);
    }

    private void drawDetections(List<Detection> detections) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.BLACK);

        for (Detection det : detections) {
            if (det.bbox != null && det.bbox.length == 4) {
                double x = det.bbox[0];
                double y = det.bbox[1];
                double width = det.bbox[2];
                double height = det.bbox[3];

                gc.strokeRect(x, y, width, height);
                String text = det.label + " (" + String.format("%.2f", det.score) + ")";
                gc.setFill(Color.WHITE);
                gc.fillRect(x + 3, y + 3, text.length() * 7, 18);
                 // Text darüber
                gc.setFill(Color.BLACK);
                gc.fillText(text, x + 5, y + 17);
                System.out.println("Detection: " + det.label + ", Score: " + det.score);
            }
        }
    }

}
