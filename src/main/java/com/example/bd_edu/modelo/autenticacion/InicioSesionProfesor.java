package com.example.bd_edu.modelo.autenticacion;

public class InicioSesionProfesor {
    private String correoElectronico;
    private String contrasena;

    public InicioSesionProfesor() {
    }

    public InicioSesionProfesor(String correoElectronico, String contrasena) {
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