package com.example.bd_edu.controladores;

import com.example.bd_edu.config.ConexionBD;
import com.example.bd_edu.modelo.estudiantes.CrearPresentacionEvaluacion;
import com.example.bd_edu.modelo.estudiantes.ObtenerEvaluacionesDisponibles;
import com.example.bd_edu.modelo.estudiantes.ResponderPregunta;
import com.example.bd_edu.repositorios.RepositorioEstudiante;
import com.example.bd_edu.servicios.ServicioEstudiante;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuEstudianteController {

    @FXML private Label labelBienvenida;
    @FXML private TableView<Map<String, Object>> tablaGrupos;
    @FXML private TableColumn<Map<String, Object>, String> colNombreGrupo;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaGrupo;
    @FXML private ListView<String> listaEvaluaciones;
    @FXML private Button botonPresentar;
    @FXML private VBox panelEvaluacion;
    @FXML private VBox contenedorPreguntas;
    @FXML private Button botonFinalizar;

    private ServicioEstudiante servicioEstudiante;
    private Long idEstudiante;
    private Map<String, Long> mapaNombreIdGrupo;
    private Map<String, Long> mapaNombreIdEvaluacion;
    private Long idPresentacionActual;
    private Map<Long, Long> respuestasSeleccionadas = new HashMap<>();


    @FXML
    public void initialize() {
        try {
            Connection conexion = ConexionBD.obtenerConexion();
            RepositorioEstudiante repositorio = new RepositorioEstudiante(conexion);
            servicioEstudiante = new ServicioEstudiante(repositorio);
        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", e.getMessage());
        }

        // Configurar columnas de la tabla
        colNombreGrupo.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("NOMBRE_GRUPO"))));
        colMateriaGrupo.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("NOMBRE_MATERIA"))));

        // Manejar selección en la tabla de grupos
        tablaGrupos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarEvaluaciones(String.valueOf(newVal.get("NOMBRE_GRUPO")));
            }
        });

        botonPresentar.setOnAction(event -> presentarEvaluacion());
        botonFinalizar.setOnAction(event -> finalizarEvaluacion());
    }

    public void inicializarDatos(String nombreEstudiante, Long idEstudiante) {
        this.idEstudiante = idEstudiante;
        labelBienvenida.setText("Bienvenido, " + nombreEstudiante);
        cargarGrupos();
    }

    private void cargarGrupos() {
        try {
            List<Map<String, Object>> grupos = servicioEstudiante.obtenerGruposEstudiante(idEstudiante);
            mapaNombreIdGrupo = new HashMap<>();
            ObservableList<Map<String, Object>> lista = FXCollections.observableArrayList();

            for (Map<String, Object> grupo : grupos) {
                // Usar NOMBRE_GRUPO en lugar de NOMBRE
                String nombreGrupo = String.valueOf(grupo.get("NOMBRE_GRUPO"));
                Long idGrupo = ((Number) grupo.get("ID_GRUPO")).longValue();
                mapaNombreIdGrupo.put(nombreGrupo, idGrupo);
                lista.add(grupo);

                // Log para diagnóstico
                System.out.println("Grupo cargado - Nombre: " + nombreGrupo + ", ID: " + idGrupo);
            }

            tablaGrupos.setItems(lista);

            if (!lista.isEmpty()) {
                tablaGrupos.getSelectionModel().selectFirst();
                // Verificar qué grupo se seleccionó
                Map<String, Object> grupoSeleccionado = tablaGrupos.getSelectionModel().getSelectedItem();
                System.out.println("Grupo seleccionado: " + grupoSeleccionado);
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar grupos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEvaluaciones(String nombreGrupo) {
        try {
            // Validar que el grupo existe en el mapa
            if (!mapaNombreIdGrupo.containsKey(nombreGrupo)) {
                throw new Exception("El grupo seleccionado no existe en los registros");
            }

            Long idGrupo = mapaNombreIdGrupo.get(nombreGrupo);
            System.out.println("Cargando evaluaciones para grupo ID: " + idGrupo);

            // Formatear fecha actual correctamente
            String fechaActual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

            ObtenerEvaluacionesDisponibles datos = new ObtenerEvaluacionesDisponibles(
                    idEstudiante,
                    idGrupo,
                    fechaActual
            );

            List<Map<String, Object>> evaluaciones = servicioEstudiante.obtenerEvaluacionesDisponibles(datos);
            ObservableList<String> nombresEvaluaciones = FXCollections.observableArrayList();
            mapaNombreIdEvaluacion = new HashMap<>();

            // Log para diagnóstico
            System.out.println("Evaluaciones obtenidas: " + evaluaciones.size());

            for (Map<String, Object> eval : evaluaciones) {
                String nombre = String.valueOf(eval.get("NOMBRE"));
                Long idEval = ((Number) eval.get("ID_EVALUACION")).longValue();

                // Verificar que la evaluación pertenece al grupo correcto
                Long idGrupoEvaluacion = ((Number) eval.get("ID_GRUPO")).longValue();
                if (!idGrupo.equals(idGrupoEvaluacion)) {
                    System.out.println("Evaluación no coincide con grupo - ID Grupo Evaluación: " +
                            idGrupoEvaluacion + ", ID Grupo Seleccionado: " + idGrupo);
                    continue;
                }

                nombresEvaluaciones.add(nombre);
                mapaNombreIdEvaluacion.put(nombre, idEval);

                // Log para diagnóstico
                System.out.println("Evaluación agregada: " + nombre + " (ID: " + idEval + ")");
            }

            listaEvaluaciones.setItems(nombresEvaluaciones);

            if (!nombresEvaluaciones.isEmpty()) {
                listaEvaluaciones.getSelectionModel().selectFirst();
            } else {
                mostrarAlerta("Sin evaluaciones", "No hay evaluaciones disponibles para este grupo");
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar evaluaciones", e.getMessage());
            e.printStackTrace();
        }
    }

    private void presentarEvaluacion() {
        String seleccion = listaEvaluaciones.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Selección requerida", "Por favor selecciona una evaluación para presentar.");
            return;
        }

        try {
            Long idEvaluacion = mapaNombreIdEvaluacion.get(seleccion);
            if (idEvaluacion == null) {
                throw new Exception("ID de evaluación no encontrado");
            }

            // Verificar que el estudiante existe
            if (idEstudiante == null || idEstudiante <= 0) {
                throw new Exception("ID de estudiante inválido");
            }

            CrearPresentacionEvaluacion datos = new CrearPresentacionEvaluacion(
                    idEvaluacion,
                    idEstudiante,
                    "127.0.0.1"
            );

            System.out.println("Intentando crear presentación para evaluación: " + idEvaluacion);

            Map<String, Object> respuesta = servicioEstudiante.crearPresentacionEvaluacion(datos);
            System.out.println("Respuesta del servicio: " + respuesta);

            // Manejar caso especial de presentación existente no terminada
            if (respuesta.get("p_mensaje_out") != null &&
                    ((String)respuesta.get("p_mensaje_out")).contains("Ya existe una presentación no finalizada")) {

                Long idPresentacion = ((Number) respuesta.get("p_id_presentacion_out")).longValue();
                if (idPresentacion != null && idPresentacion > 0) {
                    idPresentacionActual = idPresentacion;
                    mostrarAlerta("Presentación existente", "Continuarás con la presentación no finalizada anteriormente");

                    // Cargar preguntas de la evaluación
                    cargarPreguntasEvaluacion(idEvaluacion);

                    // Mostrar panel de evaluación
                    panelEvaluacion.setVisible(true);
                    botonPresentar.setDisable(true);
                    return;
                }
            }

            Long idPresentacion = ((Number) respuesta.get("id_presentacion")).longValue();

            if (idPresentacion == null || idPresentacion <= 0) {
                throw new Exception("No se pudo obtener un ID de presentación válido");
            }

            idPresentacionActual = idPresentacion;
            System.out.println("Presentación creada con ID: " + idPresentacionActual);

            // Cargar preguntas de la evaluación
            cargarPreguntasEvaluacion(idEvaluacion);

            // Mostrar panel de evaluación
            panelEvaluacion.setVisible(true);
            botonPresentar.setDisable(true);

        } catch (Exception e) {
            String mensajeError = "Error al iniciar evaluación: " + e.getMessage();
            System.err.println(mensajeError);
            e.printStackTrace();
            mostrarAlerta("Error al iniciar evaluación", mensajeError);
        }
    }

    private void cargarPreguntasEvaluacion(Long idEvaluacion) {
        try {
            contenedorPreguntas.getChildren().clear();
            respuestasSeleccionadas.clear();

            System.out.println("Intentando obtener preguntas para evaluación ID: " + idEvaluacion);
            List<Map<String, Object>> preguntas = servicioEstudiante.obtenerPreguntasEvaluacion(idEvaluacion);
            System.out.println("Preguntas obtenidas: " + preguntas.size());

            if (preguntas == null || preguntas.isEmpty()) {
                mostrarAlerta("Sin preguntas", "Esta evaluación no tiene preguntas disponibles");
                return;
            }

            for (Map<String, Object> pregunta : preguntas) {
                Long idPregunta = ((Number) pregunta.get("id_pregunta")).longValue();
                String textoPregunta = (String) pregunta.get("texto");
                String tipoPregunta = (String) pregunta.get("tipo");

                // Crear contenedor de pregunta
                VBox contenedorPregunta = new VBox(5);
                contenedorPregunta.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

                // Añadir texto de la pregunta
                Label lblPregunta = new Label(textoPregunta);
                lblPregunta.setFont(Font.font(14));
                contenedorPregunta.getChildren().add(lblPregunta);

                // Obtener opciones de respuesta
                List<Map<String, Object>> opciones = servicioEstudiante.obtenerOpcionesPregunta(idPregunta);

                // Crear grupo de toggle para las opciones
                ToggleGroup grupoOpciones = new ToggleGroup();

                for (Map<String, Object> opcion : opciones) {
                    Long idOpcion = ((Number) opcion.get("id_opcion")).longValue();
                    String textoOpcion = (String) opcion.get("texto");

                    RadioButton rbOpcion = new RadioButton(textoOpcion);
                    rbOpcion.setToggleGroup(grupoOpciones);
                    rbOpcion.setUserData(idOpcion);
                    rbOpcion.setStyle("-fx-font-size: 13;");

                    // Manejar selección de opción
                    rbOpcion.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            respuestasSeleccionadas.put(idPregunta, idOpcion);
                            registrarRespuesta(idPregunta, idOpcion);
                        }
                    });

                    contenedorPregunta.getChildren().add(rbOpcion);
                }

                contenedorPreguntas.getChildren().add(contenedorPregunta);
            }

        } catch (Exception e) {
            mostrarAlerta("Error al cargar preguntas", e.getMessage());
            e.printStackTrace();
        }
    }

    private void registrarRespuesta(Long idPregunta, Long idOpcion) {
        try {
            if (idPresentacionActual == null || idPregunta == null || idOpcion == null) {
                throw new Exception("Datos incompletos para registrar respuesta");
            }

            System.out.println("Intentando registrar respuesta - Pregunta: " + idPregunta +
                    ", Opción: " + idOpcion);

            ResponderPregunta datos = new ResponderPregunta(
                    idPresentacionActual,
                    idPregunta,
                    idOpcion
            );

            Map<String, Object> resultado = servicioEstudiante.responderPregunta(datos);

            // Añadir log del resultado
            System.out.println("Resultado del registro de respuesta: " + resultado);

        } catch (Exception e) {
            System.err.println("Error al registrar respuesta: " + e.getMessage());
            mostrarAlerta("Error al registrar respuesta", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void finalizarEvaluacion() {
        try {
            if (idPresentacionActual == null) {
                throw new Exception("No hay una presentación activa para finalizar");
            }

            System.out.println("Finalizando evaluación con ID presentación: " + idPresentacionActual);

            Map<String, Object> resultado = servicioEstudiante.finalizarEvaluacion(idPresentacionActual);

            if (resultado == null) {
                throw new Exception("El servicio devolvió un resultado nulo");
            }

            Float calificacion = (Float) resultado.get("calificacion");

            if (calificacion == null) {
                throw new Exception("No se pudo obtener la calificación");
            }

            // Mostrar resultados
            mostrarAlerta("Evaluación finalizada",
                    String.format("Tu calificación es: %.1f/5.0", calificacion));

            // Restablecer la interfaz
            panelEvaluacion.setVisible(false);
            botonPresentar.setDisable(false);
            contenedorPreguntas.getChildren().clear();
            respuestasSeleccionadas.clear();

        } catch (Exception e) {
            String mensajeError = "Error al finalizar evaluación: " + e.getMessage();
            System.err.println(mensajeError);
            e.printStackTrace();
            mostrarAlerta("Error al finalizar evaluación", mensajeError);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}