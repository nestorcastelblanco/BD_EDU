package com.example.bd_edu.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Examen {

    private int idExamen;
    private String titulo;
    private String descripcion;
    private String categoria;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int tiempoTotal;
    private int idDocente;
    private int idCurso;
    private BigDecimal umbralAprobacion;
    private int preguntasEstudiante;
    private int idTema;

    public Examen(int idExamen, String titulo) {
        this.idExamen = idExamen;
        this.titulo = titulo;
    }

}
