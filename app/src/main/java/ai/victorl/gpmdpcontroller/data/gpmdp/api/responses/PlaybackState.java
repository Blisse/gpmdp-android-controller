package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

public enum PlaybackState {
    STOPPED(0),
    PAUSED(1),
    PLAYING(2);

    public int value;
    PlaybackState(int value) {
        this.value = value;
    }

    public static PlaybackState fromValue(int value) {
        for (PlaybackState state: PlaybackState.values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("No PlaybackState with value " + value + "found.");
    }
}
