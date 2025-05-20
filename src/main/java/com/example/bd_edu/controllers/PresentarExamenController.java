package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import com.example.bd_edu.model.Examen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PresentarExamenController {

    @FXML
    private VBox preguntasVBox;

    @FXML
    private Button finalizarExamenButton;

    @FXML
    private Button volverButton;
    @FXML private TableView<Examen> tablaExamenes;
    @FXML private TableColumn<Examen, String> colTitulo;
    @FXML private TableColumn<Examen, String> colDescripcion;
    @FXML private TableColumn<Examen, LocalDate> colFechaInicio;
    @FXML private TableColumn<Examen, LocalDate> colFechaFin;
    @FXML private TableColumn<Examen, Integer> colTiempo;
    @FXML private TableColumn<Examen, String> colCategoria;

    private final Bd_Edu bd_edu = Bd_Edu.getInstance();
    private Examen examenSeleccionado;

    @FXML
    private void initialize() {
        configurarTabla();
        cargarExamenesDesdeBD();

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                examenSeleccionado = newSel;
                mostrarSeccionDePreguntas(newSel);
            }
        });
 // Temporal
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempoTotal"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    private void cargarExamenesDesdeBD() {
        List<Examen> examenes = bd_edu.obtenerExamenesEstudiante(); // Este método ya lo usas en el controlador ProgramarExamen
        ObservableList  <Examen> lista = FXCollections.observableArrayList(examenes);
        tablaExamenes.setItems(lista);
    }

    private void mostrarSeccionDePreguntas(Examen examen) {
        // Ocultar tabla
        tablaExamenes.setVisible(false);
        tablaExamenes.setManaged(false);

        // Mostrar preguntas
        preguntasVBox.setVisible(true);
        preguntasVBox.setManaged(true);

        // Limpiar preguntas anteriores
        preguntasVBox.getChildren().clear();

        // Agregar título (puede ser opcional si ya tienes uno global)
        Label tituloLabel = new Label("Examen: " + examen.getTitulo());
        tituloLabel.getStyleClass().add("label-pregunta");
        preguntasVBox.getChildren().add(tituloLabel);

        // TODO: Reemplazar con carga real desde base de datos
        for (int i = 1; i <= 3; i++) {
            Label pregunta = new Label("Pregunta " + i + ": ¿Ejemplo de pregunta?");
            pregunta.getStyleClass().add("label-pregunta");

            TextField respuesta = new TextField();
            respuesta.setPromptText("Tu respuesta...");
            respuesta.getStyleClass().add("campo-respuesta");
            respuesta.setPrefWidth(760);

            preguntasVBox.getChildren().addAll(pregunta, respuesta);
        }
    }


    @FXML
    private void finalizarExamen() {
        System.out.println("Finalizando el examen...");
        // Aquí se enviaría la respuesta al servidor y calcularía la calificación
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menuestudiante.fxml"));
            Stage stage = (Stage) volverButton.getScene().getWindow(); // Usa un componente visible en esta escena
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Estudiante");
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
