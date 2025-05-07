package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuDocenteController {

    @FXML
    private Button crearExamenButton;

    @FXML
    private Button agregarPreguntaButton;

    @FXML
    private Button verEstadisticasButton;

    @FXML
    private Button configurarExamenButton;

    @FXML
    private Button volverButton;

    @FXML
    private void crearExamen() {
        System.out.println("Acción para crear un examen.");
        cargarVentana("/ventanas/crearquiz.fxml", "Crear Examen");
    }

    @FXML
    private void agregarPregunta() {
        System.out.println("Acción para agregar una pregunta.");
        cargarVentana("/ventanas/seleccionarTipoPregunta.fxml", "Agregar Pregunta");
    }

    @FXML
    private void verEstadisticas() {
        System.out.println("Acción para ver las estadísticas.");
        cargarVentana("/ventanas/estadisticas.fxml", "Estadisticas");
    }

    @FXML
    private void configurarExamen() {
        System.out.println("Acción para configurar un examen.");
        cargarVentana("/ventanas/programarexamen.fxml", "Programar Examen");
    }

    private void cargarVentana(String rutaFXML, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Stage stage = (Stage) agregarPreguntaButton.getScene().getWindow(); // Puedes usar cualquier botón
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(tituloVentana);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana: " + tituloVentana);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void volver(ActionEvent event) {
        Object evt = event.getSource();
        if (evt.equals(volverButton)) {
            Bd_Edu.loadStage("/ventanas/login.fxml", event);
        }
    }
}
