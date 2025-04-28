package com.example.bd_edu.controllers;

import com.example.bd_edu.Bd_Edu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    Bd_Edu bd_edu = Bd_Edu.getInstance();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingrese usuario y contraseña.");
            return;
        }

        if (bd_edu.iniciarSesion(username, password)) {
            String rol = bd_edu.getRolUsuario();
            if ("docente".equals(rol)) {
                mostrarAlerta("Acceso concedido", "Bienvenido, Docente " + username);
                cargarMenuDocente(); // Cargar la vista de docente
            } else if ("estudiante".equals(rol)) {
                mostrarAlerta("Acceso concedido", "Bienvenido, Estudiante " + username);
                cargarMenuEstudiante(); // Cargar la vista de estudiante
            } else if ("admin".equals(rol)) {
                mostrarAlerta("Acceso concedido", "Bienvenido, Administrador " + username);
            }
        } else {
            errorLabel.setText("Credenciales incorrectas. Inténtelo nuevamente.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarMenuDocente() {
        try {
            // Cargar la siguiente vista (ejemplo: MenuDocente)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menudocente.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Docente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la siguiente ventana.");
        }
    }

    private void cargarMenuEstudiante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menuestudiante.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Menú Estudiante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la siguiente ventana.");
        }
    }

    public void onFocus(MouseEvent event) {
        TextField textField = (TextField) event.getSource();
        textField.setStyle("-fx-border-color: #2575fc; -fx-border-width: 2px;");
    }

}
