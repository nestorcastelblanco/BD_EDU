package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CrearQuizController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextArea descripcionArea;

    @FXML
    private ComboBox<String> categoriaCombo;

    @FXML
    private Spinner<Integer> totalPreguntasSpinner;

    @FXML
    private Spinner<Integer> preguntasExamenSpinner;

    @FXML
    private Spinner<Integer> tiempoSpinner;

    @FXML
    private Button crearQuizButton;

    @FXML
    private Button volverButton;

    @FXML
    public void initialize() {
        categoriaCombo.getItems().addAll("Matemáticas", "Ciencias", "Lengua", "Historia");

        totalPreguntasSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        preguntasExamenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5));
        tiempoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 180, 60));
    }

    @FXML
    private void crearQuiz() {
        String nombre = nombreField.getText();
        String descripcion = descripcionArea.getText();
        String categoria = categoriaCombo.getValue();
        int total = totalPreguntasSpinner.getValue();
        int seleccionadas = preguntasExamenSpinner.getValue();
        int tiempo = tiempoSpinner.getValue();

        if (nombre.isEmpty() || descripcion.isEmpty() || categoria == null) {
            System.out.println("Por favor complete todos los campos.");
            return;
        }

        System.out.println("Quiz creado:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Categoría: " + categoria);
        System.out.println("Preguntas en banco: " + total);
        System.out.println("Preguntas a presentar: " + seleccionadas);
        System.out.println("Tiempo límite: " + tiempo + " minutos");

        // Aquí iría la lógica para guardar el quiz en la base de datos o enviarlo al backend
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
