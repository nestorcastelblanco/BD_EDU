package com.example.bd_edu.servicios;

import com.example.bd_edu.modelo.profesores.*;
import com.example.bd_edu.repositorios.RepositorioProfesor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioProfesor {

    private final RepositorioProfesor repositorio;

    public ServicioProfesor(RepositorioProfesor repositorio) {
        this.repositorio = repositorio;
    }

    public Map<String, Object> crearEvaluacion(CrearEvaluacion datos) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            Long idEvaluacion = repositorio.crearEvaluacion(datos);
            resultado.put("idEvaluacion", idEvaluacion);
            resultado.put("mensaje", "Evaluación creada con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> crearPregunta(CrearPregunta datos) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            Long idPregunta = repositorio.crearPregunta(datos);
            resultado.put("idPregunta", idPregunta);
            resultado.put("mensaje", "Pregunta creada con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> crearOpcionRespuesta(CrearOpcionRespuesta datos) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            Long idRespuesta = repositorio.crearOpcionRespuesta(datos);
            resultado.put("idRespuesta", idRespuesta);
            resultado.put("mensaje", "Opción de respuesta creada con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerMaterias() {
        Map<String, Object> resultado = new HashMap<>();
        try {
            List<Map<String, Object>> materias = repositorio.obtenerMaterias();
            resultado.put("datos", materias);
            resultado.put("mensaje", "Materias obtenidas con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerTemas() {
        Map<String, Object> resultado = new HashMap<>();
        try {
            List<Map<String, Object>> temas = repositorio.obtenerTemas();
            resultado.put("datos", temas);
            resultado.put("mensaje", "Temas obtenidos con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerGruposPorMateria(Long idMateria) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            List<Map<String, Object>> grupos = repositorio.obtenerGruposPorMateria(idMateria);
            resultado.put("datos", grupos);
            resultado.put("mensaje", "Grupos obtenidos con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerPreguntasPorTema(Long idTema) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            List<Map<String, Object>> preguntas = repositorio.obtenerPreguntasPorTema(idTema);
            resultado.put("datos", preguntas);
            resultado.put("mensaje", "Preguntas obtenidas con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> asignarPreguntaAEvaluacion(AsignarPreguntaAEvaluacion datos) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            repositorio.asignarPreguntaAEvaluacion(datos);
            resultado.put("mensaje", "Pregunta asignada con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> asignarPreguntasAleatorias(Long idEvaluacion) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            repositorio.asignarPreguntasAleatorias(idEvaluacion);
            resultado.put("mensaje", "Preguntas aleatorias asignadas con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerPreguntasDeEvaluacion(Long idEvaluacion) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            List<Map<String, Object>> preguntas = repositorio.obtenerPreguntasDeEvaluacion(idEvaluacion);
            resultado.put("datos", preguntas);
            resultado.put("mensaje", "Preguntas obtenidas con éxito");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }

    public Map<String, Object> obtenerInfoEvaluacion(Long idEvaluacion) {
        Map<String, Object> resultado = new HashMap<>();
        try {
            Map<String, Object> evaluacion = repositorio.obtenerInfoEvaluacion(idEvaluacion);
            resultado.put("datos", evaluacion);
            resultado.put("mensaje", "Información de evaluación obtenida");
            resultado.put("estado", "EXITO");
        } catch (SQLException e) {
            resultado.put("mensaje", e.getMessage());
            resultado.put("estado", "ERROR");
        }
        return resultado;
    }
}
