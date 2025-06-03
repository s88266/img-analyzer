package com.ducki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Lade das Bild aus dem Projektverzeichnis
        Image image = new Image(getClass().getResource("/sample.jpg").toExternalForm());

        System.out.println("Bild nicht geladen: " + image.isError());

        System.out.println("Bildbreite: " + image.getWidth() + ", Hoehe: " + image.getHeight());

        // Beispielhafte Liste von Erkennungen
        List<ImageWithOverlay.Detection> detections = new ArrayList<>();
        detections.add(new ImageWithOverlay.Detection(50, 50, 100, 100, "Hund"));

        detections.add(new ImageWithOverlay.Detection(50, 150, 100, 100, "Mensch"));

        // Erzeuge die Ansicht mit Overlay
        ImageWithOverlay view = new ImageWithOverlay(image, detections);

        StackPane root = new StackPane();
        root.getChildren().add(view);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Image Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
