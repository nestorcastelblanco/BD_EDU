package com.example.bd_edu.modelo.profesores;

public class AsignarPreguntaAEvaluacion {
    private Long idPregunta;
    private Long idEvaluacion;
    private int porcentaje;
    private String tieneTiempoMaximo;
    private int tiempoPorPregunta;

    public AsignarPreguntaAEvaluacion() {
    }

    public AsignarPreguntaAEvaluacion(Long idPregunta, Long idEvaluacion, int porcentaje,
                                      String tieneTiempoMaximo, int tiempoPorPregunta) {
        this.idPregunta = idPregunta;
        this.idEvaluacion = idEvaluacion;
        this.porcentaje = porcentaje;
        this.tieneTiempoMaximo = tieneTiempoMaximo;
        this.tiempoPorPregunta = tiempoPorPregunta;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Long idPregunta) {
        this.idPregunta = idPregunta;
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getTieneTiempoMaximo() {
        return tieneTiempoMaximo;
    }

    public void setTieneTiempoMaximo(String tieneTiempoMaximo) {
        this.tieneTiempoMaximo = tieneTiempoMaximo;
    }

    public int getTiempoPorPregunta() {
        return tiempoPorPregunta;
    }

    public void setTiempoPorPregunta(int tiempoPorPregunta) {
        this.tiempoPorPregunta = tiempoPorPregunta;
    }
}