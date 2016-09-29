package ai.victorl.gpmdpcontroller.data.gpmdp.api;

import java.util.ArrayList;
import java.util.List;

public abstract class GpmdpRequest {
    public static Integer REQUEST_ID = 1;
    public GpmdpRequestResponseCallback callback;

    public String namespace;
    public String method;
    public List<Object> arguments;
    public Integer requestId;

    protected GpmdpRequest(String namespace, String method, GpmdpRequestResponseCallback callback) {
        this.method = method;
        this.namespace = namespace;
        this.arguments = new ArrayList<>();
        this.callback = callback;
    }

    public GpmdpRequest withArgument(Object object) {
        this.arguments.add(object);
        return this;
    }

    public static Integer getRequestId() {
        return REQUEST_ID++;
    }
}
