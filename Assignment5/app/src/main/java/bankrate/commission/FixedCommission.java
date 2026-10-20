package bankrate.commission;

public class FixedCommission implements CommissionStrategy {

    @Override
    public double calculate(double amount) {
        return 100;
    }
}