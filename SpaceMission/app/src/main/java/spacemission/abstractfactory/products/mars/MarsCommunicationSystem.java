package spacemission.abstractfactory.products.mars;

import spacemission.abstractfactory.products.CommunicationSystem;

public class MarsCommunicationSystem implements CommunicationSystem {
    @Override
    public String transmit(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data can't be empty");
        }
        return "Mars sent data: " + data;
    }
}