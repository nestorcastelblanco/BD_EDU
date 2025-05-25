package com.example.bd_edu.repositorios;

import com.example.bd_edu.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RepositorioAutenticacion {

    private final Connection conexion;

    public RepositorioAutenticacion(Connection conexion) throws SQLException {
        if (!ConexionBD.validarConexion(conexion)) {
            throw new SQLException("La conexión proporcionada no es válida");
        }
        this.conexion = conexion;
    }

    public Map<String, Object> autenticarProfesor(String correo, String contrasena) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "SELECT id_profesor, nombre FROM PROFESOR WHERE correo = ? AND contrasena = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado.put("p_resultado", "EXITO");
                    resultado.put("p_profesor_id", rs.getLong("id_profesor"));
                    resultado.put("p_nombre", rs.getString("nombre"));
                } else {
                    resultado.put("p_resultado", "ERROR");
                    resultado.put("p_mensaje", "Correo o contraseña incorrectos");
                }
            }
        } catch (SQLException e) {
            resultado.put("p_resultado", "ERROR");
            resultado.put("p_mensaje", "Error al acceder a la base de datos: " + e.getMessage());
            throw e;
        }

        return resultado;
    }

    public Map<String, Object> autenticarEstudiante(String correo, String contrasena) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "SELECT id_estudiante, nombre FROM ESTUDIANTE WHERE correo = ? AND contrasena = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado.put("p_resultado", "EXITO");
                    resultado.put("p_estudiante_id", rs.getLong("id_estudiante"));
                    resultado.put("p_nombre", rs.getString("nombre"));
                } else {
                    resultado.put("p_resultado", "ERROR");
                    resultado.put("p_mensaje", "Correo o contraseña incorrectos");
                }
            }
        } catch (SQLException e) {
            resultado.put("p_resultado", "ERROR");
            resultado.put("p_mensaje", "Error al acceder a la base de datos: " + e.getMessage());
            throw e;
        }

        return resultado;
    }
}
