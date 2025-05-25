package com.example.bd_edu;

import com.example.bd_edu.config.ConexionBD;
import com.example.bd_edu.controladores.LoginController;
import com.example.bd_edu.repositorios.RepositorioAutenticacion;
import com.example.bd_edu.servicios.ServicioAutenticacion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Objects;

public class QuizApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Establecer conexión con la base de datos
        Connection conexion = ConexionBD.obtenerConexion();

        // Inicializar repositorio y servicio
        RepositorioAutenticacion repositorio = new RepositorioAutenticacion(conexion);
        ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion(repositorio);

        // Cargar FXML y asignar el controlador manualmente
        FXMLLoader loader = new FXMLLoader(QuizApplication.class.getResource("/ventanas/login.fxml"));
        loader.setControllerFactory(param -> new LoginController(servicioAutenticacion)); // <-- Aquí inyectas el servicio
        Parent root = loader.load();

        // Crear escena y mostrar ventana
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(QuizApplication.class.getResource("/estilos.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Sistema de Exámenes");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(QuizApplication.class, args);
    }
}
