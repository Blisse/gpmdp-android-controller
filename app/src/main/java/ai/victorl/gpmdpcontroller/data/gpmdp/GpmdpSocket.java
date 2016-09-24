package ai.victorl.gpmdpcontroller.data.gpmdp;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class GpmdpSocket {
    private static int GPMDP_DEFAULT_PORT = 5672;
    private static int GPMDP_TIMEOUT = 5000;

    private final EventBus socketEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();

    private WebSocket webSocket;

    public EventBus getEventBus() {
        return socketEventBus;
    }

    public void connect(String gpmdpIpAddress) {
        if (webSocket != null) {
            disconnect();
        }

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

    public void disconnect() {
        if (webSocket != null) {
            webSocket.disconnect();
            webSocket = null;
        }
    }

    public boolean isOpen() {
        return webSocket != null && webSocket.isOpen();
    }

    public void write(String data) {
        if (isOpen()) {
            webSocket.sendText(data, true);
        }
    }

    private class GpmdpSocketListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            socketEventBus.post(text);
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
            socketEventBus.post(cause.getMessage());
        }

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
            super.onStateChanged(websocket, newState);
            socketEventBus.post(newState);
        }
    }
}
