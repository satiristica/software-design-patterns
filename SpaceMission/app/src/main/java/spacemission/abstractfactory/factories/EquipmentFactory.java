package spacemission.abstractfactory.factories;

import spacemission.abstractfactory.products.CommunicationSystem;
import spacemission.abstractfactory.products.EnvironmentalSensor;
import spacemission.abstractfactory.products.NavigationModule;

public interface EquipmentFactory {

    EnvironmentalSensor createEnvironmentalSensor();

    CommunicationSystem createCommunicationSystem();

    NavigationModule createNavigationModule();
}