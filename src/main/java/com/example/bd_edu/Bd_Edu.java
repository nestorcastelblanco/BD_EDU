package com.example.bd_edu;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.sql.*;

public class Bd_Edu {

    private static Bd_Edu  instance;
    private Connection connection;

    String rolUsuario;
    // Cambia estos datos según tu entorno
    private final String url = "jdbc:oracle:thin:@//localhost:1521/xe";
    private final String user = "SYSTEM";
    private final String password = "Arango2004";

    private static Bd_Edu bd_edu;

    public static Bd_Edu getInstance(){
        if(bd_edu == null){
            bd_edu = new Bd_Edu();
        }

        return  bd_edu;
    }

    private Bd_Edu() {
        try {
            // Carga del driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a Oracle.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean iniciarSesion(String usuario, String clave) {
        try {
            // Preparamos la llamada al procedimiento
            CallableStatement cs = connection.prepareCall("{ call validar_usuario_con_rol(?, ?, ?, ?) }");

            // Parámetros de entrada
            cs.setString(1, usuario);
            cs.setString(2, clave);

            // Parámetros de salida
            cs.registerOutParameter(3, Types.INTEGER);  // Para el estado de validación
            cs.registerOutParameter(4, Types.VARCHAR);  // Para el rol

            // Ejecutar
            cs.execute();

            // Obtener los valores de salida
            int resultado = cs.getInt(3);
            String rol = cs.getString(4);

            // Si el usuario es válido, retornamos el rol
            if (resultado > 0 && rol != null && !rol.isEmpty()) {
                this.rolUsuario = rol; // Guardar el rol del usuario
                return true;
            } else {
                System.out.println("Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al intentar iniciar sesión.");
        }
        return false;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public static void loadStage(String url, Event event) {

        try {
            ((Node) (event.getSource())).getScene().getWindow().hide();

            Parent root = FXMLLoader.load(Objects.requireNonNull(QuizApplication.class.getResource(url)));
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Plataforma");
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
