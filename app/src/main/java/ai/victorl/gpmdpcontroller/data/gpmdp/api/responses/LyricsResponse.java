package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class LyricsResponse extends GpmdpResponse {
    @SerializedName("payload")
    public String lyrics;
}
