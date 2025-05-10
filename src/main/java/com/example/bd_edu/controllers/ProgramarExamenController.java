package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import com.example.bd_edu.model.Curso;
import com.example.bd_edu.model.Examen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ProgramarExamenController {

    private final Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    private ComboBox<Examen> examenCombo;

    @FXML
    private ComboBox<Curso> cursoChoice;

    @FXML
    private DatePicker fechaPicker;

    @FXML
    private Spinner<Integer> horaSpinner;

    @FXML
    private Spinner<Integer> minutoSpinner;

    @FXML
    private Button volverButton;

    @FXML
    private TextField umbralAprobacion, tiempoLimite;

    @FXML
    public void initialize() {

        List<Curso> cursos = bd_edu.obtenerCursos();
        cursoChoice.getItems().addAll(cursos);
        // Cargar exámenes reales desde la base de datos
        List<Examen> examenes = bd_edu.obtenerExamenes();
        // Asegúrate de que este método existe
        examenCombo.getItems().addAll(examenes);

        // Mostrar solo el título en el ComboBox
        examenCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Examen item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });

        examenCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Examen item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });


        cursoChoice.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Curso item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        cursoChoice.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Curso item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });


        // Configurar spinners de tiempo
        horaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        minutoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    private void programarExamen() {
        Examen examen = examenCombo.getValue();

        LocalDate fecha = fechaPicker.getValue();

        Integer hora = horaSpinner.getValue();

        Integer minuto = minutoSpinner.getValue();

        int tiempo = Integer.parseInt(tiempoLimite.getText());

        Curso curso = cursoChoice.getValue();

        BigDecimal umbral = new BigDecimal(umbralAprobacion.getText());

        if (examen == null || fecha == null || hora == null || minuto == null) {
            mostrarAlerta("Datos incompletos", "Seleccione un examen, fecha y hora válidos.", AlertType.WARNING);
            return;
        }

        LocalTime horario = LocalTime.of(hora, minuto);
        LocalDateTime fechaHora = LocalDateTime.of(fecha, horario);

        try {
            bd_edu.guardarProgramacionExamen(
                    examen.getIdExamen(),
                    fechaHora.toLocalDate(),
                    fechaHora.toLocalTime(),
                    tiempo,
                    curso.getId(),
                    umbral
            );
            mostrarAlerta("Éxito", "Examen programado correctamente.", AlertType.INFORMATION);
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo programar el examen.", AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        examenCombo.setValue(null);
        fechaPicker.setValue(null);
        horaSpinner.getValueFactory().setValue(9);
        minutoSpinner.getValueFactory().setValue(0);
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
            Stage stage = (Stage) volverButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Docente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al menú.", AlertType.ERROR);
        }
    }
}
