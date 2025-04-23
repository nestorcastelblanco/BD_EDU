package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;

public class HorarioController {

    @FXML
    private TableView<?> tablaHorario;

    @FXML
    private TableColumn<?, ?> colDia;

    @FXML
    private TableColumn<?, ?> colHora;

    @FXML
    private TableColumn<?, ?> colMateria;

    @FXML
    private TableColumn<?, ?> colAula;

    @FXML
    private Button volverButton;

    @FXML
    public void initialize() {
        // Aquí se podrían cargar los horarios desde la BD
        System.out.println("Cargando horario...");
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
