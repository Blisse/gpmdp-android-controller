package ai.victorl.gpmdpcontroller.data.gpmdp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import ai.victorl.gpmdpcontroller.ui.activities.ConnectActivity;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;

public class GpmdpMediaService extends MediaBrowserServiceCompat {
    @Inject @ApplicationScope Context context;
    @Inject GpmdpController gpmdpController;
    @Inject GpmdpMediaProvider gpmdpMediaProvider;

    private final GpmdpMediaSessionCallback mediaSessionCallback = new GpmdpMediaSessionCallback();
    private MediaSessionCompat mediaSession;
    private GpmdpMediaNotification mediaNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.applicationComponent(getApplicationContext()).inject(this);

        mediaSession = new MediaSessionCompat(context, GpmdpMediaService.class.getSimpleName());
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent intent = new Intent(context, ConnectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pendingIntent);

        try {
            mediaNotification = new GpmdpMediaNotification(this);
            mediaNotification.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaNotification.stop();

        mediaSession.release();
        mediaSession = null;

        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(GpmdpMediaProvider.MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(gpmdpMediaProvider.getChildren(parentId, getResources()));
    }

    @Override
    public void onLoadItem(String itemId, Result<MediaBrowserCompat.MediaItem> result) {
        super.onLoadItem(itemId, result);
    }

    private class GpmdpMediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            gpmdpController.playPause();
        }

        @Override
        public void onPause() {
            super.onPause();
            gpmdpController.playPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            gpmdpController.forward();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            gpmdpController.rewind();
        }
    }

    @Subscribe
    public void onEvent(GpmdpStateChangedEvent event) {
        switch (event.state) {
            case OPEN:
                mediaSession.setActive(true);
                break;
            case CLOSED:
                mediaSession.setActive(false);
                break;
        }
    }
}
