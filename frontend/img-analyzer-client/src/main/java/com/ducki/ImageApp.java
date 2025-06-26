package com.ducki;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import javafx.scene.control.ComboBox;



public class ImageApp extends Application {

    private VBox resultsBox = new VBox(10); // Für die Objektliste
    private StackPane imageContainer = new StackPane(); // Für das Bild mit Overlay
    

    @Override
    public void start(Stage primaryStage) {
        ComboBox<ModelType> modelSelector = new ComboBox<>();
        modelSelector.getItems().addAll(ModelType.values());
        modelSelector.setValue(ModelType.YOLO); // Startwert
        
        Button reloadButton = new Button("📷 Neues Bild wählen");
        reloadButton.setStyle("-fx-font-size: 14px;");
        reloadButton.setOnAction(e -> loadImageAndShow(primaryStage, modelSelector.getValue()));

        Label title = new Label("Erkannte Objekte:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        resultsBox.getChildren().add(title);

        VBox fullLayout = new VBox(15,modelSelector, reloadButton, resultsBox, imageContainer);
        fullLayout.setAlignment(Pos.TOP_CENTER);
        fullLayout.setPadding(new Insets(20));

        Scene scene = new Scene(fullLayout, 600, 600);
        primaryStage.setTitle("Image Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Automatisch in den Vollbildmodus wechseln
        primaryStage.setMaximized(true);
         // 🟡 Bildauswahl gleich beim Start mit gewähltem Modell starten
        loadImageAndShow(primaryStage, modelSelector.getValue());
    }

    private void loadImageAndShow(Stage stage, ModelType model) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bild auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilder", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            System.out.println("Kein Bild ausgewählt.");
            return;
        }

        imageContainer.getChildren().setAll(new Label("Bild wird analysiert..."));
        resultsBox.getChildren().setAll(new Label("Erkannte Objekte:"));

        new Thread(() -> {
            try {
                Image image = new Image(file.toURI().toString());
                 List<ImageWithOverlay.Detection> detections = DetectionClient.detectObjects(file, model);


                ImageWithOverlay view = new ImageWithOverlay(image, detections);

                Platform.runLater(() -> {
                    resultsBox.getChildren().clear();
                    Label title = new Label("Erkannte Objekte:");
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                    resultsBox.getChildren().add(title);

                    if (detections.isEmpty()) {
                        resultsBox.getChildren().add(new Label("Keine Objekte erkannt."));
                    } else {
                        for (ImageWithOverlay.Detection det : detections) {
                            Label entry = new Label("- " + det.label + " (Score: " + String.format("%.2f", det.score) + ")");
                            entry.setStyle("-fx-font-size: 14px;");
                            resultsBox.getChildren().add(entry);
                        }
                    }

                    imageContainer.getChildren().setAll(view);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    resultsBox.getChildren().setAll(new Label("Fehler bei der Analyse."));
                    imageContainer.getChildren().clear();
                });
            }
        }).start();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
