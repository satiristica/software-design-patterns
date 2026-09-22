package spacemission.abstractfactory.products.mars;

import spacemission.abstractfactory.products.NavigationModule;

public class MarsNavigationModule implements NavigationModule {
    @Override
    public String calcRoute() {
        return "Mars route calculated using astrodynamics";
    }
}