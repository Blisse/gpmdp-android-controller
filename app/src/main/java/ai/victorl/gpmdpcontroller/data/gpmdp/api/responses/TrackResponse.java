package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class TrackResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Track trackPayload;
}
