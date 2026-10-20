package bankrate.bonus;

import bankrate.data.BonusProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusStrategyTest {

    @Test
    void fixedBonus() {
        BonusContext context = new BonusContext(new FixedBonus());

        assertEquals(15, context.calculate(12500));
    }

    @Test
    void stepBonus() {
        BonusContext context = new BonusContext(new StepBonus());

        assertEquals(180, context.calculate(12500));
        assertEquals(15, context.calculate(1000));
        assertEquals(0, context.calculate(999));
    }

    @Test
    void largePurchaseBonusUsesProvider() {
        BonusProvider provider = new FakeBonusProvider();

        BonusContext context =
                new BonusContext(new LargePurchaseBonus(provider));

        assertEquals(250, context.calculate(12500));
    }

    @Test
    void largePurchaseBonusIsZeroUpToLimit() {
        BonusProvider provider = new FakeBonusProvider();

        BonusContext context =
                new BonusContext(new LargePurchaseBonus(provider));

        assertEquals(0, context.calculate(5000));
        assertEquals(0, context.calculate(10000));
    }

    static class FakeBonusProvider implements BonusProvider {

        @Override
        public int getBonus() {
            return 250;
        }
    }
}