package ai.victorl.gpmdpcontroller.data.media;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.ui.activities.ConnectActivity;
import ai.victorl.gpmdpcontroller.ui.views.Intents;

public class GpmdpMediaNotification extends BroadcastReceiver {
    private static final int GPMDPCONTROLLER_NOTIFICATION_ID = 415;
    private static final int GPMDPCONTROLLER_BROADCAST_INTENT_ID = 101;

    private static final String ACTION_PAUSE = "ai.victorl.gpmdpcontroller.ACTION_PAUSE";
    private static final String ACTION_PLAY = "ai.victorl.gpmdpcontroller.ACTION_PLAY";
    private static final String ACTION_PREVIOUS = "ai.victorl.gpmdpcontroller.ACTION_PREVIOUS";
    private static final String ACTION_NEXT = "ai.victorl.gpmdpcontroller.ACTION_NEXT";
    private static final String ACTION_LAUNCH = "ai.victorl.gpmdpcontroller.ACTION_LAUNCH";

    private final MediaBrowserServiceCompat mediaBrowserService;
    private final NotificationManagerCompat notificationManager;
    private final NotificationCompat.Builder notificationBuilder;

    private final PendingIntent pauseSongIntent;
    private final PendingIntent playSongIntent;
    private final PendingIntent previousSongIntent;
    private final PendingIntent nextSongIntent;
    private final PendingIntent launchIntent;

    private MediaSessionCompat.Token mediaSessionToken;
    private MediaControllerCompat mediaController;

    private boolean started = false;

    public GpmdpMediaNotification(MediaBrowserServiceCompat mediaBrowserService, MediaSessionCompat.Token mediaSessionToken) throws RemoteException {
        this.mediaBrowserService = mediaBrowserService;
        this.mediaSessionToken = mediaSessionToken;

        notificationManager = NotificationManagerCompat.from(mediaBrowserService);
        notificationBuilder = new NotificationCompat.Builder(mediaBrowserService);

        if (mediaBrowserService.getSessionToken() != null) {
            mediaController = new MediaControllerCompat(mediaBrowserService, mediaSessionToken);
        }

        pauseSongIntent = getBroadcastIntent(new Intent(ACTION_PAUSE));
        playSongIntent = getBroadcastIntent(new Intent(ACTION_PLAY));
        previousSongIntent = getBroadcastIntent(new Intent(ACTION_PREVIOUS));
        nextSongIntent = getBroadcastIntent(new Intent(ACTION_NEXT));
        launchIntent = getBroadcastIntent(new Intent(ACTION_LAUNCH));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_NEXT:
                if (mediaController != null) {
                    mediaController.getTransportControls().skipToNext();
                }
                break;
            case ACTION_PAUSE:
                if (mediaController != null) {
                    mediaController.getTransportControls().pause();
                }
                break;
            case ACTION_PLAY:
                if (mediaController != null) {
                    mediaController.getTransportControls().play();
                }
                break;
            case ACTION_PREVIOUS:
                if (mediaController != null) {
                    mediaController.getTransportControls().skipToPrevious();
                }
                break;
            case ACTION_LAUNCH:
                Intents.maybeStartActivity(mediaBrowserService, new Intent(mediaBrowserService, ConnectActivity.class));
                break;
            default:
                break;
        }
    }

    private PendingIntent getBroadcastIntent(Intent intent) {
        intent.setPackage(mediaBrowserService.getPackageName());
        return PendingIntent.getBroadcast(mediaBrowserService, GPMDPCONTROLLER_BROADCAST_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void start() {
        notificationManager.cancelAll();

        if (!started) {
            started = true;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PREVIOUS);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_LAUNCH);
            mediaBrowserService.registerReceiver(this, filter);

            notificationBuilder
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setColor(mediaBrowserService.getColor(R.color.pacifica))
                    .setOngoing(true)
                    .setContentIntent(launchIntent)
                    .setShowWhen(false)
                    .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setMediaSession(mediaBrowserService.getSessionToken()))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            mediaBrowserService.startForeground(GPMDPCONTROLLER_NOTIFICATION_ID, notificationBuilder.build());
            mediaController.registerCallback(mediaControllerCallback);
        }
    }

    public void stop() {
        if (started) {
            started = false;

            mediaController.unregisterCallback(mediaControllerCallback);
            notificationManager.cancel(GPMDPCONTROLLER_NOTIFICATION_ID);
        }
    }

    private void updateNotification(Bitmap bitmap) {
        notificationBuilder.setLargeIcon(bitmap);

        notificationManager.notify(GPMDPCONTROLLER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateNotification(PlaybackStateCompat playbackState) {
        ArrayList<NotificationCompat.Action> actions = new ArrayList<>();

        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            actions.add(new NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_previous_white_24dp,
                    mediaBrowserService.getString(R.string.notification_action_previous),
                    previousSongIntent).build());
        }

        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            notificationBuilder.setSmallIcon(R.drawable.ic_play_arrow_white_24dp);
            actions.add(new NotificationCompat.Action.Builder(
                    R.drawable.ic_pause_white_24dp,
                    mediaBrowserService.getString(R.string.notification_action_pause),
                    pauseSongIntent).build());
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_pause_white_24dp);
            actions.add(new NotificationCompat.Action.Builder(
                    R.drawable.ic_play_arrow_white_24dp,
                    mediaBrowserService.getString(R.string.notification_action_play),
                    playSongIntent).build());
        }

        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            actions.add(new NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_next_white_24dp,
                    mediaBrowserService.getString(R.string.notification_action_next),
                    nextSongIntent).build());
        }

        notificationBuilder.setStyle(new NotificationCompat.MediaStyle()
                .setMediaSession(mediaBrowserService.getSessionToken())
                .setShowActionsInCompactView(actions.size() > 1 ? new int[]{ 1 } : new int[]{}));

        notificationBuilder.mActions = actions;

        if (mediaController != null && mediaController.getMetadata() != null) {
            int max = Long.valueOf(mediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)).intValue();
            int progress = Long.valueOf(playbackState.getPosition()).intValue();
            notificationBuilder.setProgress(max, progress, false);
        }

        notificationManager.notify(GPMDPCONTROLLER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateNotification(MediaMetadataCompat metadata) {
        MediaDescriptionCompat description = metadata.getDescription();

        notificationBuilder
                .setProgress(Long.valueOf(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)).intValue(), 0, false)
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle());

        if (description.getIconBitmap() != null) {
            notificationBuilder.setLargeIcon(description.getIconBitmap());
        } else {
            Picasso.with(mediaBrowserService)
                    .load(description.getIconUri())
                    .into(mediaIconTarget);
        }

        notificationManager.notify(GPMDPCONTROLLER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private Target mediaIconTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            updateNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            updateNotification(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if ((System.currentTimeMillis() - state.getLastPositionUpdateTime()) > 2000) {
                updateNotification(state);
            }
        }
    };
}
