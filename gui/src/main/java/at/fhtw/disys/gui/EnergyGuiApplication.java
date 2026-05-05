package at.fhtw.disys.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnergyGuiApplication extends Application {

    private final Label communityPoolLabel = new Label("Community Pool Used: -");
    private final Label gridPortionLabel = new Label("Grid Portion: -");

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Energy Communities Dashboard");

        Button refreshButton = new Button("Refresh current data");
        refreshButton.setOnAction(event -> loadCurrentEnergyData());

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                titleLabel,
                communityPoolLabel,
                gridPortionLabel,
                refreshButton
        );

        Scene scene = new Scene(root, 420, 250);

        stage.setTitle("Energy Communities");
        stage.setScene(scene);
        stage.show();
    }

    private void loadCurrentEnergyData() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            String json = response.body();

            double communityPoolUsedPercent = extractDouble(json, "communityPoolUsedPercent");
            double gridPortionPercent = extractDouble(json, "gridPortionPercent");

            communityPoolLabel.setText("Community Pool Used: " + communityPoolUsedPercent + "%");
            gridPortionLabel.setText("Grid Portion: " + gridPortionPercent + "%");

        } catch (IOException | InterruptedException exception) {
            communityPoolLabel.setText("Could not load data from API.");
            gridPortionLabel.setText("Is the backend running?");
        }
    }

    private double extractDouble(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return 0.0;
    }

    public static void main(String[] args) {
        launch();
    }
}