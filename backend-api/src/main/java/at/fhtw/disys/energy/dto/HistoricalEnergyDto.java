package at.fhtw.disys.energy.dto;

public record HistoricalEnergyDto(
        String hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {}