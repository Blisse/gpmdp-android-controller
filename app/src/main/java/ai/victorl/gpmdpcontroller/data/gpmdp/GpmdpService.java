package ai.victorl.gpmdpcontroller.data.gpmdp;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.ConnectRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.PlaybackRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ConnectResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlayStateResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RatingResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RepeatResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResultsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ShuffleResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TimeResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class GpmdpService implements GpmdpController {
    private static int GPMDP_TIME_DELAY_MS = 500;

    private final Map<Integer, GpmdpRequestResponseCallback> requestCallbacks = new HashMap<>();
    private final GpmdpLocalSettings localSettings;
    private final Gson gson;
    private final EventBus serviceEventBus;
    private final GpmdpSocket socket = new GpmdpSocket();
    private final GpmdpState state = new GpmdpState();

    private long lastTimeResponseMs = 0;

    public GpmdpService(GpmdpLocalSettings localSettings, Gson gson, EventBus eventBus) {
        this.localSettings = localSettings;
        this.gson = gson;
        this.serviceEventBus = eventBus;

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

    private void sendRequest(GpmdpRequest request) {
        request.requestId = GpmdpRequest.getRequestId();

        if (request.callback != null) {
            requestCallbacks.put(request.requestId, request.callback);
        }
        socket.write(gson.toJson(request));
    }

    @Override
    public void disconnect() {
        socket.disconnect();
    }

    private boolean isAuthorized() {
        return !TextUtils.isEmpty(localSettings.getAuthCode());
    }

    private void pair() {
        sendRequest(ConnectRequest.Factory.buildPairRequest());
    }

    private void authorize() {
        String authCode = localSettings.getAuthCode();
        sendRequest(ConnectRequest.Factory.buildAuthRequest(authCode));
        serviceEventBus.post(new GpmdpAuthorizedEvent());
    }

    @Override
    public void pin(String pin) {
        sendRequest(ConnectRequest.Factory.buildPinRequest(pin));
    }

    @Override
    public void tryAuthorize() {
        if (!isAuthorized()) {
            pair();
        } else {
            authorize();
        }
    }

    @Override
    public void requestState() {
        EventBusUtils.safePost(serviceEventBus, state.apiVersion);
        EventBusUtils.safePost(serviceEventBus, state.currentTrack);
        EventBusUtils.safePost(serviceEventBus, state.trackLyrics);
        EventBusUtils.safePost(serviceEventBus, state.trackRating);
        EventBusUtils.safePost(serviceEventBus, state.trackTime);
        EventBusUtils.safePost(serviceEventBus, state.playbackState);
        EventBusUtils.safePost(serviceEventBus, state.repeat);
        EventBusUtils.safePost(serviceEventBus, state.shuffle);
        EventBusUtils.safePost(serviceEventBus, state.playlists);
        EventBusUtils.safePost(serviceEventBus, state.queue);
        EventBusUtils.safePost(serviceEventBus, state.searchResults);
    }

    @Override
    public void playPause() {
        sendRequest(PlaybackRequest.Factory.playPauseRequest());
    }

    @Override
    public void forward() {
        sendRequest(PlaybackRequest.Factory.forwardRequest());
    }

    @Override
    public void rewind() {
        sendRequest(PlaybackRequest.Factory.rewindRequest());
    }

    @Override
    public void getCurrentTime() {
        sendRequest(PlaybackRequest.Factory.getCurrentTimeRequest());
    }

    @Override
    public void setCurrentTime(int ms) {
        sendRequest(PlaybackRequest.Factory.setCurrentTimeRequest(ms));
    }

    @Override
    public void getPlaybackState() {
        sendRequest(PlaybackRequest.Factory.getPlaybackStateRequest());
    }

    private void onRequestResponse(String text) {
        GpmdpRequestResponse response = gson.fromJson(text, GpmdpRequestResponse.class);

        if (requestCallbacks.containsKey(response.requestId)) {
            GpmdpRequestResponseCallback callback = requestCallbacks.get(response.requestId);
            if (response.type.equals("return")) {
                callback.onSuccess(response, state, serviceEventBus);
            } else {
                callback.onError(response, state, serviceEventBus);
            }
        }
    }

    private void onGpmdpResponse(String text) {
        GpmdpResponse response = gson.fromJson(text, GpmdpResponse.class);
        switch (response.channel) {
            case API_VERSION:
                state.apiVersion = (ApiVersionResponse) response;
                serviceEventBus.post(state.apiVersion);
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
                state.trackLyrics = (LyricsResponse) response;
                serviceEventBus.post(state.trackLyrics);
                break;
            case PLAY_STATE:
                state.playbackState = ((PlayStateResponse) response).playState
                        ? PlaybackState.PLAYING : PlaybackState.PAUSED;
                serviceEventBus.post(state.playbackState);
                break;
            case PLAYLISTS:
                state.playlists = (PlaylistsResponse) response;
                serviceEventBus.post(state.playlists);
                break;
            case QUEUE:
                state.queue = (QueueResponse) response;
                serviceEventBus.post(state.queue);
                break;
            case RATING:
                state.trackRating = ((RatingResponse) response).ratingPayload;
                serviceEventBus.post(state.trackRating);
                break;
            case REPEAT:
                state.repeat = ((RepeatResponse) response).repeat;
                serviceEventBus.post(state.repeat);
                break;
            case SEARCH_RESULTS:
                state.searchResults = ((SearchResultsResponse) response).searchResultsPayload;
                serviceEventBus.post(state.searchResults);
                break;
            case SHUFFLE:
                state.shuffle = ((ShuffleResponse) response).shuffle;
                serviceEventBus.post(state.shuffle);
                break;
            case TIME:
                state.trackTime = ((TimeResponse) response).timePayload;
                if (System.currentTimeMillis() - lastTimeResponseMs > GPMDP_TIME_DELAY_MS) {
                    lastTimeResponseMs = System.currentTimeMillis();
                    serviceEventBus.post(state.trackTime);
                }
                break;
            case TRACK:
                state.currentTrack = ((TrackResponse) response).trackPayload;
                serviceEventBus.post(state.currentTrack);
                break;
            default:
                Log.d("GPMDP", "could not deserialize response.");
                break;
        }
    }

    @Subscribe
    public void onEvent(String text) {
        JsonObject responseJson = new JsonParser().parse(text).getAsJsonObject();
        if (responseJson.has("namespace") && responseJson.get("namespace").getAsString().equals("result")) {
            onRequestResponse(text);
        } else {
            onGpmdpResponse(text);
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
