package at.fhtw.disys.percentage.dto;

public record UsageUpdateMessage(
        String hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {
}