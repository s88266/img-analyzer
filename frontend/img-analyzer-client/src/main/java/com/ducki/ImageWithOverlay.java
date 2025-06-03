package com.ducki;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;

public class ImageWithOverlay extends StackPane {
    private final ImageView imageView;
    private final Canvas canvas;

    public ImageWithOverlay(Image image, List<Detection> detections) {
    this.imageView = new ImageView(image);
    this.imageView.setPreserveRatio(true);

    // Bildgrößen korrekt setzen
    double width = image.getWidth();
    double height = image.getHeight();
    System.out.println("Bildgröße: " + width + "x" + height);

    this.canvas = new Canvas(width, height);

    this.setPrefSize(width, height);
    this.setMaxSize(width, height);

    // Canvas auf gleiche Größe bringen
    canvas.setWidth(width);
    canvas.setHeight(height);

    this.getChildren().addAll(imageView, canvas);

    drawDetections(detections);
}


    private void drawDetections(List<Detection> detections) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setFill(Color.BLACK);

        for (Detection det : detections) {
            if(det.bbox != null && det.bbox.length==4){ 
            double x = det.bbox[0];
            double y = det.bbox[1];
            double width = det.bbox[2] - det.bbox[0];
            double height = det.bbox[3] - det.bbox[1];

            gc.strokeRect(x, y, width, height);
            gc.fillText(det.label + " (" + det.confidence + ")", x + 3, y + 15);
            System.out.println("Detection: " + det);
        }
        }
    }

    public static class Detection {
        public String label;
        public double confidence;
        public double[] bbox;

   public Detection(double x, double y, double width, double height, String label) {
    this.label = label;
    this.confidence = 0.9; // Dummy-Wert
    this.bbox = new double[] { x, y, x + width, y + height };
}



    }
}
