package com.example.bd_edu.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/xe";
    private static final String USUARIO = "SYSTEM";
    private static final String CONTRASENA = "Arango2004";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            logger.info("Driver Oracle cargado correctamente");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "ERROR: No se encontró el driver de Oracle", e);
            throw new RuntimeException("No se encontró el driver de Oracle", e);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            if (conexion != null && !conexion.isClosed()) {
                logger.info("Conexión establecida correctamente");
                return conexion;
            }
            throw new SQLException("No se pudo establecer la conexión");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ERROR al conectar a la base de datos", e);
            throw e;
        }
    }

    public static boolean validarConexion(Connection conexion) {
        try {
            return conexion != null && !conexion.isClosed() && conexion.isValid(5);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error validando conexión", e);
            return false;
        }
    }
}