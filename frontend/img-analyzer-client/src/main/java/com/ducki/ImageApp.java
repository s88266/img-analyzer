package com.ducki;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ImageApp extends Application {

    private VBox imageList = new VBox(30); // Container für Bilder + Erkennungsergebnisse

    @Override
    public void start(Stage primaryStage) {
        ComboBox<ModelType> modelSelector = new ComboBox<>();
        modelSelector.getItems().addAll(ModelType.values());
        modelSelector.setValue(ModelType.YOLO); // Standardmodell

        Button reloadButton = new Button("📷 Bilder auswählen & analysieren");
        reloadButton.setStyle("-fx-font-size: 14px;");
        reloadButton.setOnAction(e -> loadImagesAndShow(primaryStage, modelSelector.getValue()));

        ScrollPane scrollPane = new ScrollPane(imageList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        VBox fullLayout = new VBox(15, modelSelector, reloadButton, scrollPane);
        fullLayout.setAlignment(Pos.TOP_CENTER);
        fullLayout.setPadding(new Insets(20));

        Scene scene = new Scene(fullLayout, 1000, 800);
        primaryStage.setTitle("Image Analyzer");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void loadImagesAndShow(Stage stage, ModelType model) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Mehrere Bilder auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilder", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles == null || selectedFiles.isEmpty()) {
            System.out.println("Keine Bilder ausgewählt.");
            return;
        }

        imageList.getChildren().clear();

        for (File file : selectedFiles) {
            VBox imageBlock = new VBox(5);
            imageBlock.setPadding(new Insets(10));
            imageBlock.setAlignment(Pos.CENTER_LEFT);
            imageBlock.getChildren().add(new Label("Analysiere: " + file.getName()));

            Image fxImage = new Image(file.toURI().toString());

            new Thread(() -> {
                try {
                    List<ImageWithOverlay.Detection> detections = DetectionClient.detectObjects(file, model);
                    ImageWithOverlay imageView = new ImageWithOverlay(fxImage, detections);

                    Platform.runLater(() -> {
                        imageBlock.getChildren().clear();
                        imageBlock.getChildren().add(imageView);

                        if (detections.isEmpty()) {
                            imageBlock.getChildren().add(new Label("→ Keine Objekte erkannt"));
                        } else {
                            for (ImageWithOverlay.Detection det : detections) {
                                imageBlock.getChildren().add(new Label("- " + det.label + " (Score: " + String.format("%.2f", det.score) + ")"));
                            }
                        }

                        imageList.getChildren().add(imageBlock);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        imageBlock.getChildren().add(new Label("❌ Fehler bei der Analyse"));
                        imageList.getChildren().add(imageBlock);
                    });
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
