package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import com.example.bd_edu.model.Pregunta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CrearExamenController {

    @FXML private TextField nombreExamenField;
    @FXML private TextArea descripcionArea;
    @FXML private ChoiceBox<String> tipoExamenChoice;
    @FXML private ChoiceBox<String> temaChoice;
    @FXML private TextField cantidadPreguntasField;
    @FXML private TextField preguntasEstudiantesField;
    @FXML private TableView<Pregunta> tablaPreguntas;
    @FXML private TableColumn<Pregunta, String> columnaEnunciado;
    @FXML private TableColumn<Pregunta, String> columnaTipo;
    @FXML private ChoiceBox<String> agregarPreguntaChoice;
    @FXML private Button modelarExamenButton;
    @FXML private Button crearExamenButton;
    @FXML private Button agregarPreguntaButton;
    @FXML private Button eliminarPreguntaButton;

    private ObservableList<Pregunta> preguntasSeleccionadas = FXCollections.observableArrayList();
    private Bd_Edu bd_edu = Bd_Edu.getInstance();  // Singleton instancia

    private int cantidadMaximaPreguntas = 0;

    @FXML
    public void initialize() {
        tipoExamenChoice.getItems().addAll("Completar", "Única respuesta", "Multiple respuesta", "Verdadero/Falso", "Ordenar", "Emparejar");
        temaChoice.getItems().addAll(bd_edu.obtenerTemasDesdeBD());

        columnaEnunciado.setCellValueFactory(new PropertyValueFactory<>("texto"));
        columnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tablaPreguntas.setItems(preguntasSeleccionadas);
    }

    @FXML
    private void modelarExamen() {
        String nombre = nombreExamenField.getText();
        String descripcion = descripcionArea.getText();
        String tipo = tipoExamenChoice.getValue();
        String tema = temaChoice.getValue();
        String cantidadStr = cantidadPreguntasField.getText();

        if (nombre.isEmpty() || descripcion.isEmpty() || tipo == null || tema == null || cantidadStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Complete todos los campos antes de modelar el examen.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "La cantidad de preguntas debe ser un número.");
            return;
        }

        // Cargar preguntas disponibles para agregar manualmente
        List<Pregunta> disponibles = bd_edu.obtenerPreguntasPorTipoYTema(tipo, tema);
        if (disponibles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay preguntas disponibles para ese tipo y tema.");
            return;
        }

        actualizarComboAgregar(disponibles);
        preguntasSeleccionadas.clear();  // Reinicia selección
        cantidadMaximaPreguntas = cantidad;

        mostrarAlerta(Alert.AlertType.INFORMATION, "Modelo de examen preparado. Puede comenzar a agregar preguntas manualmente.");
    }


    @FXML
    private void crearExamen() {
        String nombre = nombreExamenField.getText();
        String descripcion = descripcionArea.getText();
        String tipo = tipoExamenChoice.getValue();
        String tema = temaChoice.getValue();
        String cantidadEstudiantesStr = preguntasEstudiantesField.getText();

        if (nombre.isEmpty() || descripcion.isEmpty() || tipo == null || tema == null || cantidadEstudiantesStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Complete todos los campos.");
            return;
        }

        try {
            int preguntasEstudiantes = Integer.parseInt(cantidadEstudiantesStr);
            if (preguntasEstudiantes > cantidadMaximaPreguntas) {
                mostrarAlerta(Alert.AlertType.ERROR, "Preguntas a presentar no puede ser mayor a las totales.");
                return;
            }
            int idTema = bd_edu.obtenerIdTema(tema);
            // Aquí iría la lógica de guardar el examen (a BD o estructura)
            int idExamen = bd_edu.crearExamen(nombre, descripcion, tipo, idTema, bd_edu.retornarIdUsuario(), Integer.parseInt(cantidadEstudiantesStr));

            for (int i = 0 ; i < preguntasSeleccionadas.size() ; i++) {
                Pregunta pregunta = preguntasSeleccionadas.get(i);
                bd_edu.crearPreguntaExamen(idExamen, pregunta.getId(), pregunta.getPorcentaje());
            }

            System.out.println("Examen creado:");
            System.out.println("Nombre: " + nombre);
            System.out.println("Descripción: " + descripcion);
            System.out.println("Tipo: " + tipo);
            System.out.println("Tema: " + tema);
            System.out.println("Preguntas banco: " + cantidadMaximaPreguntas);
            System.out.println("Preguntas a presentar: " + preguntasEstudiantes);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Examen creado exitosamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Preguntas Estudiantes debe ser un número.");
        }
    }

    @FXML
    private void agregarPregunta() {
        if (preguntasSeleccionadas.size() >= cantidadMaximaPreguntas) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ya alcanzaste el máximo de preguntas.");
            return;
        }

        String enunciado = agregarPreguntaChoice.getValue();
        if (enunciado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione una pregunta para agregar.");
            return;
        }

        Pregunta pregunta = bd_edu.obtenerPreguntaPorEnunciado(enunciado);
        preguntasSeleccionadas.add(pregunta);

        agregarPreguntaChoice.getItems().remove(enunciado);
        agregarPreguntaChoice.setValue(null);
    }

    @FXML
    private void eliminarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione una pregunta para eliminar.");
            return;
        }

        preguntasSeleccionadas.remove(seleccionada);
        agregarPreguntaChoice.getItems().add(seleccionada.getTexto());
    }

    private void actualizarComboAgregar(List<Pregunta> preguntasRelacionadas) {
        List<String> enunciadosDisponibles = preguntasRelacionadas.stream()
                .map(Pregunta::getTexto)
                .filter(en -> preguntasSeleccionadas.stream().noneMatch(p -> p.getTexto().equals(en)))
                .collect(Collectors.toList());
        agregarPreguntaChoice.setItems(FXCollections.observableArrayList(enunciadosDisponibles));
    }

    @FXML
    private void generarPreguntasAutomaticamente() {
        String tipo = tipoExamenChoice.getValue();
        String tema = temaChoice.getValue();
        String cantidadStr = cantidadPreguntasField.getText();

        if (tipo == null || tema == null || cantidadStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Complete Tipo de Examen, Tema y Cantidad de Preguntas.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cantidad de preguntas debe ser un número.");
            return;
        }

        List<Pregunta> disponibles = bd_edu.obtenerPreguntasPorTipoYTema(tipo, tema);
        if (disponibles.size() < cantidad) {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay suficientes preguntas disponibles.");
            return;
        }

        preguntasSeleccionadas.clear();
        List<Pregunta> seleccionadas = disponibles.subList(0, cantidad);

        double valorPorPregunta = 100.0 / cantidad;
        for (Pregunta p : seleccionadas) {
            p.setPorcentaje(BigDecimal.valueOf(valorPorPregunta));  // Asegúrate que Pregunta tenga este método
            preguntasSeleccionadas.add(p);
        }

        actualizarComboAgregar(disponibles);
        cantidadMaximaPreguntas = cantidad;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Preguntas generadas automáticamente.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
