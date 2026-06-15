package at.fhtw.disys.energy.dto;

public record CurrentEnergyDto(
        String hour,
        double communityProduced,
        double communityUsed,
        double gridUsed,
        double communityDepleted,
        double gridPortion
) {
}