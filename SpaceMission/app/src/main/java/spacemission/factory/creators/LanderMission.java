package spacemission.factory.creators;

import spacemission.factory.products.Lander;
import spacemission.factory.products.Spacecraft;

public class LanderMission extends Mission {
    @Override
    protected Spacecraft createSpacecraft() {
        return new Lander();
    }
}