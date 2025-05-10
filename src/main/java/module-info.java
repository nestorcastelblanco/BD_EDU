module com.example.bd_edu {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;
    requires ojdbc8;

    opens com.example.bd_edu to javafx.fxml;
    exports com.example.bd_edu;
    exports com.example.bd_edu.controllers;
    opens com.example.bd_edu.controllers to javafx.fxml;
    exports com.example.bd_edu.model;
    opens com.example.bd_edu.model to javafx.fxml;
}