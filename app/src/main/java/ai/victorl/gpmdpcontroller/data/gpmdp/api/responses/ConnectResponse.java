package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public class ConnectResponse extends GpmdpResponse {
    @SerializedName("payload")
    String requestCode;
}
