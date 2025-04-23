package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuEstudianteController {

    @FXML
    private Button verExamenesButton;

    @FXML
    private Button verEstadisticasButton;

    @FXML
    private Button verHorarioButton;

    @FXML
    private Button volverButton;

    @FXML
    private void verExamenes() {
        cargarVentana("/presentarexamen.fxml", "Exámenes Disponibles");
    }

    @FXML
    private void verEstadisticas() {
        cargarVentana("/estadisticas.fxml", "Estadísticas del Estudiante");
    }

    @FXML
    private void verHorario() {
        cargarVentana("/horario.fxml", "Horario de Clases");
    }

    private void cargarVentana(String rutaFXML, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Stage stage = (Stage) verHorarioButton.getScene().getWindow(); // Puedes usar cualquier botón
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana: " + tituloVentana);
        }
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) volverButton.getScene().getWindow(); // Usa un componente visible en esta escena
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
