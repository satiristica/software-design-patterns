package spacemission.factory.creators;

import spacemission.factory.products.Lander;
import spacemission.factory.products.MissionVehicle;

public class LanderMission extends Mission {
    @Override
    protected MissionVehicle createSpacecraft() {
        return new Lander();
    }
}