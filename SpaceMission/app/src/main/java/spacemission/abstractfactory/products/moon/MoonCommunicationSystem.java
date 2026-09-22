package spacemission.abstractfactory.products.moon;

import spacemission.abstractfactory.products.CommunicationSystem;

public class MoonCommunicationSystem implements CommunicationSystem {

    @Override
    public String transmit(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data cant be empty");
        }

        return "Moon data sent: " + data;
    }
}