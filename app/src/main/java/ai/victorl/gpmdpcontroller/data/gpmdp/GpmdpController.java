package ai.victorl.gpmdpcontroller.data.gpmdp;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Playlist;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;

/**
 *
 */
public interface GpmdpController {
    /**
     *
     * @return
     */
    EventBus getEventBus();

    /**
     *
     */
    void connect();

    /**
     *
     */
    void disconnect();

    boolean connected();

    void pin(String authCode);

    void tryAuthorize();

    void getState();

    void getCurrentTime();

    void setCurrentTime(int ms);

    void playPause();

    void getPlaybackState();

    void forward();

    void rewind();

    void getShuffle();

    void toggleShuffle();

    void getRepeat();

    void toggleRepeat();

    void getQueue();

    void playQueueWithTrack(Track track);

    void getAllPlaylists();

    void playPlaylist(Playlist playlist);

    void playPlaylistWithTrack(Playlist playlist, Track track);
}
