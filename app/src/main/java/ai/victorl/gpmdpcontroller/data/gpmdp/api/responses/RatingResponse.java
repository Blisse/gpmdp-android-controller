package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class RatingResponse extends GpmdpResponse {
    @SerializedName("payload")
    public Rating ratingPayload;
}
