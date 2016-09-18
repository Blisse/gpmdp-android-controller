package ai.victorl.gpmdpcontroller.data.gpmdp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.PairRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ConnectResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class GpmdpManager implements GpmdpController {
    private final GpmdpLocalSettings gpmdpLocalSettings;

    private GpmdpSocket gpmdpWebSocket = new GpmdpSocket();

    public GpmdpManager(GpmdpLocalSettings localSettings) {
        this.gpmdpLocalSettings = localSettings;

        EventBusUtils.safeRegister(getEventBus(), this);
    }

    @Override
    public EventBus getEventBus() {
        return gpmdpWebSocket.getEventBus();
    }

    @Override
    public void connect() {
        String gpmdpIpAddress = gpmdpLocalSettings.getHostIpAddress();

        if (NetUtils.isValidIpAddress(gpmdpIpAddress)) {
            gpmdpWebSocket.connect(gpmdpIpAddress);
        }
    }

    @Override
    public void disconnect() {
        gpmdpWebSocket.disconnect();
    }

    @Override
    public boolean isConnected() {
        return gpmdpWebSocket.isOpen();
    }

    @Override
    public boolean isAuthorized() {
        return gpmdpLocalSettings.getAuthCode() != null;
    }

    @Override
    public void pair() {
        PairRequest request = new PairRequest();
        gpmdpWebSocket.write(request);
    }

    @Override
    public void pair(String pin) {
        PairRequest request = new PairRequest(pin);
        gpmdpWebSocket.write(request);
    }

    @Override
    public void authorize() {
        String authCode = gpmdpLocalSettings.getAuthCode();
        PairRequest request = new PairRequest(authCode);
        gpmdpWebSocket.write(request);
        gpmdpWebSocket.getEventBus().post(new GpmdpAuthorizedEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectResponse response) {
        if (!response.isPinRequired()) {
            gpmdpLocalSettings.saveAuthCode(response.requestCode);
            authorize();
        }
    }
}
