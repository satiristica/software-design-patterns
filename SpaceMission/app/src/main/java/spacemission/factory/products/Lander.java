package spacemission.factory.products;

public class Lander implements Spacecraft {

    private boolean toolsActive;

    @Override
    public void deploy() {
        toolsActive = true;
        System.out.println("Lander activated");
    }

    @Override
    public boolean isOperational() {
        return toolsActive;
    }
}