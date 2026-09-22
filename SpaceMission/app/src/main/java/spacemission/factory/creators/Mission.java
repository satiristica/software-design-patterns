package spacemission.factory.creators;

import spacemission.factory.products.MissionVehicle;

public abstract class Mission {

    protected abstract MissionVehicle createSpacecraft();

    public final void launchMission() {
        MissionVehicle spacecraft = createSpacecraft();

        System.out.println("Preparing mission");
        spacecraft.deploy();

        if (!spacecraft.isOperational()) {
            throw new IllegalStateException("Deploy failed");
        }

        System.out.println("Mission is operational");
    }
}