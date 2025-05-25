package com.example.bd_edu.controladores;

import com.example.bd_edu.modelo.autenticacion.InicioSesionEstudiante;
import com.example.bd_edu.modelo.autenticacion.InicioSesionProfesor;
import com.example.bd_edu.modelo.autenticacion.RespuestaInicioSesion;
import com.example.bd_edu.servicios.ServicioAutenticacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField campoCorreo;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private RadioButton radioProfesor;

    @FXML
    private RadioButton radioEstudiante;

    private final ServicioAutenticacion servicioAutenticacion;

    public LoginController(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @FXML
    public void initialize() {
        ToggleGroup grupo = new ToggleGroup();
        radioProfesor.setToggleGroup(grupo);
        radioEstudiante.setToggleGroup(grupo);
        radioEstudiante.setSelected(true); // por defecto
    }

    @FXML
    private void manejarInicioSesion() {
        String correo = campoCorreo.getText();
        String contrasena = campoContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor ingrese el correo y la contraseña.");
            return;
        }

        try {
            RespuestaInicioSesion respuesta;
            if (radioProfesor.isSelected()) {
                InicioSesionProfesor datos = new InicioSesionProfesor(correo, contrasena);
                respuesta = servicioAutenticacion.iniciarSesionProfesor(datos);

                // Redirigir a la vista del profesor
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menu-profesor.fxml"));
                Parent root = loader.load();

                // Obtener el controlador y pasar los datos
                MenuProfesorController controller = loader.getController();
                controller.inicializarDatos(respuesta.getNombre(), Long.valueOf(respuesta.getId()));

                Stage stage = new Stage();
                stage.setTitle("Menú del Profesor");
                stage.setScene(new Scene(root));
                stage.show();

                cerrarVentana(); // Cerrar login

            } else {
                InicioSesionEstudiante datos = new InicioSesionEstudiante(correo, contrasena);
                respuesta = servicioAutenticacion.iniciarSesionEstudiante(datos);

                // Redirigir a la vista del estudiante
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/menu-estudiante.fxml"));
                Parent root = loader.load();

                // Obtener el controlador y pasar los datos
                MenuEstudianteController controller = loader.getController();
                controller.inicializarDatos(respuesta.getNombre(), Long.valueOf(respuesta.getId()));

                Stage stage = new Stage();
                stage.setTitle("Menú del Estudiante");
                stage.setScene(new Scene(root));
                stage.show();

                cerrarVentana(); // Cerrar login
            }

        } catch (Exception e) {
            e.printStackTrace(); // Para depuración
            mostrarAlerta("Error de autenticación", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) campoCorreo.getScene().getWindow();
        stage.close();
    }
}