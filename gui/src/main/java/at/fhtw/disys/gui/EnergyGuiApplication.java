package at.fhtw.disys.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class EnergyGuiApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                EnergyGuiApplication.class.getResource("/at/fhtw/disys/gui/dashboard-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 820, 640);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        EnergyGuiApplication.class.getResource("/at/fhtw/disys/gui/styles.css")
                ).toExternalForm()
        );

        stage.setTitle("Energy Monitoring System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}