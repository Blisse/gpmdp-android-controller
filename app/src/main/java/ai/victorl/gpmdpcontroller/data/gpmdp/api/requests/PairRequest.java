package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import java.util.ArrayList;

import ai.victorl.gpmdpcontroller.utils.StringUtils;

public class PairRequest extends GpmdpRequest {
    private static String PAIR_REQUEST_MESSAGE = "connect";

    protected PairRequest() {
        namespace = "connect";
        method = "connect";
        arguments = new ArrayList<>();
    }

    public static class Factory {
        public static PairRequest buildPairRequest() {
            PairRequest request = new PairRequest();
            request.arguments.add(StringUtils.getDeviceName());
            return request;
        }

        public static PairRequest buildPinRequest(String pin) {
            PairRequest request = new PairRequest();
            request.arguments.add(StringUtils.getDeviceName());
            request.arguments.add(pin);
            return request;
        }

        public static PairRequest buildAuthRequest(String authCode) {
            PairRequest request = new PairRequest();
            request.arguments.add(StringUtils.getDeviceName());
            request.arguments.add(authCode);
            return request;
        }
    }
}
