package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;

public class ShuffleResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Shuffle shuffle;
}
