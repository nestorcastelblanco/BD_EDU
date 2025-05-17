package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PreguntaOrdenarController {

    @FXML
    private TextArea enunciadoField;

    @FXML
    private TextField opcion1Field;

    @FXML
    private TableView<String> tablaOrdenamientos;

    @FXML
    private TableColumn<String, String> columnaElemento;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private ComboBox<String> comboTema;

    @FXML
    private TextField porcentajeField;

    @FXML
    private TextField tiempoField;

    private ObservableList<String> listaElementos = FXCollections.observableArrayList();

    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    public void initialize() {
        columnaElemento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue())
        );

        tablaOrdenamientos.setItems(listaElementos);

        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));
    }

    @FXML
    private void almacenarElemento() {
        String elemento1 = opcion1Field.getText().trim();

        if (!elemento1.isEmpty()) {
            listaElementos.add(elemento1);
            opcion1Field.clear();
        }

        tablaOrdenamientos.refresh();
    }

    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText().trim();
        String estado = comboEstado.getValue();
        String tema = comboTema.getValue();
        String porcentajeStr = porcentajeField.getText().trim();
        String tiempoStr = tiempoField.getText().trim();

        if (enunciado.isEmpty() || estado == null || tema == null || porcentajeStr.isEmpty() || tiempoStr.isEmpty() || listaElementos.isEmpty()) {
            mostrarAlerta("Todos los campos deben estar completos y debe añadir al menos un elemento.");
            return;
        }

        int idTema = bd_edu.obtenerIdTema(tema);
        int idBanco = bd_edu.obtenerIdBanco(idTema);

        try {
            double porcentaje = Double.parseDouble(porcentajeStr);
            int tiempo = Integer.parseInt(tiempoStr);

            String respuestaOrdenada = String.join(";", listaElementos);

            // Aquí debes llamar a tu lógica de persistencia (DAO o procedimiento almacenado)
            System.out.println("Pregunta guardada:");
            System.out.println("Enunciado: " + enunciado);
            System.out.println("Respuesta ordenada: " + respuestaOrdenada);
            System.out.println("Estado: " + estado);
            System.out.println("Tema: " + tema);
            System.out.println("Porcentaje: " + porcentaje);
            System.out.println("Tiempo: " + tiempo);

            int idPregunta = bd_edu.crearPreguntaOrdenar(enunciado, porcentaje, estado,tiempo, "Ordenar" , idBanco, idTema);

            bd_edu.guardarRespuestaOrdenar(idPregunta, respuestaOrdenada);
            mostrarAlerta("Pregunta guardada correctamente.");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Porcentaje y tiempo deben ser valores numéricos.");
        }
    }

    private void limpiarCampos() {
        enunciadoField.clear();
        opcion1Field.clear();
        listaElementos.clear();
        comboEstado.getSelectionModel().clearSelection();
        comboTema.getSelectionModel().clearSelection();
        porcentajeField.clear();
        tiempoField.clear();
        tablaOrdenamientos.refresh();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void volver(ActionEvent actionEvent) {
        Bd_Edu.loadStage("/ventanas/seleccionarTipoPregunta.fxml", actionEvent);
    }
}
