package spacemission.abstractfactory;

import spacemission.abstractfactory.factories.EquipmentFactory;
import spacemission.abstractfactory.products.CommunicationSystem;
import spacemission.abstractfactory.products.EnvironmentalSensor;
import spacemission.abstractfactory.products.NavigationModule;

public class MissionEquipmentManager {

    private final EnvironmentalSensor sensor;
    private final CommunicationSystem communication;
    private final NavigationModule navigation;

    public MissionEquipmentManager(EquipmentFactory factory) {
        sensor = factory.createEnvironmentalSensor();
        communication = factory.createCommunicationSystem();
        navigation = factory.createNavigationModule();
    }

    public void prepareEquipment() {
        String route = navigation.calcRoute();
        String environmentData = sensor.collectData();
        String transmission = communication.transmit(
                environmentData + " " + route
        );

        System.out.println(route);
        System.out.println(environmentData);
        System.out.println(transmission);
    }
}