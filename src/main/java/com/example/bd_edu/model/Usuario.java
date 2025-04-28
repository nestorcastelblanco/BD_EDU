package com.example.bd_edu.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    String nombre;
    String correo;
    String password;
    String rol;
}
