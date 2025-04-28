package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AgregarPreguntaController {

    @FXML
    private TextArea preguntaTextArea;

    @FXML
    private ComboBox<String> tipoPreguntaCombo;

    @FXML
    private TextField porcentajeField;

    @FXML
    private Button guardarButton;

    @FXML
    private Button volverButton;

    @FXML
    private void initialize() {
        tipoPreguntaCombo.getItems().addAll(
                "Selección única",
                "Selección múltiple",
                "Verdadero / Falso",
                "Emparejar",
                "Ordenar",
                "Completar"
        );
    }

    @FXML
    private void guardarPregunta() {
        String textoPregunta = preguntaTextArea.getText();
        String tipo = tipoPreguntaCombo.getValue();
        String porcentaje = porcentajeField.getText();

        // Validaciones básicas
        if (textoPregunta.isEmpty() || tipo == null || porcentaje.isEmpty()) {
            System.out.println("Debe completar todos los campos.");
            return;
        }

        System.out.println("Pregunta guardada:");
        System.out.println("Texto: " + textoPregunta);
        System.out.println("Tipo: " + tipo);
        System.out.println("Porcentaje: " + porcentaje + "%");

        // Aquí iría la lógica para guardar la pregunta en base de datos o lista temporal
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menudocente.fxml"));
            Stage stage = (Stage) volverButton.getScene().getWindow(); // Usa un componente visible en esta escena
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Docente");
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
