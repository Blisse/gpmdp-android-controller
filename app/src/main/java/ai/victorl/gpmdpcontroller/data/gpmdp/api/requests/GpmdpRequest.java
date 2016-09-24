package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import java.util.ArrayList;
import java.util.List;

public abstract class GpmdpRequest {
    public String namespace;
    public String method;
    public List<Object> arguments;

    protected GpmdpRequest(String namespace, String method) {
        this.namespace = namespace;
        this.method = method;
        this.arguments = new ArrayList<>();
    }
}
