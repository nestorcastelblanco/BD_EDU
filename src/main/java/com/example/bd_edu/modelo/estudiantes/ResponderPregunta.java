package com.example.bd_edu.modelo.estudiantes;

public class ResponderPregunta {
    private Long idPresentacionEvaluacion;
    private Long idPregunta;
    private Long idRespuesta;

    public ResponderPregunta() {
    }

    public ResponderPregunta(Long idPresentacionEvaluacion, Long idPregunta, Long idRespuesta) {
        this.idPresentacionEvaluacion = idPresentacionEvaluacion;
        this.idPregunta = idPregunta;
        this.idRespuesta = idRespuesta;
    }

    public Long getIdPresentacionEvaluacion() {
        return idPresentacionEvaluacion;
    }

    public void setIdPresentacionEvaluacion(Long idPresentacionEvaluacion) {
        this.idPresentacionEvaluacion = idPresentacionEvaluacion;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Long idPregunta) {
        this.idPregunta = idPregunta;
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Long idRespuesta) {
        this.idRespuesta = idRespuesta;
    }
}
