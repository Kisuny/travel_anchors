package ca.lukegrahamlandry.travelstaff.platform;

import ca.lukegrahamlandry.travelstaff.TravelStaffMain;
import ca.lukegrahamlandry.travelstaff.platform.services.INetworkHelper;
import ca.lukegrahamlandry.travelstaff.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        TravelStaffMain.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
