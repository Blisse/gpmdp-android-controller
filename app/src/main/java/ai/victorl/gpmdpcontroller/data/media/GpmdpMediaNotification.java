package ai.victorl.gpmdpcontroller.data.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;

public class GpmdpMediaNotification extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 412;

    private static final String ACTION_PAUSE = "ai.victorl.gpmdpcontroller.ACTION_PAUSE";
    private static final String ACTION_PLAY = "ai.victorl.gpmdpcontroller.ACTION_PLAY";
    private static final String ACTION_PREVIOUS = "ai.victorl.gpmdpcontroller.ACTION_PREVIOUS";
    private static final String ACTION_NEXT = "ai.victorl.gpmdpcontroller.ACTION_NEXT";
    private static final String ACTION_STOP = "ai.victorl.gpmdpcontroller.ACTION_STOP";

    private final MediaBrowserServiceCompat mediaBrowserService;
    private final NotificationManagerCompat notificationManager;

    private final PendingIntent pauseSongIntent;
    private final PendingIntent playSongIntent;
    private final PendingIntent previousSongIntent;
    private final PendingIntent nextSongIntent;
    private final PendingIntent stopBroadcastIntent;

    private MediaSessionCompat.Token sessionToken;
    private MediaControllerCompat mediaController;
    private MediaControllerCompat.TransportControls transportControls;

    private boolean started = false;

    public GpmdpMediaNotification(MediaBrowserServiceCompat mediaBrowserService) throws RemoteException {
        this.mediaBrowserService = mediaBrowserService;

        notificationManager = NotificationManagerCompat.from(mediaBrowserService);

        sessionToken = mediaBrowserService.getSessionToken();
        if (sessionToken != null) {
            mediaController = new MediaControllerCompat(mediaBrowserService, sessionToken);
            transportControls = mediaController.getTransportControls();
        }

        pauseSongIntent = getBroadcastIntent(new Intent(ACTION_PAUSE));
        playSongIntent = getBroadcastIntent(new Intent(ACTION_PLAY));
        previousSongIntent = getBroadcastIntent(new Intent(ACTION_PREVIOUS));
        nextSongIntent = getBroadcastIntent(new Intent(ACTION_NEXT));
        stopBroadcastIntent = getBroadcastIntent(new Intent(ACTION_STOP));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_NEXT:
                transportControls.skipToNext();
                break;
            case ACTION_PAUSE:
                transportControls.pause();
                break;
            case ACTION_PLAY:
                transportControls.play();
                break;
            case ACTION_PREVIOUS:
                transportControls.skipToPrevious();
                break;
            case ACTION_STOP:
                transportControls.stop();
                break;
            default:
                break;
        }
    }

    private PendingIntent getBroadcastIntent(Intent intent) {
        intent.setPackage(mediaBrowserService.getPackageName());
        return PendingIntent.getBroadcast(mediaBrowserService, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void start() {
        notificationManager.cancelAll();

        if (!started) {
            started = true;
            mediaController.registerCallback(mediaControllerCallback);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mediaBrowserService);
            PlaybackStateCompat playbackState = mediaController.getPlaybackState();

            if (playbackState != null) {
                if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
                    notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                            "Previous", previousSongIntent);
                }

                if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    notificationBuilder.addAction(R.drawable.ic_pause_white_24dp, "Pause", pauseSongIntent);
                } else {
                    notificationBuilder.addAction(R.drawable.ic_play_arrow_white_24dp, "Play", playSongIntent);
                }

                if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
                    notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                            "Next", nextSongIntent);
                }

                MediaDescriptionCompat description = mediaController.getMetadata().getDescription();

                if (description != null) {
                    notificationBuilder
                            .setStyle(new android.support.v7.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(new int[]{1})
                                    .setMediaSession(sessionToken))
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setContentTitle(description.getTitle())
                            .setContentText(description.getSubtitle());
                }

                if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    notificationBuilder.setWhen(playbackState.getPosition())
                            .setShowWhen(true)
                            .setUsesChronometer(true)
                            .setOngoing(true);
                } else {
                    notificationBuilder.setWhen(0)
                            .setShowWhen(false)
                            .setUsesChronometer(false)
                            .setOngoing(false);
                }
            }

            Notification notification = notificationBuilder.build();

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PREVIOUS);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_STOP);

            mediaBrowserService.registerReceiver(this, filter);
            mediaBrowserService.startForeground(NOTIFICATION_ID, notification);
        }
    }

    public void stop() {

    }

    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
        }
    };
}
