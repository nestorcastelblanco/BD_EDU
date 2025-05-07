package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PreguntaOrdenarController {

    @FXML
    private TextField enunciadoField;

    @FXML
    private TextField opcion1Field;

    @FXML
    private TextField opcion2Field;

    @FXML
    private Button volverButton;

    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();
        String opcion1 = opcion1Field.getText();
        String opcion2 = opcion2Field.getText();

        // Lógica para guardar la pregunta
        System.out.println("Pregunta guardada: " + enunciado + " - " + opcion1 + " - " + opcion2);
        // Aquí podrías agregar la lógica de persistencia o lo que sea necesario para guardar la pregunta.
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
