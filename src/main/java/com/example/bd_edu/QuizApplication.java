package com.example.bd_edu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuizApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        QuizApplication.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sistema de Exámenes");
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(QuizApplication.class.getResource("/fxml/" + fxml + ".fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}