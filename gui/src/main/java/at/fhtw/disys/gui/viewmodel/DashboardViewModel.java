package at.fhtw.disys.gui.viewmodel;

import at.fhtw.disys.gui.dto.HistoricalEnergyDto;
import at.fhtw.disys.gui.model.HistoricalEnergyRow;

import java.time.LocalDate;
import java.util.List;

public class DashboardViewModel {

    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Please select a start and end date.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }
    }

    public double sumProduced(List<HistoricalEnergyDto> historicalData) {
        return historicalData.stream()
                .mapToDouble(HistoricalEnergyDto::communityProduced)
                .sum();
    }

    public double sumUsed(List<HistoricalEnergyDto> historicalData) {
        return historicalData.stream()
                .mapToDouble(HistoricalEnergyDto::communityUsed)
                .sum();
    }

    public double sumGridUsed(List<HistoricalEnergyDto> historicalData) {
        return historicalData.stream()
                .mapToDouble(HistoricalEnergyDto::gridUsed)
                .sum();
    }

    public List<HistoricalEnergyRow> toRows(List<HistoricalEnergyDto> historicalData) {
        return historicalData.stream()
                .map(dto -> new HistoricalEnergyRow(
                        dto.hour(),
                        dto.communityProduced(),
                        dto.communityUsed(),
                        dto.gridUsed()
                ))
                .toList();
    }
}