package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingrese usuario y contraseña.");
            return;
        }

        if (username.equals("docente") && password.equals("12345")) {
            mostrarAlerta("Acceso concedido", "Bienvenido, " + username);
            cargarMenuDocente();
        }
        if (username.equals("estudiante") && password.equals("12345")) {
            mostrarAlerta("Acceso concedido", "Bienvenido, " + username);
            cargarMenuEstudiante();
        }
        else {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menudocente.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuestudiante.fxml"));
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
}
