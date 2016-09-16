package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueueResponse extends GpmdpResponse {
    @SerializedName("payload")
    public List<Track> queue;
}
