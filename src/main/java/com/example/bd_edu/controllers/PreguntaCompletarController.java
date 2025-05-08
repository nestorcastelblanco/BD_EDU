package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PreguntaCompletarController {

    @FXML
    private TextArea txtPregunta;

    @FXML
    private TextField txtPorcentaje, txtRespuesta, txtTiempo;

    @FXML
    private ComboBox<String> comboEstado, comboTema;

    @FXML
    private Button btnGuardar, volver;

    private Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    private void initialize() {
        // Cargar estados
        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));
    }

    @FXML
    private void guardar(ActionEvent event) {
        String pregunta = txtPregunta.getText().trim();
        String tipo = "Completar";
        String porcentajeText = txtPorcentaje.getText().trim();
        String respuesta = txtRespuesta.getText().trim();
        String estado = comboEstado.getValue();
        String tiempoText = txtTiempo.getText().trim();
        String tema = comboTema.getValue();

        // Validación de campos
        if (pregunta.isEmpty() || porcentajeText.isEmpty() || tiempoText.isEmpty() ||
                estado == null || tema == null || respuesta.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor complete todos los campos.");
            return;
        }

        double porcentaje;
        int tiempo;
        try {
            porcentaje = Double.parseDouble(porcentajeText);
            tiempo = Integer.parseInt(tiempoText);
        } catch (NumberFormatException e) {
            mostrarAlerta("Datos inválidos", "Porcentaje debe ser un número decimal válido y tiempo un número entero.");
            return;
        }

        int idTema = bd_edu.obtenerIdTema(tema);
        int idBanco = bd_edu.obtenerIdBanco(idTema);

        if (idTema == -1 || idBanco == -1) {
            mostrarAlerta("Error", "No se pudo obtener el ID del tema o banco.");
            return;
        }

        // Guardar pregunta
        bd_edu.crearPreguntaCompletar(pregunta, "Completar", porcentaje, respuesta, estado, tiempo, idBanco, idTema);
        mostrarAlerta("Éxito", "Pregunta guardada correctamente.");

        limpiarCampos();
    }

    @FXML
    private void volver(ActionEvent actionEvent) {
        Bd_Edu.loadStage("/ventanas/seleccionarTipoPregunta.fxml", actionEvent);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        txtPregunta.clear();
        txtPorcentaje.clear();
        txtRespuesta.clear();
        txtTiempo.clear();
        comboEstado.getSelectionModel().clearSelection();
        comboTema.getSelectionModel().clearSelection();
    }
}
