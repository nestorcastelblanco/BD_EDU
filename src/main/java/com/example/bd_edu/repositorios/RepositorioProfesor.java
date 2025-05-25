package com.example.bd_edu.repositorios;

import com.example.bd_edu.config.ConexionBD;
import com.example.bd_edu.modelo.profesores.*;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.*;

public class RepositorioProfesor {

    private final Connection conexion;

    public RepositorioProfesor(Connection conexion) throws SQLException {
        if (!ConexionBD.validarConexion(conexion)) {
            throw new SQLException("La conexión proporcionada no es válida");
        }
        this.conexion = conexion;
    }

    public Long crearEvaluacion(CrearEvaluacion datos) throws SQLException {
        String sql = "BEGIN CREAR_EVALUACION(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, datos.getTiempoMaximo());
            stmt.setInt(2, datos.getCantidadPreguntas());
            stmt.setDouble(3, datos.getPorcentajeCurso());
            stmt.setString(4, datos.getNombre());
            stmt.setDouble(5, datos.getPorcentajeAprobacion());
            stmt.setTimestamp(6, Timestamp.valueOf(datos.getFechaInicio()));
            stmt.setTimestamp(7, Timestamp.valueOf(datos.getFechaFin()));
            stmt.setInt(8, datos.getPreguntasAleatorias());
            stmt.setLong(9, datos.getIdTema());
            stmt.setLong(10, datos.getIdProfesor());
            stmt.setLong(11, datos.getIdGrupo());
            stmt.registerOutParameter(12, Types.NUMERIC); // id evaluación
            stmt.registerOutParameter(13, Types.VARCHAR); // estado
            stmt.registerOutParameter(14, Types.VARCHAR); // mensaje error

            stmt.execute();

            if (!"EXITO".equalsIgnoreCase(stmt.getString(13))) {
                throw new SQLException("Error al crear evaluación: " + stmt.getString(14));
            }

            return stmt.getLong(12);
        }
    }

    public Long crearPregunta(CrearPregunta datos) throws SQLException {
        String sql = "BEGIN CREAR_PREGUNTA(?, ?, ?, ?, ?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setString(1, datos.getEnunciado());
            stmt.setString(2, datos.getEsPublica());
            stmt.setString(3, datos.getTipoPregunta());
            stmt.setObject(4, datos.getIdPreguntaCompuesta() != null ? datos.getIdPreguntaCompuesta() : null, Types.NUMERIC);
            stmt.setLong(5, datos.getIdTema());
            stmt.setLong(6, datos.getIdProfesor());
            stmt.registerOutParameter(7, Types.NUMERIC); // id pregunta
            stmt.registerOutParameter(8, Types.VARCHAR); // estado
            stmt.registerOutParameter(9, Types.VARCHAR); // mensaje error

            stmt.execute();

            if (!"EXITO".equalsIgnoreCase(stmt.getString(8))) {
                throw new SQLException("Error al crear pregunta: " + stmt.getString(9));
            }

            return stmt.getLong(7);
        }
    }

    public Long crearOpcionRespuesta(CrearOpcionRespuesta datos) throws SQLException {
        String sql = "BEGIN CREAR_OPCION_RESPUESTA(?, ?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setString(1, datos.getDescripcion());
            stmt.setString(2, datos.getEsCorrecta());
            stmt.setLong(3, datos.getIdPregunta());
            stmt.registerOutParameter(4, Types.NUMERIC); // id respuesta
            stmt.registerOutParameter(5, Types.VARCHAR); // estado
            stmt.registerOutParameter(6, Types.VARCHAR); // mensaje error

            stmt.execute();

            if (!"EXITO".equalsIgnoreCase(stmt.getString(5))) {
                throw new SQLException("Error al crear opción de respuesta: " + stmt.getString(6));
            }

            return stmt.getLong(4);
        }
    }

    public List<Map<String, Object>> obtenerMaterias() throws SQLException {
        List<Map<String, Object>> materias = new ArrayList<>();
        String sql = "BEGIN OBTENER_MATERIAS(?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                Map<String, Object> materia = new HashMap<>();
                materia.put("id_materia", rs.getLong("id_materia"));
                materia.put("nombre", rs.getString("nombre"));
                materias.add(materia);
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(2))) {
                throw new SQLException("Error al obtener materias: " + stmt.getString(3));
            }

            return materias;
        }
    }

    public List<Map<String, Object>> obtenerTemas() throws SQLException {
        List<Map<String, Object>> temas = new ArrayList<>();
        String sql = "BEGIN OBTENER_TEMAS(?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                Map<String, Object> tema = new HashMap<>();
                tema.put("id_tema", rs.getLong("id_tema"));
                tema.put("nombre", rs.getString("nombre"));
                temas.add(tema);
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(2))) {
                throw new SQLException("Error al obtener temas: " + stmt.getString(3));
            }

            return temas;
        }
    }

    public List<Map<String, Object>> obtenerGruposPorMateria(Long idMateria) throws SQLException {
        List<Map<String, Object>> grupos = new ArrayList<>();
        String sql = "BEGIN OBTENER_GRUPOS_POR_MATERIA(?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idMateria);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                Map<String, Object> grupo = new HashMap<>();
                grupo.put("id_grupo", rs.getLong("id_grupo"));
                grupo.put("nombre", rs.getString("nombre"));
                grupos.add(grupo);
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(3))) {
                throw new SQLException("Error al obtener grupos: " + stmt.getString(4));
            }

            return grupos;
        }
    }

    public List<Map<String, Object>> obtenerPreguntasPorTema(Long idTema) throws SQLException {
        List<Map<String, Object>> preguntas = new ArrayList<>();
        String sql = "BEGIN OBTENER_PREGUNTAS_TEMA(?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idTema);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                Map<String, Object> pregunta = new HashMap<>();
                pregunta.put("id_pregunta", rs.getLong("id_pregunta"));
                pregunta.put("enunciado", rs.getString("enunciado"));
                preguntas.add(pregunta);
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(3))) {
                throw new SQLException("Error al obtener preguntas: " + stmt.getString(4));
            }

            return preguntas;
        }
    }

    public void asignarPreguntasAleatorias(Long idEvaluacion) throws SQLException {
        String sql = "BEGIN ASIGNAR_PREGUNTAS_ALEATORIAS(?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idEvaluacion);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();

            if (!"EXITO".equalsIgnoreCase(stmt.getString(2))) {
                throw new SQLException("Error al asignar preguntas aleatorias: " + stmt.getString(3));
            }
        }
    }

    public void asignarPreguntaAEvaluacion(AsignarPreguntaAEvaluacion datos) throws SQLException {
        String sql = "BEGIN CREAR_PREGUNTA_EVALUACION(?, ?, ?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, datos.getIdPregunta());
            stmt.setLong(2, datos.getIdEvaluacion());
            stmt.setDouble(3, datos.getPorcentaje());
            stmt.setString(4, datos.getTieneTiempoMaximo());
            stmt.setInt(5, datos.getTiempoPorPregunta());
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.registerOutParameter(7, Types.VARCHAR);

            stmt.execute();

            if (!"EXITO".equalsIgnoreCase(stmt.getString(6))) {
                throw new SQLException("Error al asignar pregunta: " + stmt.getString(7));
            }
        }
    }

    public List<Map<String, Object>> obtenerPreguntasDeEvaluacion(Long idEvaluacion) throws SQLException {
        List<Map<String, Object>> preguntas = new ArrayList<>();
        String sql = "BEGIN OBTENER_PREGUNTAS_EVALUACION(?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idEvaluacion);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                Map<String, Object> pregunta = new HashMap<>();
                pregunta.put("id_pregunta", rs.getLong("id_pregunta"));
                pregunta.put("enunciado", rs.getString("enunciado"));
                pregunta.put("porcentaje", rs.getDouble("porcentaje"));
                pregunta.put("tiempo_por_pregunta", rs.getInt("tiempo_por_pregunta"));
                preguntas.add(pregunta);
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(3))) {
                throw new SQLException("Error al obtener preguntas: " + stmt.getString(4));
            }

            return preguntas;
        }
    }

    public Map<String, Object> obtenerInfoEvaluacion(Long idEvaluacion) throws SQLException {
        Map<String, Object> evaluacion = new HashMap<>();
        String sql = "BEGIN OBTENER_INFO_EVALUACION(?, ?, ?, ?, ?); END;";
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setLong(1, idEvaluacion);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.NUMERIC); // cantidad_preguntas

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);
            if (rs.next()) {
                evaluacion.put("id_evaluacion", rs.getLong("id_evaluacion"));
                evaluacion.put("nombre", rs.getString("nombre"));
                evaluacion.put("preguntas_aleatorias", rs.getInt("preguntas_aleatorias"));
                evaluacion.put("cantidad_preguntas", stmt.getInt(5)); // Obtener el parámetro de salida
            }

            if (!"EXITO".equalsIgnoreCase(stmt.getString(3))) {
                throw new SQLException("Error al obtener evaluación: " + stmt.getString(4));
            }

            return evaluacion;
        }
    }
}
