package bankrate.bonus;

import bankrate.CustomerType;
import bankrate.data.BonusProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusSelectorTest {

    @Test
    void defaultCustomerGetsFixedBonus() {
        BonusSelector selector =
                new BonusSelector(new FakeBonusProvider());

        assertInstanceOf(
                FixedBonus.class,
                selector.select(CustomerType.DEFAULT)
        );
    }

    @Test
    void brokerGetsStepBonus() {
        BonusSelector selector =
                new BonusSelector(new FakeBonusProvider());

        assertInstanceOf(
                StepBonus.class,
                selector.select(CustomerType.BROKER)
        );
    }

    @Test
    void eliteGetsLargePurchaseBonus() {
        BonusSelector selector =
                new BonusSelector(new FakeBonusProvider());

        BonusStrategy strategy = selector.select(CustomerType.ELITE);

        assertInstanceOf(LargePurchaseBonus.class, strategy);
        assertEquals(250, strategy.calculate(12500));
    }

    static class FakeBonusProvider implements BonusProvider {

        @Override
        public int getBonus() {
            return 250;
        }
    }
}