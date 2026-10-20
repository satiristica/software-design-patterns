package bankrate.bonus;

public class FixedBonus implements BonusStrategy {

    @Override
    public int calculate(double amount) {
        return 15;
    }
}