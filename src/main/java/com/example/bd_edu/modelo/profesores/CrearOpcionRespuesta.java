package com.example.bd_edu.modelo.profesores;

public class CrearOpcionRespuesta {
    private String descripcion;
    private String esCorrecta;
    private Long idPregunta;

    public CrearOpcionRespuesta() {
    }

    public CrearOpcionRespuesta(String descripcion, String esCorrecta, Long idPregunta) {
        this.descripcion = descripcion;
        this.esCorrecta = esCorrecta;
        this.idPregunta = idPregunta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(String esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Long idPregunta) {
        this.idPregunta = idPregunta;
    }
}
