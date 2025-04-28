package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PresentarExamenController {

    @FXML
    private VBox preguntasVBox;

    @FXML
    private Label tiempoLabel;

    @FXML
    private Button finalizarExamenButton;

    @FXML
    private Button volverButton;

    @FXML
    private void initialize() {
        // Aquí puedes cargar las preguntas dinámicamente en la interfaz
        System.out.println("Cargando preguntas del examen...");

        // Configurar el temporizador (ejemplo: 60 minutos)
        iniciarTemporizador(60);
    }

    @FXML
    private void finalizarExamen() {
        System.out.println("Finalizando el examen...");
        // Aquí se enviaría la respuesta al servidor y calcularía la calificación
    }

    private void iniciarTemporizador(int minutos) {
        // Simulación de temporizador (deberías usar un temporizador real con Task en JavaFX)
        System.out.println("Temporizador iniciado para " + minutos + " minutos.");
        // Mostrar el tiempo en la etiqueta
        tiempoLabel.setText("Tiempo restante: " + minutos + " min");
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menuestudiante.fxml"));
            Stage stage = (Stage) volverButton.getScene().getWindow(); // Usa un componente visible en esta escena
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Estudiante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo regresar al menú.");
            alert.showAndWait();
        }
    }
}
