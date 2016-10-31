package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.utils.NetUtils;

public class ConnectRequest extends GpmdpRequest {
    protected ConnectRequest() {
        super("connect", "connect", null);
    }

    public static class Factory {
        public static GpmdpRequest buildPairRequest() {
            return new ConnectRequest()
                    .withArgument(NetUtils.getDeviceName());
        }

        public static GpmdpRequest buildPinRequest(String pin) {
            return new ConnectRequest()
                    .withArgument(NetUtils.getDeviceName())
                    .withArgument(pin);
        }

        public static GpmdpRequest buildAuthRequest(String authCode) {
            return new ConnectRequest()
                    .withArgument(NetUtils.getDeviceName())
                    .withArgument(authCode);
        }
    }
}
