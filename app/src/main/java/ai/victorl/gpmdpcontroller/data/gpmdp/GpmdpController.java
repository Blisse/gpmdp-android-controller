package ai.victorl.gpmdpcontroller.data.gpmdp;

import org.greenrobot.eventbus.EventBus;

public interface GpmdpController {
    EventBus getEventBus();

    void connect();

    void disconnect();

    void pin(String authCode);

    void tryAuthorize();

    void requestState();

    void playPause();

    void forward();

    void rewind();

    void getCurrentTime();

    void setCurrentTime(int ms);

    void getPlaybackState();
}
