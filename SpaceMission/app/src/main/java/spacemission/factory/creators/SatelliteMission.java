package spacemission.factory.creators;

import spacemission.factory.products.Satellite;
import spacemission.factory.products.Spacecraft;

public class SatelliteMission extends Mission {

    @Override
    protected Spacecraft createSpacecraft() {
        return new Satellite();
    }
}