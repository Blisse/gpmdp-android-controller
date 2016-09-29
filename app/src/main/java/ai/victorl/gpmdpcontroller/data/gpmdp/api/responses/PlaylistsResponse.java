package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;

public class PlaylistsResponse extends GpmdpResponse {
    @SerializedName("payload")
    public List<Playlist> playlists;
}
