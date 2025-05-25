module com.example.bd_edu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ojdbc8;
    requires static lombok;

    // Si usas estas bibliotecas visuales, las dejamos
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    // Abre los paquetes que contienen clases usadas con FXML
    opens com.example.bd_edu to javafx.fxml;
    exports com.example.bd_edu;

    opens com.example.bd_edu.controladores to javafx.fxml;
    exports com.example.bd_edu.controladores;

    opens com.example.bd_edu.modelo.profesores to javafx.fxml;
    exports com.example.bd_edu.modelo.profesores;
}
