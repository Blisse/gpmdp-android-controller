package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;

public class RatingRequest extends GpmdpRequest {
    protected RatingRequest(String method, GpmdpRequestResponseCallback callback) {
        super("rating", method, callback);
    }

    protected RatingRequest(String method) {
        this(method, null);
    }

    public static class Factory {
        public static RatingRequest getRatingRequest() {
            return new RatingRequest("getRating", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    Integer rating = Integer.valueOf((String) requestResponse.value);
                    state.trackRating.liked = (rating == 5);
                    state.trackRating.disliked = (rating == 1);
                    eventBus.post(state.trackRating);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static RatingRequest toggleThumbsUpRequest() {
            return new RatingRequest("toggleThumbsUp");
        }

        public static RatingRequest toggleThumbsDownRequest() {
            return new RatingRequest("toggleThumbsDown");
        }
    }
}
