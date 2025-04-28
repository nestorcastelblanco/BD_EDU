package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;

public class VerBancoPreguntasController {

    @FXML
    private TableView<Pregunta> tablaPreguntas;

    @FXML
    private TableColumn<Pregunta, Integer> colId;

    @FXML
    private TableColumn<Pregunta, String> colTexto;

    @FXML
    private TableColumn<Pregunta, String> colTipo;

    @FXML
    private TableColumn<Pregunta, Integer> colPorcentaje;

    @FXML
    private TableColumn<Pregunta, String> colAutor;

    @FXML
    private TableColumn<Pregunta, Boolean> colPublica;

    @FXML
    private Button volverButton;

    private ObservableList<Pregunta> datosPreguntas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar celdas de la tabla
        colId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colTexto.setCellValueFactory(cell -> cell.getValue().textoProperty());
        colTipo.setCellValueFactory(cell -> cell.getValue().tipoProperty());
        colPorcentaje.setCellValueFactory(cell -> cell.getValue().porcentajeProperty().asObject());
        colAutor.setCellValueFactory(cell -> cell.getValue().autorProperty());
        colPublica.setCellValueFactory(cell -> cell.getValue().publicaProperty().asObject());

        // Cargar datos de muestra (reemplaza con llamada a BD)
        datosPreguntas.addAll(
                new Pregunta(1, "¿Capital de Francia?", "Selección única", 20, "Prof. López", true),
                new Pregunta(2, "V o F: El agua hierve a 100°C.", "Verdadero/Falso", 10, "Prof. Ruiz", false)
        );
        tablaPreguntas.setItems(datosPreguntas);
    }

    // Clase interna de modelo de datos (puedes moverla a su propio archivo)
    public static class Pregunta {
        private final javafx.beans.property.IntegerProperty id;
        private final javafx.beans.property.StringProperty texto;
        private final javafx.beans.property.StringProperty tipo;
        private final javafx.beans.property.IntegerProperty porcentaje;
        private final javafx.beans.property.StringProperty autor;
        private final javafx.beans.property.BooleanProperty publica;

        public Pregunta(int id, String texto, String tipo, int porcentaje, String autor, boolean publica) {
            this.id = new javafx.beans.property.SimpleIntegerProperty(id);
            this.texto = new javafx.beans.property.SimpleStringProperty(texto);
            this.tipo = new javafx.beans.property.SimpleStringProperty(tipo);
            this.porcentaje = new javafx.beans.property.SimpleIntegerProperty(porcentaje);
            this.autor = new javafx.beans.property.SimpleStringProperty(autor);
            this.publica = new javafx.beans.property.SimpleBooleanProperty(publica);
        }

        public javafx.beans.property.IntegerProperty idProperty() { return id; }
        public javafx.beans.property.StringProperty textoProperty() { return texto; }
        public javafx.beans.property.StringProperty tipoProperty() { return tipo; }
        public javafx.beans.property.IntegerProperty porcentajeProperty() { return porcentaje; }
        public javafx.beans.property.StringProperty autorProperty() { return autor; }
        public javafx.beans.property.BooleanProperty publicaProperty() { return publica; }
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
