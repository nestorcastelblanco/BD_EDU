package com.example.bd_edu;

import com.example.bd_edu.model.Curso;
import com.example.bd_edu.model.Examen;
import com.example.bd_edu.model.Pregunta;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.Getter;
import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Bd_Edu {

    private static final Logger LOGGER = Logger.getLogger(Bd_Edu.class.getName());

    private Connection connection;

    private String rolUsuario;

    private int idUsuario;

    private static Bd_Edu bd_edu;

    public static Bd_Edu getInstance() {
        if (bd_edu == null) {
            bd_edu = new Bd_Edu();
        }
        return bd_edu;
    }

    public int retornarIdUsuario() {
        return idUsuario;
    }

    private Bd_Edu() {
        try {
            String url = "jdbc:oracle:thin:@//localhost:1521/xe";
            String user = "SYSTEM";
            String password = "Arango2004";

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a Oracle.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a Oracle", e);
        }
    }

    public boolean iniciarSesion(String usuario, String clave) {
        try (CallableStatement cs = connection.prepareCall("{ call validar_usuario_con_rol(?, ?, ?, ?, ?) }")) {

            cs.setString(1, usuario);
            cs.setString(2, clave);

            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(3);
            String rol = cs.getString(4);
            int idUsuario = cs.getInt(5);

            if (resultado > 0 && rol != null && !rol.isEmpty()) {
                this.rolUsuario = rol;
                this.idUsuario = idUsuario;
                System.out.println("Rol: " + rol);
                System.out.println("Id: " + idUsuario);
                return true;
            } else {
                LOGGER.warning("Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar iniciar sesión", e);
        }
        return false;
    }

    public int obtenerIdTema(String tema) {
        int idTema = -1;  // Valor por defecto si no se encuentra el tema
        String sql = "{ call obtener_id_tema_por_nombre(?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, tema);
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute();
            idTema = cs.getInt(2);  // Obtener el valor del OUT parameter

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener ID del tema", e);
        }
        return idTema;
    }

    public List<String> obtenerTemasDesdeBD() {
        List<String> temas = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM TEMA";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                temas.add(rs.getString("NOMBRE"));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los temas desde la base de datos", e);
        }
        return temas;
    }

    public int obtenerIdBanco (int idTema) {
        int idBanco = -1;
        String sql = "{ call obtener_id_banco(?,?) }";
        try (CallableStatement cs = connection.prepareCall(sql)){
            cs.setInt(1, idTema);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            idBanco = cs.getInt(2);
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener ID del banco", e);
        }
        return idBanco;
    }


    /**
     * Método para almacenar la respuesta de pregunta Completar
     */
    public void guardarRespuestaCompletar(int id_pregunta, String respuesta, boolean esCorrecta) {
        String sql = "{ call guardar_respuesta(?,?,?) }";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, id_pregunta);
            cs.setString(2, respuesta);
            cs.setInt(3, esCorrecta ? 1 : 0);
            cs.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la respuesta", e);
        }
    }

    /**
     * Método para almacenar una pregunta de completar
     */
    public void crearPreguntaCompletar(String pregunta, String tipo, double porcentaje, String respuesta, String estado, int tiempo, int id_banco, int id_tema) {
        int id_pregunta = -1;
        String sql = "{ call crear_pregunta(?,?,?,?,?,?,?,?) }";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, pregunta);
            cs.setString(2, tipo);
            cs.setDouble(3, porcentaje);
            cs.setString(4, estado);
            cs.setInt(5, tiempo);
            cs.setInt(6, id_banco);
            cs.setInt(7, id_tema);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            id_pregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la pregunta", e);
        }
        guardarRespuestaCompletar(id_pregunta, respuesta, true);
    }


    /**
     * Método para cargar ventanas
     *
     **/
    public static void loadStage(String url, Event event) {
        try {
            ((Node) (event.getSource())).getScene().getWindow().hide();

            Parent root = FXMLLoader.load(Objects.requireNonNull(QuizApplication.class.getResource(url)));
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Plataforma");
            newStage.centerOnScreen();
            newStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana", e);
        }
    }

    //
    // METODOS DE ALMACENAMIENTO DE PREGUNTA/RESPUESTA SELECCION UNICA
    //

    /**
     * Crea una pregunta de tipo "Única respuesta" y retorna el ID generado.
     *
     * @param enunciado Texto de la pregunta
     * @param estado Estado de la pregunta ("Activo", "Inactivo")
     * @param tiempo Tiempo en segundos para responder
     * @param idBanco ID del banco de preguntas asociado
     * @param idTema ID del tema asociado
     * @return ID de la pregunta creada o -1 si falla
     */
    public int crearPreguntaUnicaRespuesta(String enunciado, String estado, int tiempo, int idBanco, int idTema, double porcentaje ) {
        int idPregunta = -1;
        String sql = "{ call crear_pregunta_unica(?,?,?,?,?,?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciado);
            cs.setString(2, "Única respuesta");
            cs.setDouble(3, porcentaje);
            cs.setString(4, estado);
            cs.setInt(5, tiempo);
            cs.setInt(6, idBanco);
            cs.setInt(7, idTema);
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();
            idPregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar pregunta de única respuesta", e);
        }
        return idPregunta;
    }

    /**
     * Guarda una respuesta asociada a una pregunta.
     * @param idPregunta ID de la pregunta a la que pertenece la respuesta
     * @param textoRespuesta Texto de la respuesta
     * @param esCorrecta true si es la respuesta correcta, false si no
     */
    public void guardarRespuestaUnica(int idPregunta, String textoRespuesta, boolean esCorrecta) {
        String sql = "{ call guardar_respuesta_unica(?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idPregunta);
            cs.setString(2, textoRespuesta);
            cs.setBoolean(3, esCorrecta);

            cs.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {  // ORA-00001: unique constraint violated
                LOGGER.log(Level.WARNING, "Respuesta duplicada no insertada (ID_PREGUNTA: " + idPregunta + ", RESPUESTA: " + textoRespuesta + ")");
            } else {
                LOGGER.log(Level.SEVERE, "Error al guardar respuesta", e);
            }
        }
    }

    //
    // METODOS DE ALMACENAMIENTO DE PREGUNTA/RESPUESTA SELECCION MULTIPLE
    //

    public int crearPreguntaSeleccionMultiple(String enunciado, String estado, int tiempoLimite, int idBanco, int idTema, double porcentaje) {
        int idPregunta = -1;
        String sql = "{ call crear_pregunta_multiple(?,?,?,?,?,?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciado);
            cs.setString(2, "Multiple respuesta");
            cs.setDouble(3, porcentaje);
            cs.setString(4, estado);
            cs.setInt(5, tiempoLimite);
            cs.setInt(6, idBanco);
            cs.setInt(7, idTema);
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();
            idPregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar pregunta de única respuesta", e);
        }
        return idPregunta;
    }


    public void guardarRespuestaMultiple(int idPregunta, String textoOpcion1, boolean selected) {
        String sql = "{ call guardar_respuesta_multiple(?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idPregunta);
            cs.setString(2, textoOpcion1);
            cs.setBoolean(3, selected);

            cs.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {  // ORA-00001: unique constraint violated
                LOGGER.log(Level.WARNING, "Respuesta duplicada no insertada (ID_PREGUNTA: " + idPregunta + ", RESPUESTA: " + textoOpcion1 + ")");
            } else {
                LOGGER.log(Level.SEVERE, "Error al guardar respuesta", e);
            }
        }
    }


    //
    // METODOS DE ALMACENAMIENTO DE PREGUNTA/RESPUESTA SELECCION MULTIPLE
    //

    public int crearPreguntaVerdaderoFalso(String enunciado, String estado, int tiempoLimite, int idBanco, int idTema, double porcentaje) {
        int idPregunta = -1;
        String sql = "{ call crear_pregunta_verdadero_falso(?,?,?,?,?,?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciado);
            cs.setString(2, "Verdadero/Falso");
            cs.setDouble(3, porcentaje);
            cs.setString(4, estado);
            cs.setInt(5, tiempoLimite);
            cs.setInt(6, idBanco);
            cs.setInt(7, idTema);
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();
            idPregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar pregunta de única respuesta", e);
        }
        return idPregunta;
    }


    public void guardarRespuestaVerdaderoFalso(int idPregunta, String textoOpcion1, boolean selected) {
        String sql = "{ call guardar_rpta_verdadero(?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idPregunta);
            cs.setString(2, textoOpcion1);
            cs.setBoolean(3, selected);

            cs.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {  // ORA-00001: unique constraint violated
                LOGGER.log(Level.WARNING, "Respuesta duplicada no insertada (ID_PREGUNTA: " + idPregunta + ", RESPUESTA: " + textoOpcion1 + ")");
            } else {
                LOGGER.log(Level.SEVERE, "Error al guardar respuesta", e);
            }
        }
    }

    //
    // METODOS DE ALMACENAMIENTO DE EMPAREJAR
    //

    // Método para crear una pregunta de emparejamiento
    public int crearPreguntaEmparejar(String enunciado, double porcentaje, String tipo, String estado, int tiempoLimite, int idBanco, int idTema) {
        int idPregunta = -1;
        String sql = "{ call CREAR_PREGUNTA_EMPAREJAR(?,?,?,?,?,?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciado);   // p_enunciado
            cs.setDouble(2, porcentaje);  // p_porcentaje
            cs.setString(3, tipo);        // p_tipo  <--- FALTABA
            cs.setString(4, estado);      // p_estado
            cs.setInt(5, tiempoLimite);   // p_tiempo
            cs.setInt(6, idBanco);        // p_idBanco
            cs.setInt(7, idTema);         // p_idTema
            cs.registerOutParameter(8, Types.INTEGER); // p_id_pregunta OUT

            cs.execute();
            idPregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar pregunta de emparejamiento", e);
        }
        return idPregunta;
    }


    // Método para guardar cada par de emparejamiento (A, B)
    public void guardarRespuestaEmparejar(int idPregunta, String textoA, String textoB) {
        String sql = "{ call GUARDAR_RESPUESTA_EMPAREJAR(?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idPregunta);
            cs.setString(2, textoA);
            cs.setString(3, textoB);

            cs.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar par de emparejamiento (ID_PREGUNTA: " + idPregunta + ", A: " + textoA + ", B: " + textoB + ")", e);
        }
    }

    //CREAR PREGUNTA ORDENAR

    // Método para crear una pregunta de ordenamiento
    public int crearPreguntaOrdenar(String enunciado, double porcentaje, String estado, int tiempoLimite, String tipo,  int idBanco, int idTema) {
        int idPregunta = -1;
        String sql = "{ call CREAR_PREGUNTA_ORDENAR(?,?,?,?,?,?,?,?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciado);   // p_enunciado
            cs.setDouble(2, porcentaje);  // p_porcentaje
            cs.setString(3, tipo);        // p_tipo  <--- FALTABA
            cs.setString(4, estado);      // p_estado
            cs.setInt(5, tiempoLimite);   // p_tiempo
            cs.setInt(6, idBanco);        // p_idBanco
            cs.setInt(7, idTema);         // p_idTema
            cs.registerOutParameter(8, Types.INTEGER); // p_id_pregunta OUT

            cs.execute();
            idPregunta = cs.getInt(8);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar pregunta de emparejamiento", e);
        }
        return idPregunta;
    }

    public void guardarRespuestaOrdenar(int idPregunta, String respuestaConcatenada) {
        String sql = "{ call GUARDAR_RESPUESTA_ORDENADA(?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idPregunta);
            cs.setString(2, respuestaConcatenada);
            cs.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar respuesta ordenada", e);
        }
    }

    public List<Pregunta> obtenerPreguntasPorTipoYTema(String tipo, String idTema) {
        List<Pregunta> preguntas = new ArrayList<>();
        int idTemas = obtenerIdTema(idTema);
        String sql = "{ call OBTENER_PREGUNTAS_TIPO_TEMA(?, ?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, tipo);
            cs.setInt(2, idTemas);
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    Pregunta pregunta = new Pregunta();
                    pregunta.setId(rs.getInt("ID_PREGUNTA"));
                    pregunta.setTexto(rs.getString("TEXTO"));
                    pregunta.setTipo(rs.getString("TIPO"));
                    pregunta.setPorcentaje(rs.getBigDecimal("PORCENTAJE"));
                    pregunta.setTiempoLimite(rs.getInt("TIEMPO_LIMITE"));
                    pregunta.setIdBanco(rs.getInt("ID_BANCO"));
                    pregunta.setIdTema(rs.getInt("ID_TEMA"));
                    pregunta.setEstado(rs.getString("ESTADO"));
                    preguntas.add(pregunta);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener preguntas por tipo y tema", e);
        }

        return preguntas;
    }

    public Pregunta obtenerPreguntaPorEnunciado(String enunciadoSeleccionado) {
        String sql = "{ call OBTENER_PREGUNTA_POR_ENUNCIADO(?, ?) }";
        Pregunta pregunta = null;

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, enunciadoSeleccionado);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    pregunta = new Pregunta();
                    pregunta.setId(rs.getInt("ID_PREGUNTA"));
                    pregunta.setTexto(rs.getString("TEXTO"));
                    pregunta.setTipo(rs.getString("TIPO"));
                    pregunta.setPorcentaje(rs.getBigDecimal("PORCENTAJE"));
                    pregunta.setTiempoLimite(rs.getInt("TIEMPO_LIMITE"));
                    pregunta.setIdBanco(rs.getInt("ID_BANCO"));
                    pregunta.setIdTema(rs.getInt("ID_TEMA"));
                    pregunta.setEstado(rs.getString("ESTADO"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener pregunta por enunciado", e);
        }

        return pregunta;
    }

    public int crearExamen(String titulo, String descripcion, String categoria, int idTema, int idDocente, int preguntasEstudiante) {
        String sql = "{ call CREAR_EXAMEN(?, ?, ?, ?, ?, ?, ?) }";
        int idGenerado = -1;

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, titulo);
            cs.setString(2, descripcion);
            cs.setString(3, categoria);
            cs.setInt(4, idTema);
            cs.setInt(5, idDocente);
            cs.setInt(6, preguntasEstudiante);

            cs.registerOutParameter(7, Types.INTEGER);

            cs.execute();
            idGenerado = cs.getInt(7);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear examen", e);
        }

        return idGenerado;
    }

    public List<Pregunta> obtenerPreguntasPorCategoria(String categoria) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "{ call OBTENER_PREGUNTAS_POR_TIPO(?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, categoria);
            cs.registerOutParameter(2, OracleTypes.CURSOR);

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Pregunta pregunta = new Pregunta();
                    pregunta.setId(rs.getInt("ID_PREGUNTA"));
                    pregunta.setTexto(rs.getString("TEXTO"));
                    pregunta.setTipo(rs.getString("TIPO"));
                    pregunta.setPorcentaje(rs.getBigDecimal("PORCENTAJE"));
                    pregunta.setTiempoLimite(rs.getInt("TIEMPO_LIMITE"));
                    pregunta.setIdBanco(rs.getInt("ID_BANCO"));
                    pregunta.setIdTema(rs.getInt("ID_TEMA"));
                    pregunta.setEstado(rs.getString("ESTADO"));
                    preguntas.add(pregunta);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener preguntas por categoría", e);
        }

        return preguntas;
    }

    public void crearPreguntaExamen(int idExamen, int id, BigDecimal porcentaje) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "{ call CREAR_PREGUNTA_EXAMEN(?, ?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idExamen);
            cs.setInt(2, id);
            cs.setBigDecimal(3, porcentaje);
            cs.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener preguntas por categoría", e);
        }
    }

    public void guardarProgramacionExamen(int idExamen, LocalDate fecha, LocalTime hora,
                                          int duracionMinutos, int idCurso, BigDecimal umbralAprobacion) {
        String sql = "{ call GUARDAR_PROGRAMACION_EXAMEN(?, ?, ?, ?, ?, ?) }";
        try (Connection connection = getConnection();  // o tu forma de obtener conexión
             CallableStatement cs = connection.prepareCall(sql)) {

            cs.setInt(1, idExamen);
            cs.setDate(2, java.sql.Date.valueOf(fecha));
            cs.setString(3, hora.toString());
            cs.setInt(4, duracionMinutos);
            cs.setInt(5, idCurso);
            cs.setBigDecimal(6, umbralAprobacion);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la programación del examen", e);
        }
    }

    public List<Pregunta> obtenerPreguntasPorExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "{ call obtener_preguntas_por_examen(?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idExamen);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Pregunta pregunta = new Pregunta();
                    pregunta.setId(rs.getInt("ID_PREGUNTA"));
                    pregunta.setTexto(rs.getString("TEXTO"));
                    pregunta.setTipo(rs.getString("TIPO"));
                    pregunta.setPorcentaje(BigDecimal.valueOf(rs.getDouble("PORCENTAJE")));
                    pregunta.setTiempoLimite(rs.getInt("TIEMPO_LIMITE"));
                    preguntas.add(pregunta);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener preguntas del examen", e);
        }

        return preguntas;
    }


    public List<Examen> obtenerExamenes() {
        List<Examen> examenes = new ArrayList<>();
        String sql = "{ call obtener_examenes_docente(?, ?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idUsuario); // Asegúrate de tener este valor correctamente
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Examen examen = new Examen();
                    examen.setIdExamen(rs.getInt("ID_EXAMEN"));
                    examen.setTitulo(rs.getString("TITULO"));
                    examen.setTiempoTotal(rs.getInt("TIEMPO_TOTAL"));
                    examen.setIdCurso(rs.getInt("ID_CURSO"));
                    examen.setUmbralAprobacion(rs.getBigDecimal("UMBRAL_APROBACION"));

                    examenes.add(examen);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener exámenes del docente", e);
        }

        return examenes;
    }

    public List<Curso> obtenerCursos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "{ call obtener_cursos(?) }";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Curso curso = new Curso();
                    curso.setId(rs.getInt("ID_CURSO"));
                    curso.setNombre(rs.getString("NOMBRE"));
                    curso.setDescripcion(rs.getString("DESCRIPCION"));
                    cursos.add(curso);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener exámenes del docente", e);
        }
        return cursos;
    }
}
