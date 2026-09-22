package spacemission.factory.creators;

import spacemission.factory.products.Rover;
import spacemission.factory.products.MissionVehicle;

public class RoverMission extends Mission {
    @Override
    protected MissionVehicle createSpacecraft() {
        return new Rover();
    }
}