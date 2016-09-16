package ai.victorl.gpmdpcontroller.data.gpmdp.events;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpResponse;

public class GpmdpResponseEvent {
    public GpmdpResponse response;

    public GpmdpResponseEvent(GpmdpResponse response) {
        this.response = response;
    }
}
