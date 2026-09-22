package spacemission.factory.creators;

import spacemission.factory.products.Satellite;
import spacemission.factory.products.MissionVehicle;

public class SatelliteMission extends Mission {

    @Override
    protected MissionVehicle createSpacecraft() {
        return new Satellite();
    }
}