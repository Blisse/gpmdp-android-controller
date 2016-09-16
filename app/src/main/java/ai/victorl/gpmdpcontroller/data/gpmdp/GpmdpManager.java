package ai.victorl.gpmdpcontroller.data.gpmdp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Channel;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpResponseEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class GpmdpManager implements GpmdpController {
    private final GpmdpLocalSettings gpmdpLocalSettings;

    private GpmdpSocket gpmdpWebSocket = new GpmdpSocket();

    public GpmdpManager(GpmdpLocalSettings localSettings) {
        this.gpmdpLocalSettings = localSettings;
    }

    @Override
    public EventBus getEventBus() {
        return gpmdpWebSocket.getEventBus();
    }

    @Override
    public void connect() {
        String gpmdpIpAddress = gpmdpLocalSettings.getGpmdpIpAddress();

        if (NetUtils.isValidIpAddress(gpmdpIpAddress)) {
            EventBusUtils.safeRegister(getEventBus(), this);
            gpmdpWebSocket.connect(gpmdpIpAddress);
        }
    }

    @Override
    public void disconnect() {
        EventBusUtils.safeUnregister(getEventBus(), this);
        gpmdpWebSocket.disconnect();
    }

    @Override
    public boolean isConnected() {
        return gpmdpWebSocket.isOpen();
    }

    @Subscribe
    public void onEvent(GpmdpResponseEvent event) {
        if (event.response.channel.equals(Channel.CONNECT)) {

        }
    }
}
