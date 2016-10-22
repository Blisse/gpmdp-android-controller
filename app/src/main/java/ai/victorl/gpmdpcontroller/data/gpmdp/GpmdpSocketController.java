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
import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.PlaylistsRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.requests.QueueRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ConnectResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlayStateResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Playlist;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RatingResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RepeatResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResultsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ShuffleResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TimeResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpPairRequestEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class GpmdpSocketController implements GpmdpController {
    private static final int GPMDP_TIME_DELAY_MS = 500;

    private final Map<Integer, GpmdpRequestResponseCallback> requestCallbacks = new HashMap<>();
    private final EventBus eventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();
    private final GpmdpLocalSettings localSettings;
    private final Gson gson;

    private final GpmdpSocket socket = new GpmdpSocket();
    private final GpmdpState state = new GpmdpState();

    private long lastTimeResponseMs = 0;

    public GpmdpSocketController(GpmdpLocalSettings localSettings, Gson gson) {
        this.localSettings = localSettings;
        this.gson = gson;

        EventBusUtils.safeRegister(socket.getEventBus(), this);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
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

    @Override
    public boolean connected() {
        return socket.isOpen();
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
        eventBus.post(new GpmdpAuthorizedEvent());
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
    public void getState() {
        EventBusUtils.safePost(eventBus, state.apiVersion);
        EventBusUtils.safePost(eventBus, state.searchResults);
        EventBusUtils.safePost(eventBus, state.playlists);
        EventBusUtils.safePost(eventBus, state.queue);
        EventBusUtils.safePost(eventBus, state.repeat);
        EventBusUtils.safePost(eventBus, state.shuffle);
        EventBusUtils.safePost(eventBus, state.playbackState);
        EventBusUtils.safePost(eventBus, state.trackTime);
        EventBusUtils.safePost(eventBus, state.trackLyrics);
        EventBusUtils.safePost(eventBus, state.trackRating);
        EventBusUtils.safePost(eventBus, state.currentTrack);
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
    public void playPause() {
        sendRequest(PlaybackRequest.Factory.playPauseRequest());
    }

    @Override
    public void getPlaybackState() {
        sendRequest(PlaybackRequest.Factory.getPlaybackStateRequest());
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
    public void getShuffle() {
        sendRequest(PlaybackRequest.Factory.getShuffleRequest());
    }

    @Override
    public void toggleShuffle() {
        sendRequest(PlaybackRequest.Factory.toggleShuffleRequest());
    }

    @Override
    public void getRepeat() {
        sendRequest(PlaybackRequest.Factory.getRepeatRequest());
    }

    @Override
    public void toggleRepeat() {
        sendRequest(PlaybackRequest.Factory.toggleRepeatRequest());
    }

    @Override
    public void getQueue() {
        sendRequest(QueueRequest.Factory.getTracksRequest());
    }

    @Override
    public void playQueueWithTrack(Track track) {
        sendRequest(QueueRequest.Factory.playTrackRequest(track));
    }

    @Override
    public void getAllPlaylists() {
        sendRequest(PlaylistsRequest.Factory.getAllPlaylistsRequest());
    }

    @Override
    public void playPlaylist(Playlist playlist) {
        sendRequest(PlaylistsRequest.Factory.playRequest(playlist));
    }

    @Override
    public void playPlaylistWithTrack(Playlist playlist, Track track) {
        sendRequest(PlaylistsRequest.Factory.playWithTrackRequest(playlist, track));
    }

    private void onRequestResponse(String text) {
        GpmdpRequestResponse response = gson.fromJson(text, GpmdpRequestResponse.class);

        if (requestCallbacks.containsKey(response.requestId)) {
            GpmdpRequestResponseCallback callback = requestCallbacks.get(response.requestId);
            if (response.type.equals("return")) {
                callback.onSuccess(response, state, eventBus);
            } else {
                callback.onError(response, state, eventBus);
            }
        }
    }

    private void onGpmdpResponse(String text) {
        GpmdpResponse response = gson.fromJson(text, GpmdpResponse.class);
        switch (response.channel) {
            case API_VERSION:
                state.apiVersion = (ApiVersionResponse) response;
                eventBus.post(state.apiVersion);
                break;
            case CONNECT:
                ConnectResponse connectResponse = (ConnectResponse) response;
                if (!((ConnectResponse) response).isPinRequired()) {
                    // Received the auth code
                    localSettings.saveAuthCode(connectResponse.requestCode);
                    authorize();
                } else {
                    eventBus.post(new GpmdpPairRequestEvent());
                }
                break;
            case LYRICS:
                state.trackLyrics = (LyricsResponse) response;
                eventBus.post(state.trackLyrics);
                break;
            case PLAY_STATE:
                state.playbackState = ((PlayStateResponse) response).playState
                        ? PlaybackState.PLAYING : PlaybackState.PAUSED;
                eventBus.post(state.playbackState);
                break;
            case PLAYLISTS:
                state.playlists = (PlaylistsResponse) response;
                eventBus.post(state.playlists);
                break;
            case QUEUE:
                state.queue = (QueueResponse) response;
                eventBus.post(state.queue);
                break;
            case RATING:
                state.trackRating = ((RatingResponse) response).ratingPayload;
                eventBus.post(state.trackRating);
                break;
            case REPEAT:
                state.repeat = ((RepeatResponse) response).repeat;
                eventBus.post(state.repeat);
                break;
            case SEARCH_RESULTS:
                state.searchResults = ((SearchResultsResponse) response).searchResultsPayload;
                eventBus.post(state.searchResults);
                break;
            case SHUFFLE:
                state.shuffle = ((ShuffleResponse) response).shuffle;
                eventBus.post(state.shuffle);
                break;
            case TIME:
                state.trackTime = ((TimeResponse) response).timePayload;
                state.trackTime.current /= 1000;
                state.trackTime.total /= 1000;
                if (System.currentTimeMillis() - lastTimeResponseMs > GPMDP_TIME_DELAY_MS) {
                    lastTimeResponseMs = System.currentTimeMillis();
                    eventBus.post(state.trackTime);
                }
                break;
            case TRACK:
                state.currentTrack = ((TrackResponse) response).trackPayload;
                eventBus.post(state.currentTrack);
                break;
            default:
                Log.d("GPMDP", "Did not handle response with channel: " + response.channel);
                break;
        }
    }

    @Subscribe
    public void onEvent(String text) {
        if (!TextUtils.isEmpty(text)) {
            JsonObject responseJson = new JsonParser().parse(text).getAsJsonObject();
            if (responseJson.has("namespace") && TextUtils.equals(responseJson.get("namespace").getAsString(), ("result"))) {
                onRequestResponse(text);
            } else {
                onGpmdpResponse(text);
            }
        }
    }

    @Subscribe
    public void onEvent(WebSocketException exception) {
        eventBus.post(new GpmdpErrorEvent(exception));
    }

    @Subscribe
    public void onEvent(WebSocketState state) {
        eventBus.postSticky(new GpmdpStateChangedEvent(state));
    }
}
