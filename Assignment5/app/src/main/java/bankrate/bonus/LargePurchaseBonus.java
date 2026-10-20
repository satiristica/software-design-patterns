package bankrate.bonus;

import bankrate.data.BonusProvider;

public class LargePurchaseBonus implements BonusStrategy {

    private BonusProvider provider;

    public LargePurchaseBonus(BonusProvider provider) {
        this.provider = provider;
    }

    @Override
    public int calculate(double amount) {
        if (amount > 10000) {
            return provider.getBonus();
        }

        return 0;
    }
}