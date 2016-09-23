package ai.victorl.gpmdpcontroller.data.gpmdp;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlayStateResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RatingResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RepeatResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResultsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ShuffleResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TimeResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;

public class GpmdpState {
    ApiVersionResponse apiVersion;
    LyricsResponse lyrics;
    PlayStateResponse playState;
    PlaylistsResponse playlists;
    QueueResponse queue;
    RatingResponse rating;
    RepeatResponse repeat;
    SearchResultsResponse searchResults;
    ShuffleResponse shuffle;
    TimeResponse time;
    TrackResponse track;
}
