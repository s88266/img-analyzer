package com.ducki;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ImageApp extends Application {

    private VBox imageList = new VBox(30);

    @Override
    public void start(Stage primaryStage) {
        Button reloadButton = new Button("📷 Bilder auswählen & analysieren (alle Modelle)");
        reloadButton.setStyle("-fx-font-size: 14px;");
        reloadButton.setOnAction(e -> loadImagesAndShow(primaryStage));

        ScrollPane scrollPane = new ScrollPane(imageList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        VBox fullLayout = new VBox(15, reloadButton, scrollPane);
        fullLayout.setAlignment(Pos.TOP_CENTER);
        fullLayout.setPadding(new Insets(20));

        Scene scene = new Scene(fullLayout, 1200, 900);
        primaryStage.setTitle("Image Analyzer - Alle Modelle");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void loadImagesAndShow(Stage stage) {
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

        new Thread(() -> {
            try {
                Map<String, Map<String, List<ImageWithOverlay.Detection>>> results =
                        DetectionClient.detectBatchAll(selectedFiles);

                Platform.runLater(() -> {
                    for (File file : selectedFiles) {
                        String fileName = file.getName();
                        VBox imageBlock = new VBox(10);
                        imageBlock.setPadding(new Insets(10));
                        imageBlock.setAlignment(Pos.TOP_LEFT);
                        imageBlock.getChildren().add(new Label("Bild: " + fileName));

                        Image fxImage = new Image(file.toURI().toString());

                        Map<String, List<ImageWithOverlay.Detection>> modelResults = results.get(fileName);

                        if (modelResults == null) {
                            imageBlock.getChildren().add(new Label("❌ Keine Ergebnisse für dieses Bild."));
                        } else {
                            for (Map.Entry<String, List<ImageWithOverlay.Detection>> entry : modelResults.entrySet()) {
                                String modelName = entry.getKey();
                                List<ImageWithOverlay.Detection> detections = entry.getValue();

                                imageBlock.getChildren().add(new Label("→ Modell: " + modelName));

                                ImageWithOverlay imageView = new ImageWithOverlay(fxImage, detections);
                                imageBlock.getChildren().add(imageView);

                                if (detections.isEmpty()) {
                                    imageBlock.getChildren().add(new Label("   Keine Objekte erkannt."));
                                } else {
                                    for (ImageWithOverlay.Detection det : detections) {
                                        imageBlock.getChildren().add(new Label(
                                                String.format("   - %s (Score: %.2f)", det.label, det.score)
                                        ));
                                    }
                                }
                            }
                        }

                        imageList.getChildren().add(imageBlock);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> imageList.getChildren().add(
                        new Label("❌ Fehler beim Abrufen der Ergebnisse vom Server.")));
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
