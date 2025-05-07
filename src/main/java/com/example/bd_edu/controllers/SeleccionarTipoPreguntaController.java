package com.example.bd_edu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SeleccionarTipoPreguntaController {

    @FXML
    private Button btnUnica, btnMultiple, btnVF, btnEmparejar, btnOrdenar, btnCompletar;

    @FXML
    public void initialize() {
        btnUnica.setOnAction(e -> abrirVentana("unicaRespuesta.fxml"));
        btnMultiple.setOnAction(e -> abrirVentana("seleccionMultiple.fxml"));
        btnVF.setOnAction(e -> abrirVentana("falsoVerdadero.fxml"));
        btnEmparejar.setOnAction(e -> abrirVentana("emparejar.fxml"));
        btnOrdenar.setOnAction(e -> abrirVentana("ordenar.fxml"));
        btnCompletar.setOnAction(e -> abrirVentana("completar.fxml"));
    }

    private void abrirVentana(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanas/" + fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Crear pregunta");
            stage.setScene(new Scene(root));
            stage.show();
            // Opcional: cerrar esta ventana si es modal
            ((Stage) btnUnica.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

