package ai.victorl.gpmdpcontroller.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlaybackState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.QueueResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Time;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.ui.adapters.PlaylistAdapter;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueueActivity extends BaseActivity {
    @Inject GpmdpController gpmdpController;
    @Inject Picasso picasso;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cover) ImageView musicCoverView;
    @BindView(R.id.title) View titleView;
    @BindView(R.id.track_title) TextView trackTitleTextView;
    @BindView(R.id.track_artist) TextView trackArtistTextView;
    @BindView(R.id.progress) ProgressView progressView;
    @BindView(R.id.fab) FloatingActionButton playPauseFab;
    @BindView(R.id.time) TextView timeTextView;
    @BindView(R.id.duration) TextView durationTextView;
    @BindView(R.id.name) TextView playlistNameTextView;
    @BindView(R.id.counter) TextView playlistCounterTextView;
    @BindView(R.id.tracks) RecyclerView tracksRecyclerView;

    @BindDrawable(R.drawable.ic_pause_animatable) Drawable pauseAnimatable;
    @BindDrawable(R.drawable.ic_play_animatable) Drawable playAnimatable;

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        gpmdpController.playPause();
    }

    private LinearLayoutManager playlistLinearLayoutManager;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);
        gpmdpController.requestState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
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
        EventBusUtils.safeRegister(playlistAdapter.getEventBus(), this);
    }

    @Override
    String getClassName() {
        return QueueActivity.class.getSimpleName();
    }

    private void updateCurrentTrack(Track track) {
        playlistAdapter.setCurrentTrack(track);
        if (playlistAdapter.getCurrentTrackIndex() != -1) {
            playlistLinearLayoutManager.scrollToPositionWithOffset(playlistAdapter.getCurrentTrackIndex(), 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Track track) {
        trackTitleTextView.setText(track.title);
        trackArtistTextView.setText(track.artist);
        picasso.load(track.albumArt)
                .fit()
                .centerCrop()
                .into(musicCoverView);
        updateCurrentTrack(track);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Time time) {
        int currentSeconds = time.current / 1000;
        int totalSeconds = time.total / 1000;

        timeTextView.setText(DateUtils.formatElapsedTime(currentSeconds));
        durationTextView.setText(DateUtils.formatElapsedTime(totalSeconds));
        if (progressView.getMax() != totalSeconds) {
            progressView.setMax(totalSeconds);
        }
        progressView.setProgress(currentSeconds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QueueResponse queueResponse) {
        List<Track> queue = queueResponse.queue;
        playlistNameTextView.setText("Queue");
        playlistCounterTextView.setText(queue.size() + " songs");
        playlistAdapter.setTracks(queue);

        for (Track track : queue) {
            if (track.title.equals(trackTitleTextView.getText())
                    && track.artist.equals(trackArtistTextView.getText())) {
                updateCurrentTrack(track);
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
            case PAUSED:
                playPauseFab.setImageDrawable(pauseAnimatable);
                break;
            case PLAYING:
                playPauseFab.setImageDrawable(playAnimatable);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlaylistAdapter.PlaylistOnClickEvent event) {
        gpmdpController.playQueueWithTrack(event.selectedTrack);
    }
}
