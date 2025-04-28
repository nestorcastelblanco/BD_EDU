package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ProgramarExamenController {

    @FXML
    private ComboBox<String> examenCombo;

    @FXML
    private DatePicker fechaPicker;

    @FXML
    private Spinner<Integer> horaSpinner;

    @FXML
    private Spinner<Integer> minutoSpinner;

    @FXML
    private Button volverButton;

    @FXML
    public void initialize() {
        // Carga de exámenes existentes (reemplaza con llamada a BD)
        examenCombo.getItems().addAll(
                "Quiz de Matemáticas",
                "Quiz de Ciencias",
                "Examen de Historia"
        );

        // Configuración de spinners para hora y minuto
        horaSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9)
        );
        minutoSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0)
        );
    }

    @FXML
    private void programarExamen() {
        String examen = examenCombo.getValue();
        LocalDate fecha = fechaPicker.getValue();
        Integer hora = horaSpinner.getValue();
        Integer minuto = minutoSpinner.getValue();

        if (examen == null || fecha == null) {
            mostrarAlerta("Datos incompletos",
                    "Seleccione un examen y una fecha válidos.",
                    AlertType.WARNING);
            return;
        }

        LocalTime horario = LocalTime.of(hora, minuto);
        System.out.println("Examen programado:");
        System.out.println("  Examen: " + examen);
        System.out.println("  Fecha: " + fecha);
        System.out.println("  Hora: " + horario);

        // Aquí iría la lógica para guardar la programación en la base de datos

        mostrarAlerta("Éxito",
                "El examen se ha programado para " + fecha + " a las " + horario,
                AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
