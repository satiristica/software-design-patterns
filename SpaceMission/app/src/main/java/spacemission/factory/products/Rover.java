package spacemission.factory.products;

public class Rover implements Spacecraft {

    private boolean deployed;

    @Override
    public void deploy() {
        deployed = true;
        System.out.println("Rover deployed.");
    }

    @Override
    public boolean isOperational() {
        return deployed;
    }
}