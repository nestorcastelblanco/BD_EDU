package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import com.example.bd_edu.model.Emparejamiento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class PreguntaEmparejarController {

    @FXML
    private TextField enunciadoField, opcion1Field, opcion2Field, porcentajeField, tiempoField;

    @FXML
    private ListView<String> listaA, listaB;  // Ya no se usan pero los dejo si quieres borrar luego

    @FXML
    private ComboBox<String> comboEstado, comboTema;

    @FXML
    private Button guardarButton, volverButton;

    @FXML
    private TableView<Emparejamiento> tablaEmparejamientos;

    @FXML
    private TableColumn<Emparejamiento, String> columnaA, columnaB;

    private ObservableList<Emparejamiento> listaEmparejamientos = FXCollections.observableArrayList();

    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar temas desde BD
        List<String> temas = bd_edu.obtenerTemasDesdeBD();
        comboTema.setItems(FXCollections.observableArrayList(temas));

        // Configurar tabla
        columnaA.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOpcionA()));
        columnaB.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOpcionB()));
        tablaEmparejamientos.setItems(listaEmparejamientos);
    }

    // Almacenar una pareja en la tabla
    @FXML
    public void almacenarPareja(ActionEvent actionEvent) {
        String opcionA = opcion1Field.getText();
        String opcionB = opcion2Field.getText();

        if (opcionA.isEmpty() || opcionB.isEmpty()) {
            System.out.println("Debe ingresar ambas opciones.");
            return;
        }

        listaEmparejamientos.add(new Emparejamiento(opcionA, opcionB));
        opcion1Field.clear();
        opcion2Field.clear();
    }

    // Guardar la pregunta y sus emparejamientos
    @FXML
    private void guardarPregunta() {
        String enunciado = enunciadoField.getText();
        String estado = comboEstado.getValue();
        String temaSeleccionado = comboTema.getValue();
        String porcentajeStr = porcentajeField.getText();
        String tiempoStr = tiempoField.getText();

        if (enunciado.isEmpty() || estado == null || temaSeleccionado == null ||
                porcentajeStr.isEmpty() || tiempoStr.isEmpty() || listaEmparejamientos.isEmpty()) {
            System.out.println("Debe completar todos los campos y cargar al menos una pareja.");
            return;
        }

        int idTema = bd_edu.obtenerIdTema(temaSeleccionado);
        int idBanco = bd_edu.obtenerIdBanco(idTema);  // Asumo que lo tienes implementado
        double porcentaje = Double.parseDouble(porcentajeStr);
        int tiempo = Integer.parseInt(tiempoStr);

        // Crear pregunta
        int idPregunta = bd_edu.crearPreguntaEmparejar(enunciado, porcentaje, "Emparejar", estado, tiempo, idBanco, idTema);

        // Guardar cada pareja como respuesta
        for (Emparejamiento emp : listaEmparejamientos) {
            bd_edu.guardarRespuestaEmparejar(idPregunta, emp.getOpcionA(), emp.getOpcionB());
        }

        System.out.println("Pregunta y emparejamientos guardados exitosamente.");
        limpiarCampos();
    }

    // Método para volver
    @FXML
    private void volver(ActionEvent actionEvent) {
        Bd_Edu.loadStage("/ventanas/seleccionarTipoPregunta.fxml", actionEvent);
    }

    // Limpiar todo después de guardar
    private void limpiarCampos() {
        enunciadoField.clear();
        opcion1Field.clear();
        opcion2Field.clear();
        porcentajeField.clear();
        tiempoField.clear();
        comboEstado.getSelectionModel().clearSelection();
        comboTema.getSelectionModel().clearSelection();
        listaEmparejamientos.clear();
    }
}
