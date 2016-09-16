package ai.victorl.gpmdpcontroller.data.gpmdp;

import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;

public class GpmdpSettings implements GpmdpLocalSettings {
    private static final String GPMDP_IPADDRESS_KEY = "GPMDP_IPADDRESS_KEY";
    private final LocalSettings localSettings;

    public GpmdpSettings(LocalSettings localSettings) {
        this.localSettings = localSettings;
    }

    public void saveGpmdpIpAddress(String gpmdpIpAddress) {
        localSettings.getSharedPreferences().edit()
                .putString(GPMDP_IPADDRESS_KEY, gpmdpIpAddress)
                .commit();
    }

    public String getGpmdpIpAddress() {
        return localSettings.getSharedPreferences()
                .getString(GPMDP_IPADDRESS_KEY, "192.168.0.1");
    }
}
