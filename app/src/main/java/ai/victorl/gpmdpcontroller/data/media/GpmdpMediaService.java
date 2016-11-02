package ai.victorl.gpmdpcontroller.data.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpErrorEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.data.media.actions.RefreshAction;
import ai.victorl.gpmdpcontroller.data.media.actions.RepeatAction;
import ai.victorl.gpmdpcontroller.data.media.actions.ShuffleAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeDownAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeUpAction;
import ai.victorl.gpmdpcontroller.data.media.events.QueueEvent;
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import ai.victorl.gpmdpcontroller.ui.activities.ConnectActivity;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;

import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;

public class GpmdpMediaService extends MediaBrowserServiceCompat {
    @Inject @ApplicationScope Context context;
    @Inject GpmdpMediaProvider gpmdpMediaProvider;

    private MediaSessionCompat mediaSession;
    private GpmdpMediaNotification mediaNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.applicationComponent(getApplicationContext()).inject(this);

        mediaSession = new MediaSessionCompat(context, GpmdpMediaService.class.getSimpleName());
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setRatingType(RatingCompat.RATING_THUMB_UP_DOWN);

        MediaMetadataCompat mediaMetadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "0")
                .build();
        mediaSession.setMetadata(mediaMetadata);

        PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build();
        mediaSession.setPlaybackState(playbackState);
        mediaSession.setActive(true);

        Intent intent = new Intent(context, ConnectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pendingIntent);

        try {
            mediaNotification = new GpmdpMediaNotification(this, mediaSession.getSessionToken());
            mediaNotification.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        EventBusUtils.safeRegister(gpmdpMediaProvider.getEventBus(), this);
        gpmdpMediaProvider.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaNotification != null) {
            mediaNotification.stop();
        }

        mediaSession.setActive(false);
        mediaSession.release();
        mediaSession = null;

        EventBusUtils.safeUnregister(gpmdpMediaProvider.getEventBus(), this);
        gpmdpMediaProvider.stop();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(GpmdpMediaProvider.MEDIA_ID_ROOT_QUEUE, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(gpmdpMediaProvider.getChildren(parentId));
    }

    @Override
    public void onLoadItem(String itemId, Result<MediaBrowserCompat.MediaItem> result) {
        super.onLoadItem(itemId, result);
    }

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            gpmdpMediaProvider.play();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            gpmdpMediaProvider.play(Long.valueOf(id).intValue());
        }

        @Override
        public void onPause() {
            super.onPause();
            gpmdpMediaProvider.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            gpmdpMediaProvider.skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            gpmdpMediaProvider.skipToPrevious();
        }

        @Override
        public void onSetRating(RatingCompat rating) {
            super.onSetRating(rating);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);

            if (action.equals(RepeatAction.getName())) {
                gpmdpMediaProvider.toggleRepeat();
            } else if (action.equals(ShuffleAction.getName())) {
                gpmdpMediaProvider.toggleShuffle();
            } else if (action.equals(RefreshAction.getName())) {
                gpmdpMediaProvider.getState();
            } else if (action.equals(VolumeUpAction.getName())) {
                gpmdpMediaProvider.increaseVolume();
            } else if (action.equals(VolumeDownAction.getName())) {
                gpmdpMediaProvider.decreaseVolume();
            }
        }
    };

    @Subscribe
    public void onEvent(GpmdpErrorEvent event) {
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                .setErrorMessage(event.error.toString())
                .build();
        mediaSession.setPlaybackState(state);
    }

    @Subscribe
    public void onEvent(GpmdpStateChangedEvent event) {
        if (event.state == WebSocketState.OPEN) {
            mediaSession.setActive(true);
        } else if (event.state == WebSocketState.CLOSED) {
            mediaSession.setActive(false);
            PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_ERROR, 0, 1.0f)
                    .setErrorMessage("WebSocket connection closed.")
                    .build();
            mediaSession.setPlaybackState(state);
        }
    }

    @Subscribe
    public void onEvent(QueueEvent queueEvent) {
        mediaSession.setQueueTitle(queueEvent.queueName);
        mediaSession.setQueue(queueEvent.queueItems);
    }

    @Subscribe
    public void onEvent(MediaMetadataCompat mediaMetadata) {
        mediaSession.setMetadata(mediaMetadata);
    }

    @Subscribe
    public void onEvent(PlaybackStateCompat playbackState) {
        mediaSession.setPlaybackState(playbackState);
    }
}
