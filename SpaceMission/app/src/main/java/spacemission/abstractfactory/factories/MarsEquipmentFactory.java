package spacemission.abstractfactory.factories;

import spacemission.abstractfactory.products.CommunicationSystem;
import spacemission.abstractfactory.products.EnvironmentalSensor;
import spacemission.abstractfactory.products.NavigationModule;
import spacemission.abstractfactory.products.mars.MarsCommunicationSystem;
import spacemission.abstractfactory.products.mars.MarsEnvironmentalSensor;
import spacemission.abstractfactory.products.mars.MarsNavigationModule;

public class MarsEquipmentFactory implements EquipmentFactory {

    @Override
    public EnvironmentalSensor createEnvironmentalSensor() {
        return new MarsEnvironmentalSensor();
    }

    @Override
    public CommunicationSystem createCommunicationSystem() {
        return new MarsCommunicationSystem();
    }

    @Override
    public NavigationModule createNavigationModule() {
        return new MarsNavigationModule();
    }
}