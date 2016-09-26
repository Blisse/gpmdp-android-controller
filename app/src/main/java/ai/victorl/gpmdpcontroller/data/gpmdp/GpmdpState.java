package ai.victorl.gpmdpcontroller.data.gpmdp;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ApiVersionResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Rating;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResults;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Shuffle;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Time;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;

public class GpmdpState {
    public ApiVersionResponse apiVersion;
    public SearchResults searchResults;
    public PlaylistsResponse playlists;
    public QueueResponse queue;
    public Repeat repeat;
    public Shuffle shuffle;
    public PlaybackState playbackState;
    public Track currentTrack;
    public LyricsResponse trackLyrics;
    public Rating trackRating;
    public Time trackTime;
}
