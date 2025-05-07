package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PreguntaSeleccionMultiple {

    @FXML private TextField enunciadoField;
    @FXML private CheckBox opcion1, opcion2, opcion3, opcion4;
    @FXML private ComboBox<String> comboEstado, comboTema;
    @FXML private TextField porcentajeField, tiempoField;
    @FXML private Button guardarButton, volverButton;

    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    public void initialize() {
        // Cargar estados
        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));
    }

    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();
        String estado = comboEstado.getValue();
        String temaSeleccionado = comboTema.getValue();

        // Validaciones iniciales
        if (enunciado.isEmpty() || estado == null || temaSeleccionado == null) {
            mostrarAlerta("Por favor, complete todos los campos obligatorios.");
            return;
        }

        // Validar que todas las opciones tengan texto
        String textoOpcion1 = opcion1.getText();
        String textoOpcion2 = opcion2.getText();
        String textoOpcion3 = opcion3.getText();
        String textoOpcion4 = opcion4.getText();

        if (textoOpcion1.isEmpty() || textoOpcion2.isEmpty() ||
                textoOpcion3.isEmpty() || textoOpcion4.isEmpty()) {
            mostrarAlerta("Por favor, complete todas las opciones.");
            return;
        }

        // Validar que al menos una opción esté marcada como correcta
        if (!opcion1.isSelected() && !opcion2.isSelected() &&
                !opcion3.isSelected() && !opcion4.isSelected()) {
            mostrarAlerta("Seleccione al menos una respuesta correcta.");
            return;
        }

        // Validar porcentaje
        if (porcentajeField.getText().isEmpty()) {
            mostrarAlerta("Por favor, asigne el porcentaje.");
            return;
        }

        double porcentaje;
        try {
            porcentaje = Double.parseDouble(porcentajeField.getText());
            if (porcentaje <= 0 || porcentaje > 100) {
                mostrarAlerta("El porcentaje debe ser un valor entre 1 y 100.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor, ingrese un valor numérico válido para el porcentaje.");
            return;
        }

        // Validar tiempo
        if (tiempoField.getText().isEmpty()) {
            mostrarAlerta("Por favor, ingrese el tiempo límite.");
            return;
        }

        int tiempoLimite;
        try {
            tiempoLimite = Integer.parseInt(tiempoField.getText());
            if (tiempoLimite <= 0) {
                mostrarAlerta("El tiempo debe ser mayor a 0 segundos.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor, ingrese un valor numérico válido para el tiempo.");
            return;
        }

        // Obtener id_tema e id_banco
        int idTema = bd_edu.obtenerIdTema(temaSeleccionado);
        int idBanco = bd_edu.obtenerIdBanco(idTema);

        // Crear la pregunta en la BD
        int idPregunta = bd_edu.crearPreguntaSeleccionMultiple(enunciado, estado, tiempoLimite, idBanco, idTema, porcentaje);

        if (idPregunta == -1) {
            mostrarAlerta("Error al guardar la pregunta en la base de datos.");
            return;
        }

        // Guardar cada opción junto con su es correcta
        bd_edu.guardarRespuestaMultiple(idPregunta, textoOpcion1, opcion1.isSelected());
        bd_edu.guardarRespuestaMultiple(idPregunta, textoOpcion2, opcion2.isSelected());
        bd_edu.guardarRespuestaMultiple(idPregunta, textoOpcion3, opcion3.isSelected());
        bd_edu.guardarRespuestaMultiple(idPregunta, textoOpcion4, opcion4.isSelected());

        mostrarAlerta("Pregunta guardada correctamente.");
        limpiarFormulario();
    }

    @FXML
    private void volver(ActionEvent actionEvent) {
        Bd_Edu.loadStage("/ventanas/seleccionarTipoPregunta.fxml", actionEvent);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setContentText(mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void limpiarFormulario() {
        enunciadoField.clear();
        porcentajeField.clear();
        tiempoField.clear();
        opcion1.setText("");
        opcion2.setText("");
        opcion3.setText("");
        opcion4.setText("");
        opcion1.setSelected(false);
        opcion2.setSelected(false);
        opcion3.setSelected(false);
        opcion4.setSelected(false);
        comboEstado.getSelectionModel().clearSelection();
        comboTema.getSelectionModel().clearSelection();
    }
}
