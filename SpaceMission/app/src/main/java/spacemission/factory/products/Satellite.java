package spacemission.factory.products;

public class Satellite implements MissionVehicle {

    private boolean inOrbit;

    @Override
    public void deploy() {
        inOrbit = true;
        System.out.println("Satellite deployed into orbit.");
    }

    @Override
    public boolean isOperational() {
        return inOrbit;
    }
}