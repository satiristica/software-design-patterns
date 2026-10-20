package bankrate.commission;

public class ThresholdCommission implements CommissionStrategy {

    @Override
    public double calculate(double amount) {
        if (amount <= 10000) {
            return 0;
        }

        return (amount - 10000) * 0.05;
    }
}