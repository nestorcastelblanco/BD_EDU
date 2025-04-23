package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class EstadisticasController {

    @FXML
    private TableView<?> tablaEstadisticas;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colGrupo;

    @FXML
    private TableColumn<?, ?> colExamen;

    @FXML
    private TableColumn<?, ?> colNota;

    @FXML
    private TableColumn<?, ?> colCorrectas;

    @FXML
    private TableColumn<?, ?> colIncorrectas;

    @FXML
    private Label labelResumen;

    @FXML
    private Button volverButton;

    @FXML
    public void initialize() {
        // Aquí se cargarían las estadísticas desde la BD o API
        System.out.println("Inicializando estadísticas...");
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuestudiante.fxml"));
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
