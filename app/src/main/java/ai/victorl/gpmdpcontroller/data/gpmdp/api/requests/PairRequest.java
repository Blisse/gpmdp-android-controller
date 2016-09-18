package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import java.util.ArrayList;

import ai.victorl.gpmdpcontroller.utils.StringUtils;

public class PairRequest extends GpmdpRequest {
    private static String PAIR_REQUEST_MESSAGE = "connect";

    public PairRequest() {
        namespace = "connect";
        method = "connect";
        arguments = new ArrayList<>();
        arguments.add(StringUtils.getDeviceName());
    }

    public PairRequest(String response) {
        this();
        arguments.add(response);
    }
}
