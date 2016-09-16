package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class ShuffleResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Shuffle shuffle;
}
