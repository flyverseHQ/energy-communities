package at.fhtw.disys.percentage.service;

import org.springframework.stereotype.Service;

@Service
public class PercentageCalculationService {

    public double calculateCommunityDepleted(double communityProduced, double communityUsed) {
        if (communityProduced <= 0) {
            return 0.0;
        }

        return roundToTwoDecimals((communityUsed / communityProduced) * 100);
    }

    public double calculateGridPortion(double communityUsed, double gridUsed) {
        double totalUsed = communityUsed + gridUsed;

        if (totalUsed <= 0) {
            return 0.0;
        }

        return roundToTwoDecimals((gridUsed / totalUsed) * 100);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}