package ai.victorl.gpmdpcontroller.data.gpmdp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ConnectResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpDeserializer;
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
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpConnectStateChangedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;

public class GpmdpSocket {
    private static int GPMDP_DEFAULT_PORT = 5672;
    private static int GPMDP_TIMEOUT = 5000;
    private static int GPMDP_TIME_DELAY_MS = 500;

    private final EventBus gpmdpEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(GpmdpResponse.class, new GpmdpDeserializer())
            .create();

    private long lastTimeResponseMs = 0;
    private WebSocket webSocket;

    public EventBus getEventBus() {
        return gpmdpEventBus;
    }

    public void connect(String gpmdpIpAddress) {
        if (webSocket == null) {
            try {
                String gpmdpUrl = "ws://" + gpmdpIpAddress + ":" + GPMDP_DEFAULT_PORT;
                webSocket = new WebSocketFactory()
                        .setConnectionTimeout(GPMDP_TIMEOUT)
                        .createSocket(gpmdpUrl)
                        .addListener(new GpmdpSocketListener())
                        .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                        .connectAsynchronously();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.disconnect();
            webSocket = null;
        }
    }

    public boolean isOpen() {
        return webSocket != null && webSocket.isOpen();
    }

    public void write(Object data) {
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(gson.toJson(data), true);
        }
    }

    private class GpmdpSocketListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            GpmdpResponse response = gson.fromJson(text, GpmdpResponse.class);
            switch (response.channel) {
                case API_VERSION:
                    gpmdpEventBus.post((ApiVersionResponse) response);
                    break;
                case CONNECT:
                    if (System.currentTimeMillis() - lastTimeResponseMs > GPMDP_TIME_DELAY_MS) {
                        lastTimeResponseMs = System.currentTimeMillis();
                        gpmdpEventBus.post((ConnectResponse) response);
                    }
                    break;
                case LYRICS:
                    gpmdpEventBus.post((LyricsResponse) response);
                    break;
                case PLAY_STATE:
                    gpmdpEventBus.post((PlayStateResponse) response);
                    break;
                case PLAYLISTS:
                    gpmdpEventBus.post((PlaylistsResponse) response);
                    break;
                case QUEUE:
                    gpmdpEventBus.post((QueueResponse) response);
                    break;
                case RATING:
                    gpmdpEventBus.post((RatingResponse) response);
                    break;
                case REPEAT:
                    gpmdpEventBus.post((RepeatResponse) response);
                    break;
                case SEARCH_RESULTS:
                    gpmdpEventBus.post((SearchResultsResponse) response);
                    break;
                case SHUFFLE:
                    gpmdpEventBus.post((ShuffleResponse) response);
                    break;
                case TIME:
                    gpmdpEventBus.post((TimeResponse) response);
                    break;
                case TRACK:
                    gpmdpEventBus.post((TrackResponse) response);
                    break;
            }
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
            gpmdpEventBus.post(new GpmdpErrorEvent(cause.getMessage()));
        }

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
            super.onStateChanged(websocket, newState);
            if (newState == WebSocketState.CLOSED) {
                webSocket = null;
            }

            gpmdpEventBus.post(new GpmdpConnectStateChangedEvent(newState));
        }
    }
}
