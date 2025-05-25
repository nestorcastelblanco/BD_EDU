package com.example.bd_edu.modelo.profesores;

public class CrearPregunta {
    private String enunciado;
    private String esPublica;
    private String tipoPregunta;
    private Long idPreguntaCompuesta;
    private Long idTema;
    private Long idProfesor;

    public CrearPregunta() {
    }

    public CrearPregunta(String enunciado, String esPublica, String tipoPregunta,
                         Long idPreguntaCompuesta, Long idTema, Long idProfesor) {
        if (tipoPregunta != null && !esTipoValido(tipoPregunta)) {
            throw new IllegalArgumentException("Tipo de pregunta inválido.");
        }

        this.enunciado = enunciado;
        this.esPublica = esPublica;
        this.tipoPregunta = tipoPregunta;
        this.idPreguntaCompuesta = idPreguntaCompuesta;
        this.idTema = idTema;
        this.idProfesor = idProfesor;
    }

    private boolean esTipoValido(String tipo) {
        return tipo.equals("Selección única") || tipo.equals("Selección múltiple")
                || tipo.equals("Verdadero/Falso") || tipo.equals("Ordenamiento");
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getEsPublica() {
        return esPublica;
    }

    public void setEsPublica(String esPublica) {
        this.esPublica = esPublica;
    }

    public String getTipoPregunta() {
        return tipoPregunta;
    }

    public void setTipoPregunta(String tipoPregunta) {
        this.tipoPregunta = tipoPregunta;
    }

    public Long getIdPreguntaCompuesta() {
        return idPreguntaCompuesta;
    }

    public void setIdPreguntaCompuesta(Long idPreguntaCompuesta) {
        this.idPreguntaCompuesta = idPreguntaCompuesta;
    }

    public Long getIdTema() {
        return idTema;
    }

    public void setIdTema(Long idTema) {
        this.idTema = idTema;
    }

    public Long getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(Long idProfesor) {
        this.idProfesor = idProfesor;
    }
}
