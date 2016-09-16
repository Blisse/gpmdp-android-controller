package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import java.util.List;

public class SearchResultsResponse extends GpmdpResponse {
    public String searchText;
    public List<Album> albums;
    public List<Artist> artists;
    public List<Track> tracks;
}
