package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class TimeResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Time timePayload;
}
