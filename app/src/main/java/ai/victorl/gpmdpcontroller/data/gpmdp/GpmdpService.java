package ai.victorl.gpmdpcontroller.data.gpmdp;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.PairRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ConnectResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlayStateResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RatingResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RepeatResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResultsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ShuffleResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TimeResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class GpmdpService implements GpmdpController {
        private static int GPMDP_TIME_DELAY_MS = 500;

    private final EventBus serviceEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();
    private final GpmdpSocket socket;
    private final GpmdpLocalSettings localSettings;
    private final Gson gson;

    private long lastTimeResponseMs = 0;

    public GpmdpService(GpmdpLocalSettings localSettings, Gson gson) {
        this.localSettings = localSettings;
        this.gson = gson;

        socket = new GpmdpSocket();

        EventBusUtils.safeRegister(socket.getEventBus(), this);
    }

    @Override
    public EventBus getEventBus() {
        return serviceEventBus;
    }

    @Override
    public void connect() {
        String gpmdpIpAddress = localSettings.getHostIpAddress();

        if (NetUtils.isValidIpAddress(gpmdpIpAddress)) {
            socket.connect(gpmdpIpAddress);
        }
    }

    @Override
    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public boolean isConnected() {
        return socket.isOpen();
    }

    private boolean isAuthorized() {
        return !TextUtils.isEmpty(localSettings.getAuthCode());
    }

    private void pair() {
        socket.write(gson.toJson(PairRequest.Factory.buildPairRequest()));
    }

    private void authorize() {
        String authCode = localSettings.getAuthCode();
        socket.write(gson.toJson(PairRequest.Factory.buildAuthRequest(authCode)));
        serviceEventBus.post(new GpmdpAuthorizedEvent());
    }

    @Override
    public void pin(String pin) {
        socket.write(gson.toJson(PairRequest.Factory.buildPinRequest(pin)));
    }

    @Override
    public void tryAuthorize() {
        if (!isAuthorized()) {
            pair();
        } else {
            authorize();
        }
    }

    @Subscribe
    public void onEvent(String text) {
        GpmdpResponse response = gson.fromJson(text, GpmdpResponse.class);
        switch (response.channel) {
            case API_VERSION:
                serviceEventBus.post((ApiVersionResponse) response);
                break;
            case CONNECT:
                ConnectResponse connectResponse = (ConnectResponse) response;
                if (!((ConnectResponse) response).isPinRequired()) {
                    // Received the auth code
                    localSettings.saveAuthCode(connectResponse.requestCode);
                    authorize();
                }
                break;
            case LYRICS:
                serviceEventBus.post((LyricsResponse) response);
                break;
            case PLAY_STATE:
                serviceEventBus.post((PlayStateResponse) response);
                break;
            case PLAYLISTS:
                serviceEventBus.post((PlaylistsResponse) response);
                break;
            case QUEUE:
                serviceEventBus.post((QueueResponse) response);
                break;
            case RATING:
                serviceEventBus.post((RatingResponse) response);
                break;
            case REPEAT:
                serviceEventBus.post((RepeatResponse) response);
                break;
            case SEARCH_RESULTS:
                serviceEventBus.post((SearchResultsResponse) response);
                break;
            case SHUFFLE:
                serviceEventBus.post((ShuffleResponse) response);
                break;
            case TIME:
                if (System.currentTimeMillis() - lastTimeResponseMs > GPMDP_TIME_DELAY_MS) {
                    lastTimeResponseMs = System.currentTimeMillis();
                    serviceEventBus.post((TimeResponse) response);
                }
                break;
            case TRACK:
                serviceEventBus.post((TrackResponse) response);
                break;
        }
    }

    @Subscribe
    public void onEvent(WebSocketException exception) {
        serviceEventBus.post(new GpmdpErrorEvent(exception));
    }

    @Subscribe
    public void onEvent(WebSocketState state) {
        serviceEventBus.post(new GpmdpStateChangedEvent(state));
    }
}
