package com.example.bd_edu.repositorios;

import com.example.bd_edu.config.ConexionBD;
import com.example.bd_edu.modelo.estudiantes.CrearPresentacionEvaluacion;
import com.example.bd_edu.modelo.estudiantes.ObtenerEvaluacionesDisponibles;
import com.example.bd_edu.modelo.estudiantes.ResponderPregunta;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.*;

public class RepositorioEstudiante {

    private final Connection conexion;

    public RepositorioEstudiante(Connection conexion) throws SQLException {
        if (!ConexionBD.validarConexion(conexion)) {
            throw new SQLException("La conexión proporcionada no es válida");
        }
        this.conexion = conexion;
    }

    public Map<String, Object> obtenerGruposEstudiante(Long idEstudiante) {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> grupos = new ArrayList<>();

        String sql = "{call OBTENER_GRUPOS_ESTUDIANTE(?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idEstudiante);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            String estado = stmt.getString(3);
            String mensaje = stmt.getString(4);

            System.out.println("Estado: " + estado);
            System.out.println("Mensaje: " + mensaje);

            if ("EXITO".equalsIgnoreCase(estado)) {
                try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    System.out.println("Columnas disponibles en grupos:");
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(i + ": " + metaData.getColumnName(i) +
                                " (Tipo: " + metaData.getColumnTypeName(i) + ")");
                    }

                    while (rs.next()) {
                        Map<String, Object> grupo = new HashMap<>();
                        grupo.put("ID_GRUPO", rs.getLong("ID_GRUPO"));
                        grupo.put("NOMBRE_GRUPO", rs.getString("NOMBRE_GRUPO"));
                        grupo.put("NOMBRE_MATERIA", rs.getString("NOMBRE_MATERIA"));

                        System.out.println("Grupo obtenido: " + grupo);
                        grupos.add(grupo);
                    }
                }
            }

            resultado.put("p_resultado_cursor", grupos);
            resultado.put("p_estado_out", estado);
            resultado.put("p_mensaje_out", mensaje);

        } catch (SQLException e) {
            resultado.put("p_resultado_cursor", null);
            resultado.put("p_estado_out", "ERROR");
            resultado.put("p_mensaje_out", "Error en base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    public Map<String, Object> obtenerEvaluacionesDisponibles(ObtenerEvaluacionesDisponibles datos) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> evaluaciones = new ArrayList<>();

        String sql = "{call OBTENER_EVALUACIONES_DISP(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, datos.getIdEstudiante());
            stmt.setLong(2, datos.getIdGrupo());
            stmt.setString(3, datos.getFechaActual());
            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.VARCHAR);

            stmt.execute();

            String estado = stmt.getString(5);
            String mensaje = stmt.getString(6);

            if ("EXITO".equalsIgnoreCase(estado)) {
                try (ResultSet rs = (ResultSet) stmt.getObject(4)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Log para diagnóstico
                    System.out.println("Columnas disponibles en el cursor:");
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(i + ": " + metaData.getColumnName(i) +
                                " (Tipo: " + metaData.getColumnTypeName(i) + ")");
                    }

                    while (rs.next()) {
                        Map<String, Object> eval = new HashMap<>();
                        // Mapear todas las columnas disponibles
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            eval.put(columnName, rs.getObject(i));
                        }
                        evaluaciones.add(eval);

                        // Log para ver los datos recibidos
                        System.out.println("Evaluación obtenida: " + eval);
                    }
                }
            }

            resultado.put("p_resultado_cursor", evaluaciones);
            resultado.put("p_estado_out", estado);
            resultado.put("p_mensaje_out", mensaje);

        } catch (SQLException e) {
            resultado.put("p_resultado_cursor", null);
            resultado.put("p_estado_out", "ERROR");
            resultado.put("p_mensaje_out", "Error al obtener evaluaciones: " + e.getMessage());
            throw e;
        }

        return resultado;
    }

    public Map<String, Object> crearPresentacionEvaluacion(CrearPresentacionEvaluacion datos) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "{call CREAR_PRESENTACION_EVALUACION(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            // Parámetros de entrada
            stmt.setLong(1, datos.getIdEvaluacion());
            stmt.setLong(2, datos.getIdEstudiante());
            stmt.setString(3, datos.getIpOrigen());

            // Parámetros de salida
            stmt.registerOutParameter(4, Types.NUMERIC); // p_id_presentacion_out
            stmt.registerOutParameter(5, Types.VARCHAR); // p_estado_out
            stmt.registerOutParameter(6, Types.VARCHAR); // p_mensaje_out

            stmt.execute();

            // Obtener resultados
            Long idPresentacion = null;
            try {
                idPresentacion = stmt.getLong(4);
            } catch (SQLException e) {
                System.err.println("Error al obtener id_presentacion: " + e.getMessage());
            }

            String estado = stmt.getString(5);
            String mensaje = stmt.getString(6);

            // Log detallado para diagnóstico
            System.out.println("Resultado de CREAR_PRESENTACION_EVALUACION:");
            System.out.println("ID Presentación: " + idPresentacion);
            System.out.println("Estado: " + estado);
            System.out.println("Mensaje: " + mensaje);

            // Validar resultados según las reglas del PL/SQL
            if (estado == null) {
                estado = "ERROR";
                mensaje = "El procedimiento no devolvió un estado válido";
            }

            if ("EXITO".equalsIgnoreCase(estado) && (idPresentacion == null || idPresentacion <= 0)) {
                estado = "ERROR";
                mensaje = "Estado EXITO pero ID de presentación inválido";
            }

            resultado.put("p_id_presentacion_out", idPresentacion);
            resultado.put("p_estado_out", estado);
            resultado.put("p_mensaje_out", mensaje);

        } catch (SQLException e) {
            System.err.println("Error en CREAR_PRESENTACION_EVALUACION: " + e.getMessage());
            resultado.put("p_estado_out", "ERROR");
            resultado.put("p_mensaje_out", "Error de base de datos: " + e.getMessage());
            throw e;
        }

        return resultado;
    }

    public Map<String, Object> obtenerPreguntasEvaluacion(Long idEvaluacion) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> preguntas = new ArrayList<>();

        String sql = "{call OBTENER_PREGUNTAS_EVALUACION(?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idEvaluacion);
            stmt.registerOutParameter(2, Types.VARCHAR); // p_estado_out
            stmt.registerOutParameter(3, Types.VARCHAR); // p_mensaje_out
            stmt.registerOutParameter(4, OracleTypes.CURSOR); // p_cursor_out

            stmt.execute();

            String estado = stmt.getString(2);
            String mensaje = stmt.getString(3);

            if ("EXITO".equalsIgnoreCase(estado)) {
                try (ResultSet rs = (ResultSet) stmt.getObject(4)) {
                    while (rs.next()) {
                        Map<String, Object> pregunta = new HashMap<>();
                        pregunta.put("id_pregunta", rs.getLong("ID_PREGUNTA"));
                        pregunta.put("texto", rs.getString("ENUNCIADO"));
                        pregunta.put("tipo", rs.getString("TIPO_PREGUNTA"));
                        preguntas.add(pregunta);
                    }
                }
            }

            resultado.put("p_estado_out", estado);
            resultado.put("p_mensaje_out", mensaje);
            resultado.put("p_resultado_cursor", preguntas);
        }

        return resultado;
    }

    public Map<String, Object> obtenerOpcionesPregunta(Long idPregunta) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> opciones = new ArrayList<>();

        String sql = "{call OBTENER_OPCIONES_PREGUNTA(?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idPregunta);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            String estado = stmt.getString(3);
            String mensaje = stmt.getString(4);

            if ("EXITO".equalsIgnoreCase(estado)) {
                try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                    while (rs.next()) {
                        Map<String, Object> opcion = new HashMap<>();
                        opcion.put("id_opcion", rs.getLong("ID_RESPUESTA"));
                        opcion.put("texto", rs.getString("DESCRIPCION"));
                        opcion.put("es_correcta", rs.getString("ES_CORRECTA"));
                        opciones.add(opcion);
                    }
                }
            }

            resultado.put("p_estado_out", estado);
            resultado.put("p_mensaje_out", mensaje);
            resultado.put("p_resultado_cursor", opciones);
        }

        return resultado;
    }

    public Map<String, Object> responderPregunta(ResponderPregunta datos) {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "BEGIN REGISTRAR_RESPUESTA_ESTUDIANTE(?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, datos.getIdPresentacionEvaluacion());
            stmt.setLong(2, datos.getIdPregunta());
            stmt.setLong(3, datos.getIdRespuesta());
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.execute();

            resultado.put("p_estado_out", stmt.getString(4));  // Cambiado de p_resultado
            resultado.put("p_mensaje_out", stmt.getString(5)); // Cambiado de p_mensaje
        } catch (SQLException e) {
            resultado.put("p_estado_out", "ERROR");
            resultado.put("p_mensaje_out", "Error al registrar respuesta: " + e.getMessage());
        }
        return resultado;
    }

    public Map<String, Object> finalizarEvaluacion(Long idPresentacion) {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "BEGIN FINALIZAR_PRESENTACION_EXAMEN(?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idPresentacion);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.FLOAT);
            stmt.execute();

            // Cambiar los nombres de las claves para que coincidan con lo que espera el servicio
            resultado.put("p_estado_out", stmt.getString(2));
            resultado.put("p_mensaje_out", stmt.getString(3));
            resultado.put("p_calificacion_out", stmt.getFloat(4));
        } catch (SQLException e) {
            resultado.put("p_estado_out", "ERROR");
            resultado.put("p_mensaje_out", "Error al finalizar evaluación: " + e.getMessage());
        }
        return resultado;
    }
}
