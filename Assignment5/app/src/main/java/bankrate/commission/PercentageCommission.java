package bankrate.commission;

public class PercentageCommission implements CommissionStrategy {

    @Override
    public double calculate(double amount) {
        return amount * 0.16;
    }
}