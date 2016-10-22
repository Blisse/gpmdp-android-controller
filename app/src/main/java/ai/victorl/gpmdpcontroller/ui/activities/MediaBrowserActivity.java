package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import ai.victorl.gpmdpcontroller.data.media.GpmdpMediaService;
import ai.victorl.gpmdpcontroller.data.media.actions.RefreshAction;
import ai.victorl.gpmdpcontroller.ui.views.Intents;

public abstract class MediaBrowserActivity extends BaseActivity {

    private MediaBrowserCompat mediaBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, GpmdpMediaService.class), mediaBrowserConnectionCallback, null);
        mediaBrowser.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaBrowser.isConnected()) {
            onMediaBrowserDisconnect();
        }
        mediaBrowser.disconnect();
    }

    private MediaBrowserCompat.ConnectionCallback mediaBrowserConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            try {
                MediaControllerCompat mediaController = new MediaControllerCompat(MediaBrowserActivity.this, mediaBrowser.getSessionToken());
                setSupportMediaController(mediaController);
                onMediaBrowserConnect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    protected final MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    protected void onMediaBrowserConnect() {;
        getSupportMediaController().registerCallback(mediaControllerCallback);
        getSupportMediaController().getTransportControls().sendCustomAction(RefreshAction.getName(), null);
    }

    protected void onMediaBrowserDisconnect() {
        getSupportMediaController().unregisterCallback(mediaControllerCallback);
    }

    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            if (state.getState() == PlaybackStateCompat.STATE_ERROR) {
                Log.d("GPMDP", state.getErrorMessage().toString());
                Intents.maybeStartActivity(MediaBrowserActivity.this, new Intent(MediaBrowserActivity.this, ConnectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
    };
}
