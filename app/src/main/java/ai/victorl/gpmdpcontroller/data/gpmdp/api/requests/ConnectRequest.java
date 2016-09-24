package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import ai.victorl.gpmdpcontroller.utils.StringUtils;

public class ConnectRequest extends GpmdpRequest {
    protected ConnectRequest() {
        super("connect", "connect");
    }

    public static class Factory {
        public static ConnectRequest buildPairRequest() {
            ConnectRequest request = new ConnectRequest();
            request.arguments.add(StringUtils.getDeviceName());
            return request;
        }

        public static ConnectRequest buildPinRequest(String pin) {
            ConnectRequest request = new ConnectRequest();
            request.arguments.add(StringUtils.getDeviceName());
            request.arguments.add(pin);
            return request;
        }

        public static ConnectRequest buildAuthRequest(String authCode) {
            ConnectRequest request = new ConnectRequest();
            request.arguments.add(StringUtils.getDeviceName());
            request.arguments.add(authCode);
            return request;
        }
    }
}
