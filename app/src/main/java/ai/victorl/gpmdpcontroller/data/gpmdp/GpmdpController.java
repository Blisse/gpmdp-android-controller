package ai.victorl.gpmdpcontroller.data.gpmdp;

import org.greenrobot.eventbus.EventBus;

public interface GpmdpController {
    EventBus getEventBus();

    void connect();

    void disconnect();

    boolean isConnected();

    boolean isAuthorized();

    void pair();

    void pair(String authCode);

    void authorize();
}
