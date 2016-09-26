package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;

public class ConnectResponse extends GpmdpResponse {
    @SerializedName("payload")
    public String requestCode;

    public boolean isPinRequired() {
        return "CODE_REQUIRED".equals(requestCode);
    }
}
