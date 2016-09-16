package ai.victorl.gpmdpcontroller.data.gpmdp;

import android.net.Uri;

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

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpDeserializer;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpConnectStateChangedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpResponseEvent;

public class GpmdpSocket {
    private static int GPMDP_DEFAULT_PORT = 5672;
    private static int GPMDP_TIMEOUT = 5000;

    private final EventBus gpmdpEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();
    private final Gson gpmdpGson = new GsonBuilder()
            .registerTypeAdapter(GpmdpResponse.class, new GpmdpDeserializer())
            .create();

    private WebSocket gpmdpWebSocket;

    public EventBus getEventBus() {
        return gpmdpEventBus;
    }

    public void connect(String gpmdpIpAddress) {
        if (gpmdpWebSocket == null) {
            try {
                String gpmdpUrl = "ws://" + gpmdpIpAddress + ":" + GPMDP_DEFAULT_PORT;
                gpmdpWebSocket = new WebSocketFactory()
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
        if (gpmdpWebSocket != null) {
            gpmdpWebSocket.disconnect();
            gpmdpWebSocket = null;
        }
    }

    public boolean isOpen() {
        return gpmdpWebSocket != null && gpmdpWebSocket.isOpen();
    }

    private class GpmdpSocketListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            GpmdpResponse response = gpmdpGson.fromJson(text, GpmdpResponse.class);
            GpmdpResponseEvent event = new GpmdpResponseEvent(response);
            gpmdpEventBus.post(event);
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
                gpmdpWebSocket = null;
            }

            gpmdpEventBus.post(new GpmdpConnectStateChangedEvent(newState));

        }
    }
}
