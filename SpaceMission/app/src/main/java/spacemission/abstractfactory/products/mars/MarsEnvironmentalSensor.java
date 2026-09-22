package spacemission.abstractfactory.products.mars;

import spacemission.abstractfactory.products.EnvironmentalSensor;

public class MarsEnvironmentalSensor implements EnvironmentalSensor {
    @Override
    public String collectData() {
        return "Mars sensor collected some data.";
    }
}