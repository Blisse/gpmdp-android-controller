package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.media.GpmdpMediaExtras;
import ai.victorl.gpmdpcontroller.data.media.actions.RepeatAction;
import ai.victorl.gpmdpcontroller.data.media.actions.ShuffleAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeDownAction;
import ai.victorl.gpmdpcontroller.data.media.actions.VolumeUpAction;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends MediaBrowserActivity {
    @Inject Picasso picasso;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cover) MusicCoverView musicCoverView;
    @BindView(R.id.title) View titleView;
    @BindView(R.id.track_title) TextView trackTitleTextView;
    @BindView(R.id.track_artist) TextView trackArtistTextView;
    @BindView(R.id.progress) ProgressView progressView;
    @BindView(R.id.fab) FloatingMusicActionButton musicFab;
    @BindView(R.id.time) TextView timeTextView;
    @BindView(R.id.duration) TextView durationTextView;
    @BindView(R.id.repeat) ImageView repeatImageView;
    @BindView(R.id.shuffle) ImageView shuffleImageView;
    @BindView(R.id.previous) ImageView previousImageView;
    @BindView(R.id.next) ImageView nextImageView;

    @BindDrawable(R.drawable.ic_repeat_white_24dp) Drawable repeatWhiteDrawable;
    @BindDrawable(R.drawable.ic_repeat_one_white_24dp) Drawable repeatOneWhiteDrawable;

    @BindColor(R.color.pacifica) int accentColor;
    @BindColor(android.R.color.white) int whiteColor;

    private int currentPlaybackState = PlaybackStateCompat.STATE_NONE;

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().play();
        }
    }

    @OnClick(R.id.previous)
    public void onClickPrevious(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().skipToPrevious();
        }
    }

    @OnClick(R.id.next)
    public void onClickNext(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().skipToNext();
        }
    }

    @OnClick(R.id.shuffle)
    public void onClickShuffle(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().sendCustomAction(ShuffleAction.getName(), null);
        }
    }

    @OnClick(R.id.repeat)
    public void onClickRepeat(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().sendCustomAction(RepeatAction.getName(), null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        animateMusicCover(currentPlaybackState);
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
                animateMusicCover(currentPlaybackState);
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                animateMusicCover(currentPlaybackState);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
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
    protected void onMediaBrowserConnect() {
        super.onMediaBrowserConnect();

        getSupportMediaController().registerCallback(mediaControllerCallback);
    }

    @Override
    protected void onMediaBrowserDisconnect() {
        super.onMediaBrowserDisconnect();

        getSupportMediaController().unregisterCallback(mediaControllerCallback);
    }

    @Override
    protected String getClassName() {
        return PlayActivity.class.getSimpleName();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO: Fix hack due to https://code.google.com/p/android/issues/detail?id=226670
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            getSupportMediaController().getTransportControls().sendCustomAction(VolumeUpAction.getName(), null);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            getSupportMediaController().getTransportControls().sendCustomAction(VolumeDownAction.getName(), null);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private boolean navigateToQueue() {
        musicCoverView.stop();
        return Intents.maybeStartActivity(this, new Intent(this, QueueActivity.class), null);
    }

    private void animateMusicCover(int playbackState) {
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            musicCoverView.start();
        } else {
            musicCoverView.stop();
        }
    }

    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            long position = state.getPosition();
            timeTextView.setText(DateUtils.formatElapsedTime(position));
            progressView.setProgress(Long.valueOf(position).intValue());

            if (currentPlaybackState != state.getState()) {
                if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    musicFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
                } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    musicFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
                }

                musicFab.playAnimation();

                currentPlaybackState = state.getState();

                animateMusicCover(currentPlaybackState);
            }

            Bundle stateExtras = state.getExtras();
            if (stateExtras != null && !stateExtras.isEmpty()) {
                GpmdpMediaExtras mediaExtras = GpmdpMediaExtras.fromBundle(stateExtras);

                switch (mediaExtras.getShuffle()) {
                    case ALL_SHUFFLE:
                        shuffleImageView.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                        break;
                    case NO_SHUFFLE:
                        shuffleImageView.getDrawable().setColorFilter(whiteColor, PorterDuff.Mode.MULTIPLY);
                        break;
                }

                switch (mediaExtras.getRepeat()) {
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
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            trackTitleTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE));
            trackArtistTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

            String displayIconUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI);
            if (!TextUtils.isEmpty(displayIconUrl)) {
                picasso.load(displayIconUrl)
                        .fit()
                        .centerCrop()
                        .into(musicCoverView);
            }

            Long duration = Long.valueOf(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
            timeTextView.setText(DateUtils.formatElapsedTime(0));
            durationTextView.setText(DateUtils.formatElapsedTime(duration));
            progressView.setMax(duration.intValue());
        }
    };

}
