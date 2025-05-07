package com.example.bd_edu;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.lang.reflect.Type;
import java.sql.*;
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

    private static Bd_Edu bd_edu;

    public static Bd_Edu getInstance() {
        if (bd_edu == null) {
            bd_edu = new Bd_Edu();
        }
        return bd_edu;
    }

    private Bd_Edu() {
        try {
            String url = "jdbc:oracle:thin:@//localhost:1521/xe";
            String user = "SYSTEM";
            String password = "0000";

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a Oracle.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a Oracle", e);
        }
    }

    public boolean iniciarSesion(String usuario, String clave) {
        try (CallableStatement cs = connection.prepareCall("{ call validar_usuario_con_rol(?, ?, ?, ?) }")) {

            cs.setString(1, usuario);
            cs.setString(2, clave);

            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);

            cs.execute();

            int resultado = cs.getInt(3);
            String rol = cs.getString(4);

            if (resultado > 0 && rol != null && !rol.isEmpty()) {
                this.rolUsuario = rol;
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
     * @param id_pregunta
     * @param respuesta
     * @param esCorrecta
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
     * @param pregunta
     * @param tipo
     * @param porcentaje
     * @param respuesta
     * @param estado
     * @param tiempo
     * @param id_banco
     * @param id_tema
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
     * @param url
     * @param event
     */
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
}
