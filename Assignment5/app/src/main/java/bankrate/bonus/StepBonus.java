package bankrate.bonus;

public class StepBonus implements BonusStrategy {

    @Override
    public int calculate(double amount) {
        int thousands = (int) (amount / 1000);
        return thousands * 15;
    }
}