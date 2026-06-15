package at.fhtw.disys.percentage.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PercentageCalculationServiceTest {

    private final PercentageCalculationService service = new PercentageCalculationService();

    @Test
    void calculatesCommunityDepletedPercentage() {
        double result = service.calculateCommunityDepleted(143.024, 130.101);

        assertEquals(90.96, result);
    }

    @Test
    void returnsZeroCommunityDepletedWhenProducedIsZero() {
        double result = service.calculateCommunityDepleted(0, 50);

        assertEquals(0.0, result);
    }

    @Test
    void calculatesGridPortionPercentage() {
        double result = service.calculateGridPortion(130.101, 14.75);

        assertEquals(10.18, result);
    }

    @Test
    void returnsZeroGridPortionWhenTotalUsageIsZero() {
        double result = service.calculateGridPortion(0, 0);

        assertEquals(0.0, result);
    }
}