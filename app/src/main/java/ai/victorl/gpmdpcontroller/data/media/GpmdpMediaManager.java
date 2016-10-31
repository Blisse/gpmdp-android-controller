package ai.victorl.gpmdpcontroller.data.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.LyricsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Playlist;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaylistsResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Rating;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.SearchResults;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Shuffle;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Time;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.data.media.actions.RefreshAction;
import ai.victorl.gpmdpcontroller.data.media.actions.RepeatAction;
import ai.victorl.gpmdpcontroller.data.media.actions.ShuffleAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeDownAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeUpAction;
import ai.victorl.gpmdpcontroller.data.media.events.QueueEvent;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import ai.victorl.gpmdpcontroller.utils.MediaIdHelper;

/**
 * Adapt GpmdpController events for GpmdpMediaService.
 */
public class GpmdpMediaManager implements GpmdpMediaProvider {
    private static final String GPMDP_ACTION_REPEAT = "ai.victorl.gpmdpcontroller.ACTION_REPEAT";
    private static final String GPMDP_ACTION_SHUFFLE = "ai.victorl.gpmdpcontroller.ACTION_SHUFFLE";

    private final Context context;
    private final GpmdpController gpmdpController;
    private final EventBus mediaEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();

    private PlaybackState playbackState = PlaybackState.STOPPED;
    private Time currentTrackTime;
    private LyricsResponse currentTrackLyrics;

    private List<Track> queueTracks = new ArrayList<>();
    private List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();

    private Map<String, List<MediaSessionCompat.QueueItem>> playlists = new HashMap<>();
    private Map<String, MediaDescriptionCompat> playlistDescriptions = new HashMap<>();

    private GpmdpMediaExtras.Builder gpmdpExtrasBuilder = new GpmdpMediaExtras.Builder();
    private PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();
    private MediaMetadataCompat.Builder mediaMetadataBuilder = new MediaMetadataCompat.Builder();

    public GpmdpMediaManager(Context context, GpmdpController gpmdpController) {
        this.context = context;
        this.gpmdpController = gpmdpController;

        playbackStateBuilder.addCustomAction(new RefreshAction(context).getAction());
        playbackStateBuilder.addCustomAction(new RepeatAction(context).getAction());
        playbackStateBuilder.addCustomAction(new ShuffleAction(context).getAction());
        playbackStateBuilder.addCustomAction(new VolumeUpAction(context).getAction());
        playbackStateBuilder.addCustomAction(new VolumeDownAction(context).getAction());
    }

    @Override
    public EventBus getEventBus() {
        return mediaEventBus;
    }

    @Override
    public void start() {
        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);
    }

    @Override
    public void stop() {
        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Override
    public void getState() {
        gpmdpController.getState();
    }

    @Override
    public int getPlaybackState() {
        if (gpmdpController.connected()) {
            switch (playbackState) {
                case STOPPED:
                    return PlaybackStateCompat.STATE_STOPPED;
                case PAUSED:
                    return PlaybackStateCompat.STATE_PAUSED;
                case PLAYING:
                    return PlaybackStateCompat.STATE_PLAYING;
                default:
                    throw new IllegalArgumentException("Unhandled playback state.");
            }
        }
        return PlaybackStateCompat.STATE_NONE;
    }

    @Override
    public boolean isConnected() {
        return gpmdpController.connected();
    }

    @Override
    public boolean isPlaying() {
        return (playbackState == PlaybackState.PLAYING);
    }

    @Override
    public void play(int queueId) {
        if (0 <= queueId && queueId < queueTracks.size()) {
            gpmdpController.playQueueWithTrack(queueTracks.get(queueId));
        }
    }

    @Override
    public void playPause() {
        gpmdpController.playPause();
    }

    @Override
    public void play() {
        gpmdpController.playPause();
    }

    @Override
    public void pause() {
        gpmdpController.playPause();
    }

    @Override
    public void skipToNext() {
        gpmdpController.forward();
    }

    @Override
    public void skipToPrevious() {
        gpmdpController.rewind();
    }

    @Override
    public void seekTo(int position) {
        gpmdpController.setCurrentTime(position);
    }

    @Override
    public void toggleShuffle() {
        gpmdpController.toggleShuffle();
    }

    @Override
    public void toggleRepeat() {
        gpmdpController.toggleRepeat();
    }

    @Override
    public void setVolume(int i) {
        gpmdpController.setVolume(i);
    }

    @Override
    public void increaseVolume() {
        gpmdpController.increaseVolume();
    }

    @Override
    public void decreaseVolume() {
        gpmdpController.decreaseVolume();
    }

    @Override
    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (mediaId.equals(MEDIA_ID_ROOT_QUEUE)) {
            for (MediaSessionCompat.QueueItem queueItem : queue) {
                mediaItems.add(createMediaItem(queueItem.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
        } else if (mediaId.equals(MEDIA_ID_ROOT_PLAYLISTS)) {
            for (String playlistKey : playlistDescriptions.keySet()) {
                mediaItems.add(createMediaItem(playlistDescriptions.get(playlistKey), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
            }
        } else if (mediaId.startsWith(MEDIA_ID_ROOT_PLAYLISTS)) {
            String playlistId = MediaIdHelper.extractMusicIdFromMediaId(mediaId);
            if (playlists.containsKey(playlistId)) {
                for (MediaSessionCompat.QueueItem queueItem : playlists.get(playlistId)) {
                    mediaItems.add(createMediaItem(queueItem.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                }
            }
        }

        return mediaItems;
    }

    @Override
    public MediaBrowserCompat.MediaItem getItem(String mediaId) {
        return null;
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaDescriptionCompat description, int flags) {
        return new MediaBrowserCompat.MediaItem(description, flags);
    }

    private MediaSessionCompat.QueueItem createQueueItem(MediaDescriptionCompat description, long id) {
        return new MediaSessionCompat.QueueItem(description, id);
    }

    private MediaMetadataCompat createMediaMetadata(String mediaId, Track track) {
        return new  MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.albumArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, String.format(Locale.CANADA, "%s - %s", track.artist, track.album))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, track.albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, track.duration.toString()) // hack to get duration on queue item
                .build();
    }

    private long getActions(PlaybackState playbackState) {
        long actions = PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM |
                PlaybackStateCompat.ACTION_SEEK_TO |
                PlaybackStateCompat.ACTION_SET_RATING;

        if (playbackState == PlaybackState.PLAYING) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }

        return actions;
    }

    private RatingCompat getRating(Rating currentTrackRating) {
        int rating = 0;
        if (currentTrackRating != null) {
            if (currentTrackRating.liked) {
                rating = 5;
            } else if (currentTrackRating.disliked) {
                rating = 1;
            }
        }
        return RatingCompat.newStarRating(RatingCompat.RATING_5_STARS, rating);
    }

    private Target mediaAlbumArtTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mediaMetadataBuilder
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap);

            mediaEventBus.post(mediaMetadataBuilder.build());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Subscribe
    public void onEvent(PlaybackState playbackState) {
        this.playbackState = playbackState;

        playbackStateBuilder.setActions(getActions(playbackState));
        if (currentTrackTime != null && currentTrackTime.current != null) {
            playbackStateBuilder.setState(getPlaybackState(), currentTrackTime.current, 1.0f);
        }

        mediaEventBus.post(playbackStateBuilder.build());
    }

    @Subscribe
    public void onEvent(LyricsResponse lyricsResponse) {
        // TODO: Display lyrics
        this.currentTrackLyrics = lyricsResponse;
    }

    @Subscribe
    public void onEvent(PlaylistsResponse playlistsResponse) {
        playlists.clear();
        playlistDescriptions.clear();

        for (Playlist playlist : playlistsResponse.playlists) {
            List<MediaSessionCompat.QueueItem> playlistTracks = new ArrayList<>();
            for (Track track : playlist.tracks) {
                String mediaId = MediaIdHelper.createMediaId(track.id, MEDIA_ID_ROOT_PLAYLISTS, playlist.id);
                MediaMetadataCompat mediaMetadata = createMediaMetadata(mediaId, track);
                MediaSessionCompat.QueueItem item = createQueueItem(mediaMetadata.getDescription(), track.index);
                playlistTracks.add(item);
            }
            playlists.put(playlist.id, playlistTracks);

            String mediaId = MediaIdHelper.createMediaId(playlist.id, MEDIA_ID_ROOT_PLAYLISTS);
            MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                    .setMediaId(mediaId)
                    .setTitle(playlist.name)
                    .setSubtitle(playlist.tracks.size() + " " + (playlist.tracks.size() == 1 ? "song" : "songs"))
                    .build();

            playlistDescriptions.put(playlist.id, description);
        }
    }

    @Subscribe
    public void onEvent(QueueResponse queueResponse) {
        queue.clear();

        for (Track track : queueResponse.queue) {
            String mediaId = MediaIdHelper.createMediaId(track.id, MEDIA_ID_ROOT_QUEUE);
            MediaMetadataCompat mediaMetadata = createMediaMetadata(mediaId, track);
            MediaSessionCompat.QueueItem item = createQueueItem(mediaMetadata.getDescription(), track.index - 1);
            queue.add(item);
        }

        queueTracks = new ArrayList<>(queueResponse.queue);

        mediaEventBus.post(new QueueEvent(context.getResources().getString(R.string.controller_queue_name), queue));
    }

    @Subscribe
    public void onEvent(Rating rating) {
        mediaMetadataBuilder.putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, getRating(rating));
        mediaEventBus.post(mediaMetadataBuilder.build());
    }

    @Subscribe
    public void onEvent(Repeat repeat) {
        gpmdpExtrasBuilder.withRepeat(repeat);
        playbackStateBuilder.setExtras(gpmdpExtrasBuilder.build().getBundle());
        mediaEventBus.post(playbackStateBuilder.build());
    }

    @Subscribe
    public void onEvent(SearchResults searchResults) {
        // TODO: Implement search
    }

    @Subscribe
    public void onEvent(Shuffle shuffle) {
        gpmdpExtrasBuilder.withShuffle(shuffle);
        playbackStateBuilder.setExtras(gpmdpExtrasBuilder.build().getBundle());
        mediaEventBus.post(playbackStateBuilder.build());
    }

    @Subscribe
    public void onEvent(Time time) {
        currentTrackTime = time;
        playbackStateBuilder.setState(getPlaybackState(), time.current, 1.0f);
        mediaEventBus.post(playbackStateBuilder.build());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Track track) {
        String trackSubtitle = String.format(Locale.CANADA, "%s - %s", track.artist, track.album);
        String mediaId = MediaIdHelper.createMediaId(track.id);
        mediaMetadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.albumArtist)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, trackSubtitle)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, track.albumArt);

        if (currentTrackTime != null && currentTrackTime.total != null) {
            mediaMetadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentTrackTime.total);
        }

        MediaMetadataCompat mediaMetadata = mediaMetadataBuilder.build();
        MediaDescriptionCompat mediaDescription = mediaMetadata.getDescription();
        mediaEventBus.post(mediaMetadata);

        Picasso.with(context).load(track.albumArt).into(mediaAlbumArtTarget);

        for (MediaSessionCompat.QueueItem queueItem : queue) {
            MediaDescriptionCompat description = queueItem.getDescription();
            if (TextUtils.equals(description.getTitle(), mediaDescription.getTitle())
                    && TextUtils.equals(description.getSubtitle(), mediaDescription.getSubtitle())) {
                playbackStateBuilder.setActiveQueueItemId(queueItem.getQueueId());
                break;
            }
        }

        playbackStateBuilder.setState(getPlaybackState(), 0, 1.0f);
        mediaEventBus.post(playbackStateBuilder.build());
    }

    @Subscribe
    public void onEvent(GpmdpErrorEvent event) {
        Log.d("GPMDP", "GpmdpErrorEvent received with: " + event.error.getError().toString());
        mediaEventBus.post(event);
    }

    @Subscribe
    public void onEvent(GpmdpStateChangedEvent event) {
        mediaEventBus.postSticky(event);
    }
}
