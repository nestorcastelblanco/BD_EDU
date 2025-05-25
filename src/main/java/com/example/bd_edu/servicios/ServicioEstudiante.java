package com.example.bd_edu.servicios;

import com.example.bd_edu.modelo.estudiantes.CrearPresentacionEvaluacion;
import com.example.bd_edu.modelo.estudiantes.ObtenerEvaluacionesDisponibles;
import com.example.bd_edu.modelo.estudiantes.ResponderPregunta;
import com.example.bd_edu.repositorios.RepositorioEstudiante;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioEstudiante {

    private final RepositorioEstudiante repositorioEstudiante;

    public ServicioEstudiante(RepositorioEstudiante repositorioEstudiante) {
        this.repositorioEstudiante = repositorioEstudiante;
    }

    public List<Map<String, Object>> obtenerGruposEstudiante(Long idEstudiante) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.obtenerGruposEstudiante(idEstudiante);

            String estado = (String) resultado.get("p_estado_out");
            String mensaje = (String) resultado.get("p_mensaje_out");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> grupos = (List<Map<String, Object>>) resultado.get("p_resultado_cursor");

            if (!"EXITO".equals(estado)) {
                throw new Exception(mensaje != null ? mensaje : "Error al obtener grupos del estudiante");
            }

            if (grupos == null || grupos.isEmpty()) {
                throw new Exception("El estudiante no tiene grupos asignados");
            }

            return grupos;
        } catch (ClassCastException e) {
            throw new Exception("Error en el formato de los datos recibidos", e);
        } catch (SQLException e) {
            throw new Exception("Error de base de datos: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> obtenerEvaluacionesDisponibles(ObtenerEvaluacionesDisponibles datos) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.obtenerEvaluacionesDisponibles(datos);
            String estado = (String) resultado.get("p_estado_out");
            String mensaje = (String) resultado.get("p_mensaje_out");

            if (!"EXITO".equals(estado)) {
                throw new Exception(mensaje != null ? mensaje : "Error al obtener evaluaciones disponibles");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> evaluaciones = (List<Map<String, Object>>) resultado.get("p_resultado_cursor");

            if (evaluaciones == null) {
                throw new Exception("No se encontraron evaluaciones disponibles");
            }

            return evaluaciones;
        } catch (SQLException e) {
            throw new Exception("Error de base de datos al obtener evaluaciones: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> crearPresentacionEvaluacion(CrearPresentacionEvaluacion datos) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.crearPresentacionEvaluacion(datos);
            return procesarResultadoCreacionPresentacion(resultado);
        } catch (SQLException e) {
            throw new Exception("Error al crear la presentación de evaluación: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> obtenerPreguntasEvaluacion(Long idEvaluacion) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.obtenerPreguntasEvaluacion(idEvaluacion);
            String estado = (String) resultado.get("p_estado_out");
            String mensaje = (String) resultado.get("p_mensaje_out");

            if (!"EXITO".equals(estado)) {
                throw new Exception(mensaje != null ? mensaje : "Error al obtener las preguntas de la evaluación");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> preguntas = (List<Map<String, Object>>) resultado.get("p_resultado_cursor");

            if (preguntas == null || preguntas.isEmpty()) {
                throw new Exception("La evaluación no tiene preguntas asociadas");
            }

            return preguntas;
        } catch (SQLException e) {
            throw new Exception("Error de base de datos al obtener preguntas: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> obtenerOpcionesPregunta(Long idPregunta) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.obtenerOpcionesPregunta(idPregunta);
            return procesarResultadoLista(resultado, "obtener las opciones de la pregunta");
        } catch (SQLException e) {
            throw new Exception("Error al obtener las opciones de la pregunta: " + e.getMessage(), e);
        }
    }

    public void responderPregunta(ResponderPregunta datos) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.responderPregunta(datos);
            procesarResultadoGenerico(resultado, "registrar la respuesta del estudiante");
        } catch (SQLException e) {
            throw new Exception("Error al registrar la respuesta del estudiante: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> finalizarEvaluacion(Long idPresentacion) throws Exception {
        try {
            Map<String, Object> resultado = repositorioEstudiante.finalizarEvaluacion(idPresentacion);
            return procesarResultadoFinalizacionEvaluacion(resultado);
        } catch (SQLException e) {
            throw new Exception("Error al finalizar la evaluación: " + e.getMessage(), e);
        }
    }

    // ========== MÉTODOS PRIVADOS DE PROCESAMIENTO ==========

    private List<Map<String, Object>> procesarResultadoLista(Map<String, Object> resultado, String operacion) throws Exception {
        String estado = (String) resultado.get("p_estado_out");
        if ("EXITO".equalsIgnoreCase(estado)) {
            return (List<Map<String, Object>>) resultado.get("p_resultado_cursor");
        } else {
            throw new Exception("Error al " + operacion + ": " + resultado.get("p_mensaje_out"));
        }
    }

    private Map<String, Object> procesarResultadoCreacionPresentacion(Map<String, Object> resultado) throws Exception {
        String estado = (String) resultado.get("p_estado_out");
        String mensaje = (String) resultado.get("p_mensaje_out");
        Long idPresentacion = resultado.get("p_id_presentacion_out") != null ?
                ((Number) resultado.get("p_id_presentacion_out")).longValue() : null;

        System.out.println("Procesando resultado de creación de presentación:");
        System.out.println("Estado recibido: " + estado);
        System.out.println("Mensaje recibido: " + mensaje);
        System.out.println("ID Presentación recibido: " + idPresentacion);

        // Manejar casos especiales según el PL/SQL
        if ("EXITO".equalsIgnoreCase(estado) && mensaje != null && mensaje.contains("Ya existe una presentación no finalizada")) {
            if (idPresentacion == null || idPresentacion <= 0) {
                throw new Exception("Presentación existente pero ID inválido");
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id_presentacion", idPresentacion);
            return respuesta;
        }

        if (!"EXITO".equalsIgnoreCase(estado)) {
            throw new Exception(mensaje != null ? mensaje : "Error al crear la presentación de evaluación");
        }

        if (idPresentacion == null || idPresentacion <= 0) {
            throw new Exception("El ID de presentación es inválido");
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id_presentacion", idPresentacion);
        return respuesta;
    }

    private void procesarResultadoGenerico(Map<String, Object> resultado, String operacion) throws Exception {
        String estado = (String) resultado.get("p_estado_out");
        String mensaje = (String) resultado.get("p_mensaje_out");

        if (estado == null) {
            throw new Exception("El resultado no contiene un estado válido");
        }

        if (!"EXITO".equalsIgnoreCase(estado)) {
            throw new Exception("Error al " + operacion + ": " + (mensaje != null ? mensaje : "Error desconocido"));
        }
    }

    private Map<String, Object> procesarResultadoFinalizacionEvaluacion(Map<String, Object> resultado) throws Exception {
        String estado = (String) resultado.get("p_estado_out");
        String mensaje = (String) resultado.get("p_mensaje_out");
        Float calificacion = resultado.get("p_calificacion_out") != null ?
                ((Number) resultado.get("p_calificacion_out")).floatValue() : null;

        System.out.println("Resultado de finalización:");
        System.out.println("Estado: " + estado);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("Calificación: " + calificacion);

        if (!"EXITO".equalsIgnoreCase(estado)) {
            throw new Exception("No se pudo finalizar la presentación: " +
                    (mensaje != null ? mensaje : "Error desconocido"));
        }

        if (calificacion == null) {
            throw new Exception("No se pudo calcular la calificación");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("calificacion", calificacion);
        return data;
    }
}
