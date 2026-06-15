module at.fhtw.disys.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;

    opens at.fhtw.disys.gui.controller to javafx.fxml;
    opens at.fhtw.disys.gui.dto to com.fasterxml.jackson.databind;
    opens at.fhtw.disys.gui.model to javafx.base;

    exports at.fhtw.disys.gui;
}