package ai.victorl.gpmdpcontroller.data.media;

import android.support.v4.media.MediaBrowserCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Interface to control media.
 */
public interface GpmdpMediaProvider extends GpmdpMediaController {
    String MEDIA_ID_ROOT_QUEUE = "ai.victorl.gpmdpcontroller.root.queue";
    String MEDIA_ID_ROOT_PLAYLISTS = "ai.victorl.gpmdpcontroller.root.playlists";


    /**
     * Get the EventBus to listen for the following events
     * @GpmdpPlayStateChangedEvent
     * @QueueEvent
     * @GpmdpErrorEvent
     * @PlaybackStateCompat
     * @MediaMetadataCompat
     *
     * @return the EventBus to subscribe to.
     */
    EventBus getEventBus();

    /**
     * Start the playback.
     */
    void start();

    /**
     * Stop the playback.
     */
    void stop();

    /**
     * Get a list of media items
     *
     * @param mediaId of the media items to get
     * @return the list of media items associated with the mediaId
     */
    List<MediaBrowserCompat.MediaItem> getChildren(String mediaId);

    /**
     * Get information about a specific media item
     *
     * @param mediaId of the media item
     * @return the media item associated with the mediaId
     */
    MediaBrowserCompat.MediaItem getItem(String mediaId);
}
