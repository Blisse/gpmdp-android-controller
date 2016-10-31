package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;

public class VolumeRequest extends GpmdpRequest {
    protected VolumeRequest(String method, GpmdpRequestResponseCallback callback) {
        super("volume", method, callback);
    }

    protected VolumeRequest(String method) {
        this(method, null);
    }

    public static class Factory {
        public static VolumeRequest getVolumeRequest() {
            return new VolumeRequest("getVolume", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static VolumeRequest setVolumeRequest(int volume) {
            VolumeRequest request = new VolumeRequest("setVolume");
            request.arguments.add(volume);
            return request;
        }

        public static VolumeRequest increaseVolumeRequest() {
            return new VolumeRequest("increaseVolume");
        }

        public static VolumeRequest increaseVolumeRequest(int volume) {
            VolumeRequest request = new VolumeRequest("increaseVolume");
            request.arguments.add(volume);
            return request;
        }

        public static VolumeRequest decreaseVolumeRequest() {
            return new VolumeRequest("decreaseVolume");
        }

        public static VolumeRequest decreaseVolumeRequest(int volume) {
            VolumeRequest request = new VolumeRequest("decreaseVolume");
            request.arguments.add(volume);
            return request;
        }
    }
}
