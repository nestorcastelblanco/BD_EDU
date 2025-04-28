package com.example.bd_edu;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Bd_Edu {

    private static final Logger LOGGER = Logger.getLogger(Bd_Edu.class.getName());

    private Connection connection;

    private String rolUsuario;

    private static Bd_Edu bd_edu;

    public static Bd_Edu getInstance() {
        if (bd_edu == null) {
            bd_edu = new Bd_Edu();
        }
        return bd_edu;
    }

    private Bd_Edu() {
        try {
            String url = "jdbc:oracle:thin:@//localhost:1521/xe";
            String user = "SYSTEM";
            String password = "Arango2004";

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a Oracle.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a Oracle", e);
        }
    }

    public boolean iniciarSesion(String usuario, String clave) {
        try (CallableStatement cs = connection.prepareCall("{ call validar_usuario_con_rol(?, ?, ?, ?) }")) {

            cs.setString(1, usuario);
            cs.setString(2, clave);

            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);

            cs.execute();

            int resultado = cs.getInt(3);
            String rol = cs.getString(4);

            if (resultado > 0 && rol != null && !rol.isEmpty()) {
                this.rolUsuario = rol;
                return true;
            } else {
                LOGGER.warning("Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar iniciar sesión", e);
        }
        return false;
    }

    public static void loadStage(String url, Event event) {
        try {
            ((Node) (event.getSource())).getScene().getWindow().hide();

            Parent root = FXMLLoader.load(Objects.requireNonNull(QuizApplication.class.getResource(url)));
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Plataforma");
            newStage.centerOnScreen();
            newStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana", e);
        }
    }
}
