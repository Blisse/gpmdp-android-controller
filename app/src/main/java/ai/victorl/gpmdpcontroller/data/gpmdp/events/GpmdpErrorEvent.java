package ai.victorl.gpmdpcontroller.data.gpmdp.events;

import com.neovisionaries.ws.client.WebSocketException;

public class GpmdpErrorEvent {
    public WebSocketException error;

    public GpmdpErrorEvent(WebSocketException error) {
        this.error = error;
    }
}
