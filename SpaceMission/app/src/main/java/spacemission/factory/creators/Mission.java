package spacemission.factory.creators;

import spacemission.factory.products.Spacecraft;

public abstract class Mission {

    protected abstract Spacecraft createSpacecraft();

    public final void launchMission() {
        Spacecraft spacecraft = createSpacecraft();

        System.out.println("Preparing mission");
        spacecraft.deploy();

        if (!spacecraft.isOperational()) {
            throw new IllegalStateException("Deploy failed");
        }

        System.out.println("Mission is operational");
    }
}