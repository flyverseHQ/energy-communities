package at.fhtw.disys.gui.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CurrentEnergyDto(
        String hour,
        double communityProduced,
        double communityUsed,
        double gridUsed,

        @JsonAlias({"communityPoolUsedPercent", "communityDepleted"})
        double communityDepleted,

        @JsonAlias({"gridPortionPercent", "gridPortion"})
        double gridPortion
) {
}