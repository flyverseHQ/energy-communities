package at.fhtw.disys.gui.dto;

public record HistoricalEnergyDto(
        String hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {
}