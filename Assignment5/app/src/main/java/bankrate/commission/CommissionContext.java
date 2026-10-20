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
        if (strategy instanceof PercentageCommission) {
            return amount * 0.16;
        }


        if (strategy instanceof FixedCommission) {
            return 100;
        }


        if (amount <= 10000) {
            return 0;
        }

        return (amount - 10000) * 0.05;
    }
}