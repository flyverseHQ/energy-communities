package at.fhtw.disys.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnergyGuiApplication extends Application {

    private final Label communityUsageValue = new Label("0.00%");
    private final Label gridPortionValue = new Label("0.00%");

    private final DatePicker startDatePicker = new DatePicker(LocalDate.of(2025, 1, 10));
    private final DatePicker endDatePicker = new DatePicker(LocalDate.of(2025, 1, 10));

    private final Label producedValue = new Label("--- kWh");
    private final Label usedValue = new Label("--- kWh");
    private final Label gridUsedValue = new Label("--- kWh");

    private final Label statusLabel = new Label("");

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(26);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f4f6f8;");

        VBox currentCard = createCurrentStatusCard();
        VBox historicalCard = createHistoricalDataCard();

        root.getChildren().addAll(currentCard, historicalCard, statusLabel);

        Scene scene = new Scene(root, 640, 560);

        stage.setTitle("Energy Monitoring System");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createCurrentStatusCard() {
        Label title = new Label("Current Status");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("""
                -fx-background-color: #e5e7eb;
                -fx-font-size: 14px;
                -fx-padding: 8 16;
                -fx-background-radius: 6;
                """);
        refreshButton.setOnAction(event -> loadCurrentEnergyData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(title, spacer, refreshButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Separator separator = new Separator();

        Label communityLabel = new Label("Community Usage");
        communityLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");

        communityUsageValue.setStyle("-fx-text-fill: #15803d; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label gridLabel = new Label("Grid Portion");
        gridLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");

        gridPortionValue.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox communityBox = new VBox(8, communityLabel, communityUsageValue);
        VBox gridBox = new VBox(8, gridLabel, gridPortionValue);

        HBox values = new HBox(80, communityBox, gridBox);
        values.setAlignment(Pos.CENTER_LEFT);

        VBox card = createCard();
        card.getChildren().addAll(header, separator, values);

        return card;
    }

    private VBox createHistoricalDataCard() {
        Label title = new Label("Historical Data Query");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Separator separatorTop = new Separator();

        Label startLabel = new Label("Start Date");
        Label endLabel = new Label("End Date");

        Button fetchButton = new Button("Fetch");
        fetchButton.setStyle("""
                -fx-background-color: #1976d2;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 8 16;
                -fx-background-radius: 6;
                """);
        fetchButton.setOnAction(event -> loadHistoricalData());

        VBox startBox = new VBox(8, startLabel, startDatePicker);
        VBox endBox = new VBox(8, endLabel, endDatePicker);

        HBox inputRow = new HBox(20, startBox, endBox, fetchButton);
        inputRow.setAlignment(Pos.BOTTOM_LEFT);

        Separator separatorBottom = new Separator();

        Label producedLabel = new Label("Produced");
        Label usedLabel = new Label("Used");
        Label gridUsedLabel = new Label("Grid Usage");

        producedValue.setStyle("-fx-text-fill: #15803d; -fx-font-size: 18px; -fx-font-weight: bold;");
        usedValue.setStyle("-fx-text-fill: #0057d9; -fx-font-size: 18px; -fx-font-weight: bold;");
        gridUsedValue.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox producedBox = new VBox(8, producedLabel, producedValue);
        VBox usedBox = new VBox(8, usedLabel, usedValue);
        VBox gridUsedBox = new VBox(8, gridUsedLabel, gridUsedValue);

        producedBox.setAlignment(Pos.CENTER);
        usedBox.setAlignment(Pos.CENTER);
        gridUsedBox.setAlignment(Pos.CENTER);

        HBox resultsRow = new HBox(50, producedBox, usedBox, gridUsedBox);
        resultsRow.setAlignment(Pos.CENTER);

        VBox card = createCard();
        card.getChildren().addAll(title, separatorTop, inputRow, separatorBottom, resultsRow);

        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(22));
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #d9d9d9;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);
                """);
        return card;
    }

    private void loadCurrentEnergyData() {
        try {
            String json = sendGetRequest("http://localhost:8080/energy/current");

            double communityPoolUsedPercent = extractDouble(json, "communityPoolUsedPercent");
            double gridPortionPercent = extractDouble(json, "gridPortionPercent");

            communityUsageValue.setText(String.format("%.2f%%", communityPoolUsedPercent));
            gridPortionValue.setText(String.format("%.2f%%", gridPortionPercent));
            statusLabel.setText("Current data loaded successfully.");

        } catch (Exception exception) {
            statusLabel.setText("Could not connect to backend API.");
        }
    }

    private void loadHistoricalData() {
        try {
            String start = startDatePicker.getValue() + "T00:00:00";
            String end = endDatePicker.getValue() + "T23:59:59";

            String url = "http://localhost:8080/energy/historical?start=" + start + "&end=" + end;
            String json = sendGetRequest(url);

            double produced = sumField(json, "communityProduced");
            double used = sumField(json, "communityUsed");
            double gridUsed = sumField(json, "gridUsed");

            producedValue.setText(String.format("%.3f kWh", produced));
            usedValue.setText(String.format("%.3f kWh", used));
            gridUsedValue.setText(String.format("%.3f kWh", gridUsed));

            statusLabel.setText("Historical data loaded successfully.");

        } catch (Exception exception) {
            statusLabel.setText("Could not load historical data.");
        }
    }

    private String sendGetRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }

    private double extractDouble(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return 0.0;
    }

    private double sumField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(json);

        double sum = 0.0;

        while (matcher.find()) {
            sum += Double.parseDouble(matcher.group(1));
        }

        return sum;
    }

    public static void main(String[] args) {
        launch(args);
    }
}