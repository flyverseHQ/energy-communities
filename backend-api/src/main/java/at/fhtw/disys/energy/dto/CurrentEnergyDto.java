package at.fhtw.disys.energy.dto;

public record CurrentEnergyDto(
        double communityPoolUsedPercent,
        double gridPortionPercent
) {

}