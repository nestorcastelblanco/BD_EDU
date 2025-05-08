package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PreguntaFalsoVerdaderoController {

    // Referencias a los elementos de la interfaz
    @FXML private TextField enunciadoField;

    @FXML private RadioButton verdaderoRadio;

    @FXML private RadioButton falsoRadio;

    @FXML private Button guardarButton;

    @FXML private Button volverButton;

    @FXML private ComboBox<String> comboEstado, comboTema;

    @FXML private TextField porcentajeField, tiempoField;

    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    public void initialize() {
        // Cargar estados
        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));
    }

    // Método para guardar la pregunta
    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();

        // Verifica cuál opción ha sido seleccionada
        String respuestaCorrecta = null;
        if (verdaderoRadio.isSelected()) {
            respuestaCorrecta = "Verdadero";
        } else if (falsoRadio.isSelected()) {
            respuestaCorrecta = "Falso";
        }

        // Obtener valores de los campos adicionales
        String estado = comboEstado.getValue();
        String temaSeleccionado = comboTema.getValue();
        String porcentajeTexto = porcentajeField.getText();
        String tiempoTexto = tiempoField.getText();

        // Validación de campos vacíos
        if (enunciado.isEmpty() || respuestaCorrecta == null || estado == null || temaSeleccionado == null ||
                porcentajeTexto.isEmpty() || tiempoTexto.isEmpty()) {
            System.out.println("Debe completar todos los campos.");
            return;
        }

        // Validación y conversión de porcentaje y tiempo
        int porcentaje, tiempoLimite;
        try {
            porcentaje = Integer.parseInt(porcentajeTexto);
            tiempoLimite = Integer.parseInt(tiempoTexto);
        } catch (NumberFormatException e) {
            System.out.println("Porcentaje y tiempo deben ser números enteros.");
            return;
        }

        // Suponiendo que idBanco e idTema vienen del contexto
        int idTema = bd_edu.obtenerIdTema(temaSeleccionado);
        int idBanco = bd_edu.obtenerIdBanco(idTema);

        // Guardar la pregunta y obtener su id
        int idPregunta = bd_edu.crearPreguntaVerdaderoFalso(enunciado, estado, tiempoLimite, idBanco, idTema, porcentaje);

        // Guardar las dos opciones (Verdadero y Falso), marcando cuál es la correcta
        boolean esVerdaderoCorrecto = respuestaCorrecta.equals("Verdadero");
        if (esVerdaderoCorrecto){
            bd_edu.guardarRespuestaVerdaderoFalso(idPregunta, "Verdadero", true);
        }else{
            bd_edu.guardarRespuestaVerdaderoFalso(idPregunta, "Falso", false);
        }


        // Mostrar mensaje de confirmación
        System.out.println("Pregunta guardada:");
        System.out.println("Enunciado: " + enunciado);
        System.out.println("Respuesta correcta: " + respuestaCorrecta);
        System.out.println("Estado: " + estado);
        System.out.println("Tema: " + temaSeleccionado);
        System.out.println("Porcentaje: " + porcentaje);
        System.out.println("Tiempo límite: " + tiempoLimite + " segundos");

        // Opcional: limpiar campos después de guardar
        limpiarCampos();
    }

    private void limpiarCampos() {
        enunciadoField.clear();
        verdaderoRadio.setSelected(false);
        falsoRadio.setSelected(false);
        comboEstado.setValue(null);
        comboTema.setValue(null);
        porcentajeField.clear();
        tiempoField.clear();
    }

    // Método para volver a la pantalla anterior
    @FXML
    private void volver(ActionEvent actionEvent) {
        Object evt = actionEvent.getSource();
        if (evt.equals(volverButton)) {
            Bd_Edu.loadStage("/ventanas/seleccionarTipoPregunta.fxml", actionEvent);
        }
    }
}
