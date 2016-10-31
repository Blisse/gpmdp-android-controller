package ai.victorl.gpmdpcontroller.ui.activities;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.ui.adapters.PlaylistAdapter;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueueActivity extends MediaBrowserActivity {
    @Inject Picasso picasso;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cover) ImageView musicCoverView;
    @BindView(R.id.title) View titleView;
    @BindView(R.id.track_title) TextView trackTitleTextView;
    @BindView(R.id.track_artist) TextView trackArtistTextView;
    @BindView(R.id.progress) ProgressView progressView;
    @BindView(R.id.fab) FloatingMusicActionButton musicFab;
    @BindView(R.id.time) TextView timeTextView;
    @BindView(R.id.duration) TextView durationTextView;
    @BindView(R.id.name) TextView playlistNameTextView;
    @BindView(R.id.counter) TextView playlistCounterTextView;
    @BindView(R.id.tracks) RecyclerView tracksRecyclerView;

    private int currentPlaybackState = PlaybackStateCompat.STATE_NONE;

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().play();
        }
    }

    private LinearLayoutManager playlistLinearLayoutManager;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        EventBusUtils.safeRegister(playlistAdapter.getEventBus(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBusUtils.safeUnregister(playlistAdapter.getEventBus(), this);
    }

    @Override
    void onPreInflate() {
        getActivityComponent().inject(this);
    }

    @Override
    void onPostInflate() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tracksRecyclerView.setHasFixedSize(true);

        playlistLinearLayoutManager = new LinearLayoutManager(this);
        tracksRecyclerView.setLayoutManager(playlistLinearLayoutManager);

        playlistAdapter = new PlaylistAdapter(this);
        tracksRecyclerView.setAdapter(playlistAdapter);
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
    String getClassName() {
        return QueueActivity.class.getSimpleName();
    }

    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            long position = state.getPosition();
            timeTextView.setText(DateUtils.formatElapsedTime(position));
            progressView.setProgress(Long.valueOf(position).intValue());

            long queueId = state.getActiveQueueItemId();
            if (queueId != MediaSessionCompat.QueueItem.UNKNOWN_ID && queueId != playlistAdapter.getActivePosition()) {
                int queuePosition = Long.valueOf(queueId).intValue();
                playlistAdapter.setActive(queuePosition);
                tracksRecyclerView.smoothScrollToPosition(queuePosition);
            }

            if (currentPlaybackState != state.getState()) {
                if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    musicFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
                } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    musicFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
                }

                musicFab.playAnimation();
                currentPlaybackState = state.getState();
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            trackTitleTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE));
            trackArtistTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));

            picasso.load(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI))
                    .fit()
                    .centerCrop()
                    .into(musicCoverView);

            long duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            timeTextView.setText(DateUtils.formatElapsedTime(0));
            durationTextView.setText(DateUtils.formatElapsedTime(duration));
            progressView.setMax(Long.valueOf(duration).intValue());
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);

            playlistAdapter.setTracks(queue);
            playlistCounterTextView.setText(String.format(Locale.CANADA, "%d %s", queue.size(), queue.size() == 1 ? "song" : "songs"));
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);

            playlistNameTextView.setText(title);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlaylistAdapter.PlaylistOnClickEvent event) {
        if (getSupportMediaController() != null) {
            getSupportMediaController().getTransportControls().skipToQueueItem(event.selectedTrackId);
        }
    }
}
