package ai.victorl.gpmdpcontroller.data.gpmdp.events;

public class GpmdpErrorEvent {
    public String errorMessage;

    public GpmdpErrorEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
