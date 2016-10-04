package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpMediaService;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Rating;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Shuffle;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Time;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity {
    @Inject GpmdpController gpmdpController;
    @Inject Picasso picasso;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cover) MusicCoverView musicCoverView;
    @BindView(R.id.title) View titleView;
    @BindView(R.id.track_title) TextView trackTitleTextView;
    @BindView(R.id.track_artist) TextView trackArtistTextView;
    @BindView(R.id.progress) ProgressView progressView;
    @BindView(R.id.fab) FloatingActionButton playPauseFab;
    @BindView(R.id.time) TextView timeTextView;
    @BindView(R.id.duration) TextView durationTextView;
    @BindView(R.id.repeat) ImageView repeatImageView;
    @BindView(R.id.shuffle) ImageView shuffleImageView;
    @BindView(R.id.previous) ImageView previousImageView;
    @BindView(R.id.next) ImageView nextImageView;

    @BindDrawable(R.drawable.ic_repeat_white_24dp) Drawable repeatWhiteDrawable;
    @BindDrawable(R.drawable.ic_repeat_one_white_24dp) Drawable repeatOneWhiteDrawable;
    @BindDrawable(R.drawable.ic_pause_white_24dp) Drawable pauseDrawable;
    @BindDrawable(R.drawable.ic_play_arrow_white_24dp) Drawable playDrawable;
    @BindDrawable(R.drawable.ic_stop_white_24dp) Drawable stopDrawable;

    @BindColor(R.color.pacifica) int accentColor;
    @BindColor(android.R.color.white) int whiteColor;

    private MediaBrowserCompat mediaBrowser;

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        gpmdpController.playPause();
    }

    @OnClick(R.id.previous)
    public void onClickPrevious(View view) {
        gpmdpController.rewind();
    }

    @OnClick(R.id.next)
    public void onClickNext(View view) {
        gpmdpController.forward();
    }

    @OnClick(R.id.shuffle)
    public void onClickShuffle(View view) {
        gpmdpController.toggleShuffle();
    }

    @OnClick(R.id.repeat)
    public void onClickRepeat(View view) {
        gpmdpController.toggleRepeat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mediaBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, GpmdpMediaService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        super.onConnected();
                        MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
                        try {
                            MediaControllerCompat controller = new MediaControllerCompat(PlayActivity.this, token);
                            setSupportMediaController(controller);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                },
                null);
        mediaBrowser.connect();

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);

        gpmdpController.requestState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaBrowser.disconnect();

        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Override
    void onPreInflate() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void onPostInflate() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        musicCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                gpmdpController.getPlaybackState();
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                gpmdpController.getPlaybackState();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
    }

    private boolean navigateToQueue() {
        musicCoverView.stop();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>((View) musicCoverView, ViewCompat.getTransitionName(musicCoverView)),
                new Pair<>((View) titleView, ViewCompat.getTransitionName(titleView)),
                new Pair<>((View) timeTextView, ViewCompat.getTransitionName(timeTextView)),
                new Pair<>((View) durationTextView, ViewCompat.getTransitionName(durationTextView)),
                new Pair<>((View) progressView, ViewCompat.getTransitionName(progressView)),
                new Pair<>((View) playPauseFab, ViewCompat.getTransitionName(playPauseFab)));
        return Intents.maybeStartActivity(this, new Intent(this, QueueActivity.class), options.toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_queue:
                return navigateToQueue();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getClassName() {
        return PlayActivity.class.getSimpleName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Track track) {
        trackTitleTextView.setText(track.title);
        trackArtistTextView.setText(track.artist);
        picasso.load(track.albumArt)
                .fit()
                .centerCrop()
                .into(musicCoverView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Time trackTime) {
        int currentSeconds = trackTime.current / 1000;
        int totalSeconds = trackTime.total / 1000;

        timeTextView.setText(DateUtils.formatElapsedTime(currentSeconds));
        durationTextView.setText(DateUtils.formatElapsedTime(totalSeconds));
        if (progressView.getMax() != totalSeconds) {
            progressView.setMax(totalSeconds);
        }
        progressView.setProgress(currentSeconds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Repeat repeat) {
        switch (repeat) {
            case NO_REPEAT:
                repeatImageView.setImageDrawable(repeatWhiteDrawable);
                repeatImageView.getDrawable().setColorFilter(whiteColor, PorterDuff.Mode.MULTIPLY);
                break;
            case LIST_REPEAT:
                repeatImageView.setImageDrawable(repeatWhiteDrawable);
                repeatImageView.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                break;
            case SINGLE_REPEAT:
                repeatImageView.setImageDrawable(repeatOneWhiteDrawable);
                repeatImageView.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Shuffle shuffle) {
        switch (shuffle) {
            case ALL_SHUFFLE:
                shuffleImageView.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                break;
            case NO_SHUFFLE:
                shuffleImageView.getDrawable().setColorFilter(whiteColor, PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Rating rating) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
                playPauseFab.setImageDrawable(stopDrawable);
                musicCoverView.stop();
                break;
            case PAUSED:
                playPauseFab.setImageDrawable(pauseDrawable);
                musicCoverView.stop();
                break;
            case PLAYING:
                playPauseFab.setImageDrawable(playDrawable);
                musicCoverView.start();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpStateChangedEvent event) {
        switch (event.state) {
            case CLOSED:
                Intents.maybeStartActivity(this, new Intent(this, ConnectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
    }
}
