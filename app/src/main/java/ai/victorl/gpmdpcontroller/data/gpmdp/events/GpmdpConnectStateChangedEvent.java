package ai.victorl.gpmdpcontroller.data.gpmdp.events;

import com.neovisionaries.ws.client.WebSocketState;

public class GpmdpConnectStateChangedEvent {
    public WebSocketState state;

    public GpmdpConnectStateChangedEvent(WebSocketState state) {
        this.state = state;
    }
}
