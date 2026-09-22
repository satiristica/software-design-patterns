package spacemission.abstractfactory.factories;

import spacemission.abstractfactory.products.CommunicationSystem;
import spacemission.abstractfactory.products.EnvironmentalSensor;
import spacemission.abstractfactory.products.NavigationModule;
import spacemission.abstractfactory.products.moon.MoonCommunicationSystem;
import spacemission.abstractfactory.products.moon.MoonEnvironmentalSensor;
import spacemission.abstractfactory.products.moon.MoonNavigationModule;

public class MoonEquipmentFactory implements EquipmentFactory {

    @Override
    public EnvironmentalSensor createEnvironmentalSensor() {
        return new MoonEnvironmentalSensor();
    }

    @Override
    public CommunicationSystem createCommunicationSystem() {
        return new MoonCommunicationSystem();
    }

    @Override
    public NavigationModule createNavigationModule() {
        return new MoonNavigationModule();
    }
}