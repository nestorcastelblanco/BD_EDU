package com.example.bd_edu.modelo.estudiantes;

public class ObtenerEvaluacionesDisponibles {
    private Long idGrupo;
    private Long idEstudiante;
    private String fechaActual;

    public ObtenerEvaluacionesDisponibles() {
    }

    public ObtenerEvaluacionesDisponibles(Long idGrupo, Long idEstudiante, String fechaActual) {
        this.idGrupo = idGrupo;
        this.idEstudiante = idEstudiante;
        this.fechaActual = fechaActual;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(String fechaActual) {
        this.fechaActual = fechaActual;
    }
}
