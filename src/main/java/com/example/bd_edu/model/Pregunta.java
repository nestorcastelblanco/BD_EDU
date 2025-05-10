package com.example.bd_edu.model;
import lombok.*;
        import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pregunta {
    private Integer id;             // ID\_PREGUNTA
    private String texto;           // TEXTO (CLOB)
    private String tipo;            // TIPO (VARCHAR2(50))
    private BigDecimal porcentaje;  // PORCENTAJE (NUMBER(5,2))
    private Integer tiempoLimite;   // TIEMPO\_LIMITE (NUMBER)
    private Integer idBanco;        // ID\_BANCO (NUMBER)
    private Integer idTema;         // ID\_TEMA (NUMBER)
    private String estado;          // ESTADO (VARCHAR2(20))
}
