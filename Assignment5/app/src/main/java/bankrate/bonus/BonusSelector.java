package bankrate.bonus;

import bankrate.CustomerType;
import bankrate.data.BonusProvider;

public class BonusSelector {

    private BonusProvider provider;

    public BonusSelector(BonusProvider provider) {
        this.provider = provider;
    }

    public BonusStrategy select(CustomerType type) {
        if (type == CustomerType.DEFAULT) {
            return new FixedBonus();
        }

        if (type == CustomerType.BROKER) {
            return new StepBonus();
        }

        if (type == CustomerType.ELITE) {
            return new LargePurchaseBonus(provider);
        }

        throw new IllegalArgumentException("Unknown customer type");
    }
}