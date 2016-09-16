package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class PlayStateResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Boolean playState;
}
