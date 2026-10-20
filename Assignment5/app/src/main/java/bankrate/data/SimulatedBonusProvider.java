package bankrate.data;

public class SimulatedBonusProvider implements BonusProvider {

    @Override
    public int getBonus() {
        return 100;
    }
}