package com.example.bd_edu.modelo.profesores;

import java.time.LocalDateTime;

public class CrearEvaluacion {
    private Integer tiempoMaximo;
    private Integer cantidadPreguntas;
    private Integer porcentajeCurso;
    private String nombre;
    private Integer porcentajeAprobacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer preguntasAleatorias;
    private Long idTema;
    private Long idProfesor;
    private Long idGrupo;

    public CrearEvaluacion() {
    }

    public CrearEvaluacion(Integer tiempoMaximo, Integer cantidadPreguntas, Integer porcentajeCurso,
                           String nombre, Integer porcentajeAprobacion, LocalDateTime fechaInicio,
                           LocalDateTime fechaFin, Integer preguntasAleatorias,
                           Long idTema, Long idProfesor, Long idGrupo) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (preguntasAleatorias != null && cantidadPreguntas != null &&
                preguntasAleatorias > cantidadPreguntas) {
            throw new IllegalArgumentException("Las preguntas aleatorias no pueden ser más que el total.");
        }

        this.tiempoMaximo = tiempoMaximo;
        this.cantidadPreguntas = cantidadPreguntas;
        this.porcentajeCurso = porcentajeCurso;
        this.nombre = nombre;
        this.porcentajeAprobacion = porcentajeAprobacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.preguntasAleatorias = preguntasAleatorias;
        this.idTema = idTema;
        this.idProfesor = idProfesor;
        this.idGrupo = idGrupo;
    }

    public Integer getTiempoMaximo() {
        return tiempoMaximo;
    }

    public void setTiempoMaximo(Integer tiempoMaximo) {
        this.tiempoMaximo = tiempoMaximo;
    }

    public Integer getCantidadPreguntas() {
        return cantidadPreguntas;
    }

    public void setCantidadPreguntas(Integer cantidadPreguntas) {
        this.cantidadPreguntas = cantidadPreguntas;
    }

    public Integer getPorcentajeCurso() {
        return porcentajeCurso;
    }

    public void setPorcentajeCurso(Integer porcentajeCurso) {
        this.porcentajeCurso = porcentajeCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(Integer porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getPreguntasAleatorias() {
        return preguntasAleatorias;
    }

    public void setPreguntasAleatorias(Integer preguntasAleatorias) {
        this.preguntasAleatorias = preguntasAleatorias;
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

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }
}
