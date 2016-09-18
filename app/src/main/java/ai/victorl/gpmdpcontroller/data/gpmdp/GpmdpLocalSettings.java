package ai.victorl.gpmdpcontroller.data.gpmdp;

public interface GpmdpLocalSettings {

    void saveHostIpAddress(String gpmdpIpAddress);

    String getHostIpAddress();

    void saveAuthCode(String authCode);

    String getAuthCode();
}
