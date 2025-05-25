package com.example.bd_edu.servicios;

import com.example.bd_edu.modelo.autenticacion.InicioSesionEstudiante;
import com.example.bd_edu.modelo.autenticacion.InicioSesionProfesor;
import com.example.bd_edu.modelo.autenticacion.RespuestaInicioSesion;
import com.example.bd_edu.repositorios.RepositorioAutenticacion;

import java.sql.SQLException;
import java.util.Map;

public class ServicioAutenticacion {

    private final RepositorioAutenticacion repositorioAutenticacion;

    public ServicioAutenticacion(RepositorioAutenticacion repositorioAutenticacion) {
        this.repositorioAutenticacion = repositorioAutenticacion;
    }

    public RespuestaInicioSesion iniciarSesionProfesor(InicioSesionProfesor datos) throws Exception {
        try {
            Map<String, Object> resultado = repositorioAutenticacion.autenticarProfesor(
                    datos.getCorreoElectronico(),
                    datos.getContrasena()
            );

            return procesarResultadoProfesor(resultado);
        } catch (SQLException e) {
            throw new Exception("Error al autenticar al profesor: " + e.getMessage(), e);
        }
    }

    public RespuestaInicioSesion iniciarSesionEstudiante(InicioSesionEstudiante datos) throws Exception {
        try {
            Map<String, Object> resultado = repositorioAutenticacion.autenticarEstudiante(
                    datos.getCorreoElectronico(),
                    datos.getContrasena()
            );

            return procesarResultadoEstudiante(resultado);
        } catch (SQLException e) {
            throw new Exception("Error al autenticar al estudiante: " + e.getMessage(), e);
        }
    }

    private RespuestaInicioSesion procesarResultadoProfesor(Map<String, Object> resultado) throws Exception {
        String estado = (String) resultado.get("p_resultado");
        if ("EXITO".equals(estado)) {
            Long id = ((Number) resultado.get("p_profesor_id")).longValue();
            String nombre = (String) resultado.get("p_nombre");
            return new RespuestaInicioSesion(String.valueOf(id), nombre);
        } else {
            throw new Exception((String) resultado.get("p_mensaje"));
        }
    }

    private RespuestaInicioSesion procesarResultadoEstudiante(Map<String, Object> resultado) throws Exception {
        String estado = (String) resultado.get("p_resultado");
        if ("EXITO".equals(estado)) {
            Long id = ((Number) resultado.get("p_estudiante_id")).longValue();
            String nombre = (String) resultado.get("p_nombre");
            return new RespuestaInicioSesion(String.valueOf(id), nombre);
        } else {
            throw new Exception((String) resultado.get("p_mensaje"));
        }
    }
}
