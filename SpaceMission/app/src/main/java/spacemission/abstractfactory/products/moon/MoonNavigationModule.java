package spacemission.abstractfactory.products.moon;

import spacemission.abstractfactory.products.NavigationModule;

public class MoonNavigationModule implements NavigationModule {

    @Override
    public String calcRoute() {
        return "Moon route calculated using ephemerides.";
    }
}