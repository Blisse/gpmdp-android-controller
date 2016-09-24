package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

public class PlaybackRequest extends GpmdpRequest {
    protected PlaybackRequest(String method) {
        super("playback", method);
    }

    public static class Factory {
        public static PlaybackRequest setCurrentTimeRequest(int ms) {
            PlaybackRequest request = new PlaybackRequest("setCurrentTime");
            request.arguments.add(ms);
            return request;
        }

        public static PlaybackRequest playPauseRequest() {
            return new PlaybackRequest("playPause");
        }

        public static PlaybackRequest forwardRequest() {
            return new PlaybackRequest("forward");
        }

        public static PlaybackRequest rewindRequest() {
            return new PlaybackRequest("rewind");
        }
    }
}
