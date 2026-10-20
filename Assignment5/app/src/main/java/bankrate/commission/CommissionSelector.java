package bankrate.commission;

import bankrate.CustomerType;

public class CommissionSelector {

    public CommissionStrategy select(CustomerType type) {
        if (type == CustomerType.DEFAULT) {
            return new PercentageCommission();
        }

        if (type == CustomerType.BROKER) {
            return new FixedCommission();
        }

        if (type == CustomerType.ELITE) {
            return new ThresholdCommission();
        }

        throw new IllegalArgumentException("Unknown customer type");
    }
}