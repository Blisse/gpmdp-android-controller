package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;

public class QueueRequest extends GpmdpRequest {
    protected QueueRequest(String method, GpmdpRequestResponseCallback callback) {
        super("queue", method, callback);
    }

    protected QueueRequest(String method) {
        this(method, null);
    }

    public static class Factory {
        public static QueueRequest getTracksRequest() {
            return new QueueRequest("getTracks", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.toJsonTree(requestResponse.value).getAsJsonArray();

                    state.queue.queue.clear();

                    for (JsonElement jsonObject : jsonArray) {
                        Track track = gson.fromJson(jsonObject, Track.class);
                        state.queue.queue.add(track);
                    }

                    eventBus.post(state.queue);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static GpmdpRequest playTrackRequest(Track track) {
            return new QueueRequest("playTrack", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            }).withArgument(track);
        }
    }
}
