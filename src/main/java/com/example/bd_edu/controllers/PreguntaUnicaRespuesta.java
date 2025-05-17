package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PreguntaUnicaRespuesta {

    @FXML private TextField enunciadoField;
    @FXML private TextField opcionField1;
    @FXML private TextField opcionField2;
    @FXML private TextField opcionField3;
    @FXML private TextField opcionField4;
    @FXML private TextField porcentajeField;
    @FXML private TextField tiempoField;
    @FXML private RadioButton radio1;
    @FXML private RadioButton radio2;
    @FXML private RadioButton radio3;
    @FXML private RadioButton radio4;
    @FXML private ComboBox<String> comboEstado, comboTema;
    @FXML private Button guardarButton, volverButton;
    @FXML private ToggleGroup opcionesGroup;


    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    public void initialize() {
        // Cargar estados
        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        radio1.setToggleGroup(opcionesGroup);
        radio2.setToggleGroup(opcionesGroup);
        radio3.setToggleGroup(opcionesGroup);
        radio4.setToggleGroup(opcionesGroup);

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));
    }

    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();
        String estado = comboEstado.getValue();
        String temaSeleccionado = comboTema.getValue();

        if (enunciado.isEmpty() || opcionesGroup.getSelectedToggle() == null || estado == null || temaSeleccionado == null) {
            mostrarAlerta("Por favor, complete todos los campos y seleccione la respuesta correcta.");
            return;
        }

        // Obtener textos de las opciones
        String textoOpcion1 = opcionField1.getText();
        String textoOpcion2 = opcionField2.getText();
        String textoOpcion3 = opcionField3.getText();
        String textoOpcion4 = opcionField4.getText();

        // Validar que ninguna opción esté vacía
        if (textoOpcion1.isEmpty() || textoOpcion2.isEmpty() ||
                textoOpcion3.isEmpty() || textoOpcion4.isEmpty()) {
            mostrarAlerta("Por favor, complete todas las opciones.");
            return;
        }

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

        // Determinar cuál opción es la correcta
        RadioButton seleccionada = (RadioButton) opcionesGroup.getSelectedToggle();

        // Obtener id_tema desde el nombre del tema seleccionado
        int idTema = bd_edu.obtenerIdTema(temaSeleccionado);

        // Definir id_banco (puedes ajustarlo según tu lógica, aquí fijo)
        int idBanco = bd_edu.obtenerIdBanco(idTema);

        // Definir tiempo límite (por ahora 60 segundos)
        int tiempoLimite = Integer.parseInt(tiempoField.getText());

        int idPregunta = bd_edu.crearPreguntaUnicaRespuesta(enunciado, estado, tiempoLimite, idBanco, idTema, porcentaje);

        if (idPregunta == -1) {
            mostrarAlerta("Error al guardar la pregunta en la base de datos.");
            return;
        }

        System.out.println(seleccionada == radio1);
        System.out.println(seleccionada == radio2);
        System.out.println(seleccionada == radio3);
        System.out.println(seleccionada == radio4);

        bd_edu.guardarRespuestaUnica(idPregunta, textoOpcion1, seleccionada == radio1);
        bd_edu.guardarRespuestaUnica(idPregunta, textoOpcion2, seleccionada == radio2);
        bd_edu.guardarRespuestaUnica(idPregunta, textoOpcion3, seleccionada == radio3);
        bd_edu.guardarRespuestaUnica(idPregunta, textoOpcion4, seleccionada == radio4);

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
        opcionField1.clear();
        opcionField2.clear();
        opcionField3.clear();
        opcionField4.clear();
        comboEstado.getSelectionModel().clearSelection();
        comboTema.getSelectionModel().clearSelection();
        opcionesGroup.selectToggle(null);
    }
}
