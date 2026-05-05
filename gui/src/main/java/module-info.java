module at.fhtw.disys.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;

    opens at.fhtw.disys.gui to javafx.fxml;
    exports at.fhtw.disys.gui;
}