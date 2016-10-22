package ai.victorl.gpmdpcontroller.data.gpmdp;

import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;

public class GpmdpSettings implements GpmdpLocalSettings {
    private static final String GPMDP_IPADDRESS_KEY = "ai.victorl.gpmdpcontroller.KEY_IPADDRESS";
    private static final String GPMDP_AUTHCODE_KEY = "ai.victorl.gpmdpcontroller.KEY_AUTHCODE";
    private final LocalSettings localSettings;

    public GpmdpSettings(LocalSettings localSettings) {
        this.localSettings = localSettings;
    }

    public void saveHostIpAddress(String gpmdpIpAddress) {
        localSettings.getSharedPreferences().edit()
                .putString(GPMDP_IPADDRESS_KEY, gpmdpIpAddress)
                .commit();
    }

    public String getHostIpAddress() {
        return localSettings.getSharedPreferences()
                .getString(GPMDP_IPADDRESS_KEY, "192.168.0.101");
    }

    @Override
    public void saveAuthCode(String authCode) {
        localSettings.getSharedPreferences().edit()
                .putString(GPMDP_AUTHCODE_KEY, authCode)
                .commit();
    }

    @Override
    public String getAuthCode() {
        return localSettings.getSharedPreferences()
                .getString(GPMDP_AUTHCODE_KEY, null);
    }
}
