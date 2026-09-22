package spacemission.abstractfactory.products.moon;

import spacemission.abstractfactory.products.EnvironmentalSensor;

public class MoonEnvironmentalSensor implements EnvironmentalSensor {

    @Override
    public String collectData() {
        return "Moon sensor collected some data.";
    }
}