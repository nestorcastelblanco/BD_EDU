package com.example.bd_edu.model;
import javafx.beans.property.SimpleStringProperty;

public class Emparejamiento {
    private final SimpleStringProperty opcionA;
    private final SimpleStringProperty opcionB;

    public Emparejamiento(String opcionA, String opcionB) {
        this.opcionA = new SimpleStringProperty(opcionA);
        this.opcionB = new SimpleStringProperty(opcionB);
    }

    public String getOpcionA() {
        return opcionA.get();
    }

    public void setOpcionA(String value) {
        opcionA.set(value);
    }

    public String getOpcionB() {
        return opcionB.get();
    }

    public void setOpcionB(String value) {
        opcionB.set(value);
    }
}
