package bankrate.commission;

import bankrate.CustomerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CommissionSelectorTest {

    private final CommissionSelector selector = new CommissionSelector();

    @Test
    void defaultCustomerGetsPercentageCommission() {
        assertInstanceOf(
                PercentageCommission.class,
                selector.select(CustomerType.DEFAULT)
        );
    }

    @Test
    void brokerGetsFixedCommission() {
        assertInstanceOf(
                FixedCommission.class,
                selector.select(CustomerType.BROKER)
        );
    }

    @Test
    void eliteGetsThresholdCommission() {
        assertInstanceOf(
                ThresholdCommission.class,
                selector.select(CustomerType.ELITE)
        );
    }
}