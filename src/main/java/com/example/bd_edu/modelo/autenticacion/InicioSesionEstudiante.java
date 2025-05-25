package com.example.bd_edu.modelo.autenticacion;

public class InicioSesionEstudiante {
    private String correoElectronico;
    private String contrasena;

    public InicioSesionEstudiante() {
    }

    public InicioSesionEstudiante(String correoElectronico, String contrasena) {
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
