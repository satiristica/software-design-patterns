package spacemission.abstractfactory;

import spacemission.abstractfactory.factories.EquipmentFactory;
import spacemission.abstractfactory.factories.MarsEquipmentFactory;
import spacemission.abstractfactory.factories.MoonEquipmentFactory;

public class AbstractFactoryShow {

    public static void main(String[] args) {
        runFamily(
                "Mars equipment",
                new MarsEquipmentFactory()
        );

        runFamily(
                "Moon equipment",
                new MoonEquipmentFactory()
        );
    }

    private static void runFamily(String title, EquipmentFactory factory) {
        System.out.println("Abstract factory: " + title);

        MissionEquipmentManager manager = new MissionEquipmentManager(factory);

        manager.prepareEquipment();
        System.out.println();
    }
}