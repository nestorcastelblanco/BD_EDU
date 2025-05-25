package com.example.bd_edu.modelo.estudiantes;

public class CrearPresentacionEvaluacion {
    private Long idEvaluacion;
    private Long idEstudiante;
    private String ipOrigen;

    public CrearPresentacionEvaluacion() {
    }

    public CrearPresentacionEvaluacion(Long idEvaluacion, Long idEstudiante, String ipOrigen) {
        this.idEvaluacion = idEvaluacion;
        this.idEstudiante = idEstudiante;
        this.ipOrigen = ipOrigen;
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }
}

