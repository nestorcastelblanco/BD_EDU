package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PreguntaEmparejarController {

    // Referencias a los elementos de la interfaz
    @FXML
    private TextField enunciadoField;

    @FXML
    private ListView<String> listaA;

    @FXML
    private ListView<String> listaB;

    @FXML
    private Button guardarButton;

    @FXML
    private Button volverButton;

    // Método para inicializar las listas de emparejamiento
    public void initialize() {
//        ObservableList<String> listaAItems = FXCollections.observableArrayList("Elemento A1", "Elemento A2", "Elemento A3");
//        ObservableList<String> listaBItems = FXCollections.observableArrayList("Elemento B1", "Elemento B2", "Elemento B3");
//
//        listaA.setItems(listaAItems);
//        listaB.setItems(listaBItems);
    }

    // Método para guardar la pregunta
    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();

        // Verifica si el enunciado está vacío
        if (enunciado.isEmpty()) {
            System.out.println("Debe ingresar un enunciado.");
            return;
        }

        // Lógica para obtener los emparejamientos entre las listas
        // Aquí asumimos que los elementos se emparejan en el orden de las listas A y B
        ObservableList<String> listaAItems = listaA.getItems();
        ObservableList<String> listaBItems = listaB.getItems();

        if (listaAItems.size() != listaBItems.size()) {
            System.out.println("El número de elementos en ambas listas debe ser igual.");
            return;
        }

        // Empareja los elementos de ambas listas
        System.out.println("Pregunta guardada:");
        System.out.println("Enunciado: " + enunciado);
        for (int i = 0; i < listaAItems.size(); i++) {
            String emparejamiento = listaAItems.get(i) + " - " + listaBItems.get(i);
            System.out.println("Emparejamiento: " + emparejamiento);
        }
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
