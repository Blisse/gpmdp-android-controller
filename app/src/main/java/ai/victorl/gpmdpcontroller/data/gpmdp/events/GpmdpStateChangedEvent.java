package ai.victorl.gpmdpcontroller.data.gpmdp.events;

import com.neovisionaries.ws.client.WebSocketState;

public class GpmdpStateChangedEvent {
    public WebSocketState state;

    public GpmdpStateChangedEvent(WebSocketState state) {
        this.state = state;
    }
}
