package ai.victorl.gpmdpcontroller.data.gpmdp.api;

import com.google.gson.annotations.SerializedName;

public class GpmdpRequestResponse {
    public String namespace;
    public String type;
    public Object value;
    @SerializedName("requestID")
    public Integer requestId;
}
