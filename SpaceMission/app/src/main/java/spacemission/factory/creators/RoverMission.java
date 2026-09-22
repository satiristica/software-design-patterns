package spacemission.factory.creators;

import spacemission.factory.products.Rover;
import spacemission.factory.products.Spacecraft;

public class RoverMission extends Mission {
    @Override
    protected Spacecraft createSpacecraft() {
        return new Rover();
    }
}