package at.fhtw.disys.gui.controller;

import at.fhtw.disys.gui.client.EnergyApiClient;
import at.fhtw.disys.gui.dto.CurrentEnergyDto;
import at.fhtw.disys.gui.dto.HistoricalEnergyDto;
import at.fhtw.disys.gui.model.HistoricalEnergyRow;
import at.fhtw.disys.gui.viewmodel.DashboardViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML
    private Label communityUsageValue;

    @FXML
    private Label gridPortionValue;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label producedValue;

    @FXML
    private Label usedValue;

    @FXML
    private Label gridUsedValue;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<HistoricalEnergyRow> historicalTable;

    @FXML
    private TableColumn<HistoricalEnergyRow, String> hourColumn;

    @FXML
    private TableColumn<HistoricalEnergyRow, Double> producedColumn;

    @FXML
    private TableColumn<HistoricalEnergyRow, Double> usedColumn;

    @FXML
    private TableColumn<HistoricalEnergyRow, Double> gridUsedColumn;

    private final EnergyApiClient energyApiClient = new EnergyApiClient();
    private final DashboardViewModel viewModel = new DashboardViewModel();

    @FXML
    private void initialize() {
        startDatePicker.setValue(LocalDate.of(2025, 1, 10));
        endDatePicker.setValue(LocalDate.of(2025, 1, 10));

        hourColumn.setCellValueFactory(new PropertyValueFactory<>("hour"));
        producedColumn.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        usedColumn.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        gridUsedColumn.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));

        producedColumn.setCellFactory(column -> createFormattedDoubleCell());
        usedColumn.setCellFactory(column -> createFormattedDoubleCell());
        gridUsedColumn.setCellFactory(column -> createFormattedDoubleCell());
    }

    private TableCell<HistoricalEnergyRow, Double> createFormattedDoubleCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.3f", value));
            }
        };
    }

    @FXML
    private void onRefreshCurrentData() {
        try {
            CurrentEnergyDto currentEnergy = energyApiClient.getCurrentEnergy();

            communityUsageValue.setText(String.format("%.2f%%", currentEnergy.communityDepleted()));
            gridPortionValue.setText(String.format("%.2f%%", currentEnergy.gridPortion()));

            statusLabel.setText("Current data loaded successfully.");
        } catch (Exception exception) {
            statusLabel.setText("Could not connect to backend API.");
        }
    }

    @FXML
    private void onFetchHistoricalData() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            viewModel.validateDateRange(startDate, endDate);

            List<HistoricalEnergyDto> historicalData = energyApiClient.getHistoricalEnergy(startDate, endDate);

            double produced = viewModel.sumProduced(historicalData);
            double used = viewModel.sumUsed(historicalData);
            double gridUsed = viewModel.sumGridUsed(historicalData);

            producedValue.setText(String.format("%.3f kWh", produced));
            usedValue.setText(String.format("%.3f kWh", used));
            gridUsedValue.setText(String.format("%.3f kWh", gridUsed));

            historicalTable.setItems(FXCollections.observableArrayList(viewModel.toRows(historicalData)));

            if (historicalData.isEmpty()) {
                statusLabel.setText("No historical data found for selected date range.");
            } else {
                statusLabel.setText("Historical data loaded successfully.");
            }
        } catch (IllegalArgumentException exception) {
            statusLabel.setText(exception.getMessage());
        } catch (Exception exception) {
            statusLabel.setText("Could not load historical data.");
        }
    }
}