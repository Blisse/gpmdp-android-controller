package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;

public class SearchResultsResponse extends GpmdpResponse {
    @SerializedName("payload")
    public SearchResults searchResultsPayload;
}
