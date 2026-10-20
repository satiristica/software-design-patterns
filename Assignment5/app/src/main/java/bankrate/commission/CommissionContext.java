package bankrate.commission;

public class CommissionContext {

    private CommissionStrategy strategy;

    public CommissionContext(CommissionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(CommissionStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(double amount) {
        return strategy.calculate(amount);
    }

}