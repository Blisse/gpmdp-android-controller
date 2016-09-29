package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Shuffle;

public class PlaybackRequest extends GpmdpRequest {
    protected PlaybackRequest(String method, GpmdpRequestResponseCallback callback) {
        super("playback", method, callback);
    }

    protected PlaybackRequest(String method) {
        this(method, null);
    }

    public static class Factory {
        public static PlaybackRequest getCurrentTimeRequest() {
            return new PlaybackRequest("getCurrentTime", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.trackTime.current = (Integer) requestResponse.value;
                    eventBus.post(state.trackTime);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static GpmdpRequest setCurrentTimeRequest(int ms) {
            return new PlaybackRequest("setCurrentTime")
                    .withArgument(ms);
        }

        public static PlaybackRequest getTotalTimeRequest() {
            return new PlaybackRequest("getTotalTime", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.trackTime.total = (Integer) requestResponse.value;
                    eventBus.post(state.trackTime);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static PlaybackRequest playPauseRequest() {
            return new PlaybackRequest("playPause");
        }

        public static PlaybackRequest getPlaybackStateRequest() {
            return new PlaybackRequest("getPlaybackState", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.playbackState = PlaybackState.fromValue(((Double) requestResponse.value).intValue());
                    eventBus.post(state.playbackState);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static PlaybackRequest forwardRequest() {
            return new PlaybackRequest("forward");
        }

        public static PlaybackRequest rewindRequest() {
            return new PlaybackRequest("rewind");
        }

        public static PlaybackRequest getShuffleRequest() {
            return new PlaybackRequest("getShuffle", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.shuffle = Shuffle.valueOf((String) requestResponse.value);
                    eventBus.post(state.shuffle);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static PlaybackRequest toggleShuffleRequest() {
            return new PlaybackRequest("toggleShuffle");
        }

        public static PlaybackRequest getRepeatRequest() {
            return new PlaybackRequest("getRepeat", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.repeat = Repeat.valueOf((String) requestResponse.value);
                    eventBus.post(state.repeat);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static PlaybackRequest toggleRepeatRequest() {
            return new PlaybackRequest("toggleRepeat");
        }
    }
}
