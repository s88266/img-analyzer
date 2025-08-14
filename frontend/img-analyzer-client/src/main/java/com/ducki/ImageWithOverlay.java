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
        // Bounding box format: [x, y, width, height]
        public Detection(double x, double y, double width, double height, String label, double score) {
            this.label = label;
            this.score = score;
            this.bbox = new double[]{x, y, width, height};
        }
    }

    private final ImageView imageView;
    private final Canvas canvas;
    /**
     * Constructs an ImageWithOverlay instance.
     *
     * @param image The image to be displayed.
     * @param detections A list of detections to overlay on the image.
     */
    public ImageWithOverlay(Image image, List<Detection> detections) {
        this.imageView = new ImageView(image);
        this.imageView.setPreserveRatio(true);

        double width = image.getWidth();
        double height = image.getHeight();

        this.canvas = new Canvas(width, height);

        this.setPrefSize(width, height);
        this.setMaxSize(width, height);

        canvas.setWidth(width);
        canvas.setHeight(height);

        this.getChildren().addAll(imageView, canvas);

        drawDetections(detections);
    }

    /**
     * Updates the image displayed in this ImageWithOverlay instance.
     *
     * @param image The new image to display.
     */
    private void drawDetections(List<Detection> detections) {
        if (detections == null || detections.isEmpty()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setLineWidth(2);
        gc.setFont(Font.font("Arial", 14));

        Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.PURPLE
        };

        int colorIndex = 0;
        // Iterate through each detection and draw the bounding box and label
        for (Detection det : detections) {
            if (det.bbox != null && det.bbox.length == 4) {
                double x = det.bbox[0];
                double y = det.bbox[1];
                double width = det.bbox[2];
                double height = det.bbox[3];

                Color boxColor = colors[colorIndex % colors.length];
                colorIndex++;

                gc.setStroke(boxColor);
                gc.setFill(boxColor);

                gc.strokeRect(x, y, width, height);

                String text = det.label + " (" + String.format("%.2f", det.score) + ")";

                double textWidth = text.length() * 7;
                double textHeight = 18;

                gc.setFill(Color.color(0, 0, 0, 0.6));
                gc.fillRect(x + 2, y + 2, textWidth, textHeight);

                gc.setFill(Color.WHITE);
                gc.fillText(text, x + 5, y + 15);

                System.out.println("Detection: " + det.label + ", Score: " + det.score);
            }
        }
    }
}
