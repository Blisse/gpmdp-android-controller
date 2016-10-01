package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

public class Track {
    public String id;
    public Integer index;
    public String title;
    public String artist;
    public String artistId;
    public String artistImage;
    public String album;
    public String albumArt;
    public String albumArtist;
    public Integer duration;
    public Integer playCount;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Track) {
            Track otherTrack = (Track) obj;
            return title.equals(otherTrack.title)
                    && artist.equals(otherTrack.artist)
                    && album.equals(otherTrack.album);
        }
        return super.equals(obj);
    }
}
