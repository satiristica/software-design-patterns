package bankrate.commission;

public interface CommissionStrategy {
    double calculate(double amount);
}