package com.example.bd_edu.controladores;

import com.example.bd_edu.config.ConexionBD;
import com.example.bd_edu.modelo.profesores.*;
import com.example.bd_edu.repositorios.RepositorioProfesor;
import com.example.bd_edu.servicios.ServicioProfesor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProfesorController {

    // Componentes de la interfaz
    @FXML private Label labelBienvenida;
    @FXML private TabPane tabPanePrincipal;
    @FXML private Tab tabMaterias;
    @FXML private Tab tabTemas;
    @FXML private Tab tabEvaluaciones;

    // Pestaña Materias
    @FXML private TableView<Map<String, Object>> tablaMateriasGrupos;
    @FXML private TableColumn<Map<String, Object>, String> colMateria;
    @FXML private TableColumn<Map<String, Object>, String> colGrupo;

    // Pestaña Temas
    @FXML private TableView<Map<String, Object>> tablaTemas;
    @FXML private TableColumn<Map<String, Object>, String> colNombreTema;
    @FXML private TableView<Map<String, Object>> tablaPreguntas;
    @FXML private TableColumn<Map<String, Object>, String> colEnunciadoPregunta;
    @FXML private TextField txtNuevaPregunta;
    @FXML private ComboBox<String> cbTipoPregunta;
    @FXML private VBox panelRespuestas;
    @FXML private TextField txtNuevaRespuesta;
    @FXML private CheckBox chkEsCorrecta;
    private Long idPreguntaSeleccionada;

    // Pestaña Evaluaciones
    @FXML private TextField txtNombreEvaluacion;
    @FXML private TextField txtTiempoMaximo;
    @FXML private TextField txtPorcentajeAprobacion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtCantidadPreguntasAletorias;
    @FXML private TextField txtPorcentajeCurso;
    @FXML private TextField txtCantidadPreguntas;
    @FXML private ComboBox<String> cbGruposEvaluacion;
    @FXML private ComboBox<String> cbTemasEvaluacion;
    @FXML private TableView<Map<String, Object>> tablaPreguntasEvaluacion;
    @FXML private TableColumn<Map<String, Object>, String> colEnunciadoPreguntaEval;
    @FXML private CheckBox chkTieneTiempoMaximo;
    // Eliminado: @FXML private TextField txtTiempoPorPregunta;
    @FXML private TextField txtPorcentajePregunta;
    @FXML private VBox panelAsignacionPreguntas;

    // Variables de estado
    private ServicioProfesor servicioProfesor;
    private Long idProfesor;
    private Long idEvaluacionActual;
    private Map<Long, String> mapaTemas = new HashMap<>();
    private Map<Long, String> mapaMaterias = new HashMap<>();

    //Botones
    @FXML
    private Button btnCrearEvaluacion;
    @FXML
    private Button btnAsignarPregunta;

    @FXML
    public void initialize() {
        try {
            Connection conexion = ConexionBD.obtenerConexion();
            servicioProfesor = new ServicioProfesor(new RepositorioProfesor(conexion));
        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", e.getMessage());
        }

        configurarInterfaces();
        configurarEventListeners();
    }

    private void configurarInterfaces() {
        // Configuración de tablas
        colMateria.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("nombre_materia"))));
        colGrupo.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("nombre_grupo"))));
        colNombreTema.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("nombre"))));
        colEnunciadoPregunta.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("enunciado"))));
        colEnunciadoPreguntaEval.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("enunciado"))));

        // Configuración de combobox
        cbTipoPregunta.getItems().addAll("Selección única", "Selección múltiple", "Verdadero/Falso", "Ordenamiento");
        cbTipoPregunta.getSelectionModel().selectFirst();

        // Eliminada la configuración del listener para txtTiempoPorPregunta
        // chkTieneTiempoMaximo.selectedProperty().addListener((obs, oldVal, newVal) -> {
        //     txtTiempoPorPregunta.setDisable(!newVal);
        //     if (!newVal) txtTiempoPorPregunta.clear();
        // });
    }

    private void configurarEventListeners() {
        tabPanePrincipal.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabMaterias) cargarMateriasYGrupos();
            else if (newTab == tabTemas) cargarTemas();
        });

        tablaTemas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarPreguntasPorTema((Long) newVal.get("id_tema"));
        });

        tablaPreguntas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) mostrarPanelRespuestas((Long) newVal.get("id_pregunta"));
        });

        cbTemasEvaluacion.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarPreguntasParaEvaluacion();
        });
    }

    public void inicializarDatos(String nombreProfesor, Long idProfesor) {
        this.idProfesor = idProfesor;
        labelBienvenida.setText("Bienvenido, " + nombreProfesor);
        cargarMateriasYGrupos();
        cargarTemasParaEvaluacion();
    }

    // Métodos para la pestaña Materias
    private void cargarMateriasYGrupos() {
        try {
            Map<String, Object> resultadoMaterias = servicioProfesor.obtenerMaterias();
            if (!"EXITO".equals(resultadoMaterias.get("estado"))) {
                mostrarAlerta("Error", (String) resultadoMaterias.get("mensaje"));
                return;
            }

            ObservableList<Map<String, Object>> datosCombinados = FXCollections.observableArrayList();
            ObservableList<String> nombresGrupos = FXCollections.observableArrayList(); // Nuevo: Lista para grupos
            List<Map<String, Object>> materias = (List<Map<String, Object>>) resultadoMaterias.get("datos");

            for (Map<String, Object> materia : materias) {
                Long idMateria = (Long) materia.get("id_materia");
                String nombreMateria = (String) materia.get("nombre");
                mapaMaterias.put(idMateria, nombreMateria);

                Map<String, Object> resultadoGrupos = servicioProfesor.obtenerGruposPorMateria(idMateria);
                if (!"EXITO".equals(resultadoGrupos.get("estado"))) continue;

                List<Map<String, Object>> grupos = (List<Map<String, Object>>) resultadoGrupos.get("datos");
                if (grupos.isEmpty()) {
                    datosCombinados.add(crearFilaMateria(nombreMateria, "Sin grupos asignados"));
                } else {
                    grupos.forEach(grupo -> {
                        datosCombinados.add(crearFilaMateriaGrupo(nombreMateria, grupo));
                        nombresGrupos.add((String) grupo.get("nombre")); // Nuevo: Agregar nombre del grupo
                    });
                }
            }

            tablaMateriasGrupos.setItems(datosCombinados);
            cbGruposEvaluacion.setItems(nombresGrupos); // Nuevo: Cargar grupos en el ComboBox
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar materias y grupos: " + e.getMessage());
        }
    }

    private Map<String, Object> crearFilaMateria(String nombreMateria, String textoGrupo) {
        Map<String, Object> fila = new HashMap<>();
        fila.put("nombre_materia", nombreMateria);
        fila.put("nombre_grupo", textoGrupo);
        return fila;
    }

    private Map<String, Object> crearFilaMateriaGrupo(String nombreMateria, Map<String, Object> grupo) {
        Map<String, Object> fila = new HashMap<>();
        fila.put("nombre_materia", nombreMateria);
        fila.put("nombre_grupo", grupo.get("nombre"));
        fila.put("id_materia", grupo.get("id_materia"));
        fila.put("id_grupo", grupo.get("id_grupo"));
        return fila;
    }

    // Métodos para la pestaña Temas
    private void cargarTemas() {
        try {
            Map<String, Object> resultado = servicioProfesor.obtenerTemas();
            if ("EXITO".equals(resultado.get("estado"))) {
                List<Map<String, Object>> temas = (List<Map<String, Object>>) resultado.get("datos");
                tablaTemas.setItems(FXCollections.observableArrayList(temas));
                actualizarMapaTemas(temas);

                if (!temas.isEmpty()) tablaTemas.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los temas: " + e.getMessage());
        }
    }

    private void actualizarMapaTemas(List<Map<String, Object>> temas) {
        mapaTemas.clear();
        temas.forEach(tema -> mapaTemas.put((Long) tema.get("id_tema"), (String) tema.get("nombre")));
    }

    private void cargarPreguntasPorTema(Long idTema) {
        try {
            Map<String, Object> resultado = servicioProfesor.obtenerPreguntasPorTema(idTema);
            if ("EXITO".equals(resultado.get("estado"))) {
                tablaPreguntas.setItems(FXCollections.observableArrayList(
                        (List<Map<String, Object>>) resultado.get("datos")));
                panelRespuestas.getChildren().clear();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las preguntas: " + e.getMessage());
        }
    }

    @FXML
    private void agregarPregunta() {
        String enunciado = txtNuevaPregunta.getText().trim();
        if (enunciado.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el enunciado de la pregunta");
            return;
        }

        Map<String, Object> temaSeleccionado = tablaTemas.getSelectionModel().getSelectedItem();
        if (temaSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un tema");
            return;
        }

        CrearPregunta datos = new CrearPregunta();
        datos.setEnunciado(enunciado);
        datos.setEsPublica("S");
        datos.setTipoPregunta(cbTipoPregunta.getValue());
        datos.setIdTema((Long) temaSeleccionado.get("id_tema"));
        datos.setIdProfesor(idProfesor);

        try {
            Map<String, Object> resultado = servicioProfesor.crearPregunta(datos);
            if ("EXITO".equals(resultado.get("estado"))) {
                txtNuevaPregunta.clear();
                cargarPreguntasPorTema(datos.getIdTema());
            } else {
                mostrarAlerta("Error", (String) resultado.get("mensaje"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear la pregunta: " + e.getMessage());
        }
    }

    private void mostrarPanelRespuestas(Long idPregunta) {
        this.idPreguntaSeleccionada = idPregunta;
        panelRespuestas.getChildren().clear();

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        txtNuevaRespuesta = new TextField();
        txtNuevaRespuesta.setPrefWidth(300);
        chkEsCorrecta = new CheckBox("Es correcta");

        formulario.add(new Label("Nueva respuesta:"), 0, 0);
        formulario.add(txtNuevaRespuesta, 1, 0);
        formulario.add(chkEsCorrecta, 1, 1);
        formulario.add(new Button("Agregar respuesta") {{
            setOnAction(e -> agregarRespuesta());
        }}, 1, 2);

        panelRespuestas.getChildren().add(formulario);
    }

    @FXML
    private void agregarRespuesta() {
        if (idPreguntaSeleccionada == null) {
            mostrarAlerta("Error", "No hay pregunta seleccionada");
            return;
        }

        String descripcion = txtNuevaRespuesta.getText().trim();
        if (descripcion.isEmpty()) {
            mostrarAlerta("Error", "Ingrese la respuesta");
            return;
        }

        CrearOpcionRespuesta datos = new CrearOpcionRespuesta();
        datos.setDescripcion(descripcion);
        datos.setEsCorrecta(chkEsCorrecta.isSelected() ? "S" : "N");
        datos.setIdPregunta(idPreguntaSeleccionada);

        try {
            Map<String, Object> resultado = servicioProfesor.crearOpcionRespuesta(datos);
            if ("EXITO".equals(resultado.get("estado"))) {
                txtNuevaRespuesta.clear();
                chkEsCorrecta.setSelected(false);
                mostrarPanelRespuestas(idPreguntaSeleccionada);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar la respuesta: " + e.getMessage());
        }
    }

    // Métodos para la pestaña Evaluaciones
    private void cargarTemasParaEvaluacion() {
        try {
            Map<String, Object> resultado = servicioProfesor.obtenerTemas();
            if ("EXITO".equals(resultado.get("estado"))) {
                List<Map<String, Object>> temas = (List<Map<String, Object>>) resultado.get("datos");
                cbTemasEvaluacion.getItems().clear();
                temas.forEach(tema -> {
                    cbTemasEvaluacion.getItems().add((String) tema.get("nombre"));
                    mapaTemas.put((Long) tema.get("id_tema"), (String) tema.get("nombre"));
                });
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los temas: " + e.getMessage());
        }
    }

    private void cargarPreguntasParaEvaluacion() {
        String temaSeleccionado = cbTemasEvaluacion.getValue();
        if (temaSeleccionado == null) return;

        Long idTema = obtenerIdTemaPorNombre(temaSeleccionado);
        if (idTema == null) return;

        try {
            Map<String, Object> resultado = servicioProfesor.obtenerPreguntasPorTema(idTema);
            if ("EXITO".equals(resultado.get("estado"))) {
                tablaPreguntasEvaluacion.setItems(FXCollections.observableArrayList(
                        (List<Map<String, Object>>) resultado.get("datos")));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las preguntas");
        }
    }

    private Long obtenerIdTemaPorNombre(String nombreTema) {
        return mapaTemas.entrySet().stream()
                .filter(entry -> nombreTema.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void crearEvaluacion() {
        if (!validarCamposEvaluacion()) return;

        try {
            CrearEvaluacion datos = construirDatosEvaluacion();

            // Validar que preguntas aleatorias no superen el total
            int totalPreguntas = Integer.parseInt(txtCantidadPreguntas.getText());
            if (!txtCantidadPreguntasAletorias.getText().trim().isEmpty()) {
                int preguntasAleatorias = Integer.parseInt(txtCantidadPreguntasAletorias.getText());
                if (preguntasAleatorias > totalPreguntas) {
                    mostrarAlerta("Error", "Las preguntas aleatorias no pueden ser más que el total de preguntas");
                    return;
                }
                datos.setPreguntasAleatorias(preguntasAleatorias);
            } else {
                datos.setPreguntasAleatorias(0);
            }

            Map<String, Object> resultado = servicioProfesor.crearEvaluacion(datos);
            if (!"EXITO".equals(resultado.get("estado"))) {
                mostrarAlerta("Error", (String) resultado.get("mensaje"));
                return;
            }

            idEvaluacionActual = (Long) resultado.get("idEvaluacion");
            panelAsignacionPreguntas.setDisable(false);
            mostrarAlerta("Éxito", "Evaluación creada correctamente");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear la evaluación: " + e.getMessage());
        }
    }


    private boolean validarCamposEvaluacion() {
        // Validar campos obligatorios
        if (txtNombreEvaluacion.getText().trim().isEmpty() ||
                cbGruposEvaluacion.getSelectionModel().isEmpty() ||
                cbTemasEvaluacion.getSelectionModel().isEmpty() ||
                txtPorcentajeAprobacion.getText().trim().isEmpty() ||
                txtTiempoMaximo.getText().trim().isEmpty() ||
                txtPorcentajeCurso.getText().trim().isEmpty() ||
                dpFechaInicio.getValue() == null ||
                dpFechaFin.getValue() == null ||
                txtCantidadPreguntas.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Complete todos los campos obligatorios");
            return false;
        }

        // Validar fechas
        if (dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
            mostrarAlerta("Error", "La fecha de inicio no puede ser posterior a la fecha fin");
            return false;
        }

        // Validar números
        try {
            int porcentajeAprobacion = Integer.parseInt(txtPorcentajeAprobacion.getText());
            int tiempoMaximo = Integer.parseInt(txtTiempoMaximo.getText());
            int porcentajeCurso = Integer.parseInt(txtPorcentajeCurso.getText());
            int totalPreguntas = Integer.parseInt(txtCantidadPreguntas.getText());

            if (porcentajeAprobacion < 0 || porcentajeAprobacion > 100 ||
                    tiempoMaximo <= 0 ||
                    porcentajeCurso < 0 || porcentajeCurso > 100 ||
                    totalPreguntas <= 0) {
                mostrarAlerta("Error", "Valores numéricos inválidos");
                return false;
            }

            // Validar preguntas aleatorias (opcional)
            if (!txtCantidadPreguntasAletorias.getText().trim().isEmpty()) {
                int cantidad = Integer.parseInt(txtCantidadPreguntasAletorias.getText());
                if (cantidad < 0 || cantidad > totalPreguntas) {
                    mostrarAlerta("Error", "Número de preguntas aleatorias inválido");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese valores numéricos válidos");
            return false;
        }

        return true;
    }

    private CrearEvaluacion construirDatosEvaluacion() {
        CrearEvaluacion datos = new CrearEvaluacion();
        datos.setNombre(txtNombreEvaluacion.getText());
        datos.setPorcentajeAprobacion(Integer.parseInt(txtPorcentajeAprobacion.getText()));
        datos.setTiempoMaximo(Integer.parseInt(txtTiempoMaximo.getText()));
        datos.setPorcentajeCurso(Integer.parseInt(txtPorcentajeCurso.getText()));
        datos.setFechaInicio(LocalDateTime.of(dpFechaInicio.getValue(), LocalTime.now()));
        datos.setFechaFin(LocalDateTime.of(dpFechaFin.getValue(), LocalTime.now().plusHours(2)));

        // Obtener el número total de preguntas
        int cantidadPreguntas = Integer.parseInt(txtCantidadPreguntas.getText());
        datos.setCantidadPreguntas(cantidadPreguntas);

        // Manejar preguntas aleatorias (opcional)
        if (!txtCantidadPreguntasAletorias.getText().trim().isEmpty()) {
            int preguntasAleatorias = Integer.parseInt(txtCantidadPreguntasAletorias.getText());
            datos.setPreguntasAleatorias(preguntasAleatorias);
        } else {
            datos.setPreguntasAleatorias(0);
        }

        datos.setIdTema(obtenerIdTemaPorNombre(cbTemasEvaluacion.getValue()));
        datos.setIdGrupo(obtenerIdGrupoPorNombre(cbGruposEvaluacion.getValue()));
        datos.setIdProfesor(idProfesor);

        return datos;
    }

    private Long obtenerIdGrupoPorNombre(String nombreGrupo) {
        try {
            Map<String, Object> resultadoMaterias = servicioProfesor.obtenerMaterias();
            if (!"EXITO".equals(resultadoMaterias.get("estado"))) return null;

            for (Map<String, Object> materia : (List<Map<String, Object>>) resultadoMaterias.get("datos")) {
                Long idMateria = (Long) materia.get("id_materia");
                Map<String, Object> resultadoGrupos = servicioProfesor.obtenerGruposPorMateria(idMateria);

                if ("EXITO".equals(resultadoGrupos.get("estado"))) {
                    for (Map<String, Object> grupo : (List<Map<String, Object>>) resultadoGrupos.get("datos")) {
                        if (nombreGrupo.equals(grupo.get("nombre"))) {
                            return (Long) grupo.get("id_grupo");
                        }
                    }
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar ID del grupo: " + e.getMessage());
        }
        return null;
    }

    @FXML
    private void asignarPreguntasAleatorias() {
        if (idEvaluacionActual == null) {
            mostrarAlerta("Error", "No hay evaluación seleccionada");
            return;
        }

        try {
            // Simplemente llamamos al procedimiento existente
            Map<String, Object> resultado = servicioProfesor.asignarPreguntasAleatorias(idEvaluacionActual);

            if ("EXITO".equals(resultado.get("estado"))) {
                mostrarAlerta("Éxito", "Preguntas aleatorias asignadas correctamente");
                cargarPreguntasAsignadasAEvaluacion();
            } else {
                mostrarAlerta("Error", (String) resultado.get("mensaje"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al asignar preguntas: " + e.getMessage());
        }
    }

    @FXML
    private void asignarPreguntaManual() {
        if (idEvaluacionActual == null) {
            mostrarAlerta("Error", "No hay evaluación seleccionada");
            return;
        }

        Map<String, Object> preguntaSeleccionada = tablaPreguntasEvaluacion.getSelectionModel().getSelectedItem();
        if (preguntaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta");
            return;
        }

        try {
            AsignarPreguntaAEvaluacion datos = new AsignarPreguntaAEvaluacion();
            datos.setIdPregunta((Long) preguntaSeleccionada.get("id_pregunta"));
            datos.setIdEvaluacion(idEvaluacionActual);

            // Manejo del porcentaje (opcional)
            if (!txtPorcentajePregunta.getText().trim().isEmpty()) {
                // Validar que el porcentaje sea numérico y esté en rango válido
                int porcentaje = Integer.parseInt(txtPorcentajePregunta.getText());
                if (porcentaje < 0 || porcentaje > 100) {
                    mostrarAlerta("Error", "El porcentaje debe estar entre 0 y 100");
                    return;
                }
                datos.setPorcentaje(porcentaje);
            } else {
                // Si no se especifica porcentaje, calcularlo automáticamente
                Map<String, Object> infoEvaluacion = servicioProfesor.obtenerInfoEvaluacion(idEvaluacionActual);
                if ("EXITO".equals(infoEvaluacion.get("estado"))) {
                    int totalPreguntas = (int) infoEvaluacion.get("cantidad_preguntas");
                    if (totalPreguntas > 0) {
                        datos.setPorcentaje(100 / totalPreguntas); // Distribución equitativa
                    } else {
                        datos.setPorcentaje(0);
                    }
                } else {
                    datos.setPorcentaje(0);
                }
            }

            // Manejo del tiempo - solo basado en el CheckBox, el tiempo se asume en PL/SQL
            datos.setTieneTiempoMaximo(chkTieneTiempoMaximo.isSelected() ? "S" : "N");
            // Aquí, si chkTieneTiempoMaximo está seleccionado, el valor de tiempo por pregunta
            // se asume que se manejará en el PL/SQL o que no requiere un valor específico desde la UI.
            // Si necesitas enviar un valor por defecto o un placeholder, podrías hacerlo.
            // Por ahora, si no hay un TextField, enviamos 0 o un valor por defecto que tu PL/SQL entienda.
            datos.setTiempoPorPregunta(0); // O el valor que tu PL/SQL espera por defecto cuando hay tiempo máximo

            Map<String, Object> resultado = servicioProfesor.asignarPreguntaAEvaluacion(datos);

            if ("EXITO".equals(resultado.get("estado"))) {
                mostrarAlerta("Éxito", "Pregunta asignada correctamente");
                // Limpiar campos después de asignar
                txtPorcentajePregunta.clear();
                chkTieneTiempoMaximo.setSelected(false);
                // Eliminado: txtTiempoPorPregunta.clear();
                cargarPreguntasAsignadasAEvaluacion();
            } else {
                mostrarAlerta("Error", (String) resultado.get("mensaje"));
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese valores numéricos válidos");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al asignar pregunta: " + e.getMessage());
        }
    }

    // Eliminado: Este método ya no es necesario tal cual, ya que la lógica está en asignarPreguntaManual()
    // private AsignarPreguntaAEvaluacion construirDatosAsignacionPregunta(Map<String, Object> pregunta) {
    //     AsignarPreguntaAEvaluacion datos = new AsignarPreguntaAEvaluacion();
    //     datos.setIdPregunta((Long) pregunta.get("id_pregunta"));
    //     datos.setIdEvaluacion(idEvaluacionActual);
    //     datos.setPorcentaje(Integer.parseInt(txtPorcentajePregunta.getText()));
    //     datos.setTieneTiempoMaximo(chkTieneTiempoMaximo.isSelected() ? "S" : "N");
    //     datos.setTiempoPorPregunta(chkTieneTiempoMaximo.isSelected() ?
    //             Integer.parseInt(txtTiempoPorPregunta.getText()) : 0);
    //     return datos;
    // }

    private void cargarPreguntasAsignadasAEvaluacion() {
        if (idEvaluacionActual == null) return;

        try {
            Map<String, Object> resultado = servicioProfesor.obtenerPreguntasDeEvaluacion(idEvaluacionActual);
            if ("EXITO".equals(resultado.get("estado"))) {
                tablaPreguntasEvaluacion.setItems(FXCollections.observableArrayList(
                        (List<Map<String, Object>>) resultado.get("datos")));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las preguntas asignadas");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje) {{
            setTitle(titulo);
            setHeaderText(null);
        }}.showAndWait();
    }
}