package com.example.bd_edu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class QuizApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(QuizApplication.class.getResource("/ventanas/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(Objects.requireNonNull(QuizApplication.class.getResource("/estilos.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Sistema de Exámenes");
        stage.setResizable(false);       // <- para que no puedan cambiar el tamaño
        stage.centerOnScreen();           // <- para que siempre salga centrado
        stage.show();
    }

    public static void main(String[] args) {
        launch(QuizApplication.class, args);
    }
}
