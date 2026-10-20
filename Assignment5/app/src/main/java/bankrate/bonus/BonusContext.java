package bankrate.bonus;

public class BonusContext {

    private BonusStrategy strategy;

    public BonusContext(BonusStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(BonusStrategy strategy) {
        this.strategy = strategy;
    }

    public int calculate(double amount) {
        return strategy.calculate(amount);
    }
}