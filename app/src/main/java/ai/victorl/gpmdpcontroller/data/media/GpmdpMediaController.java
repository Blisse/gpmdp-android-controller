package ai.victorl.gpmdpcontroller.data.media;

public interface GpmdpMediaController {

    /**
     * Update the current state
     */
    void getState();

    /**
     * Get the current PlaybackStateCompat value
     *
     * @return the playback state
     */
    int getPlaybackState();

    /**
     * Check the connection to the Google Play Music Desktop Player
     *
     * @return boolean that indicates that this is ready to be used.
     */
    boolean isConnected();

    /**
     * Check if the player is playing
     *
     * @return boolean indicating whether the player is playing
     */
    boolean isPlaying();

    /**
     * Play the specific queue item
     *
     * @param id of queue item to play
     */
    void play(long id);

    /**
     * Toggle the current play state
     */
    void playPause();

    /**
     * Play the currently playing item
     */
    void play();

    /**
     * Pause the currently playing item
     */
    void pause();

    /**
     * Skip to the next item
     */
    void skipToNext();

    /**
     * Skip to the previous item
     */
    void skipToPrevious();

    /**
     * Seek to the given position
     *
     * @param position to seek to.
     */
    void seekTo(int position);

    /**
     * Toggle shuffle
     */
    void toggleShuffle();

    /**
     * Toggle repeat
     */
    void toggleRepeat();
}
