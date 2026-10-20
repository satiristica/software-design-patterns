package bankrate.commission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommissionStrategyTest {

    @Test
    void percentageCommission() {
        CommissionContext context =
                new CommissionContext(new PercentageCommission());

        assertEquals(1920, context.calculate(12000), 0.001);
    }

    @Test
    void fixedCommission() {
        CommissionContext context =
                new CommissionContext(new FixedCommission());

        assertEquals(100, context.calculate(12000), 0.001);
    }

    @Test
    void thresholdCommission() {
        CommissionContext context =
                new CommissionContext(new ThresholdCommission());

        assertEquals(100, context.calculate(12000), 0.001);
    }

    @Test
    void thresholdCommissionIsFreeUpToLimit() {
        CommissionContext context =
                new CommissionContext(new ThresholdCommission());

        assertEquals(0, context.calculate(5000), 0.001);
        assertEquals(0, context.calculate(10000), 0.001);
    }
}