package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
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
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueueActivity extends BaseActivity {
    @Inject GpmdpController gpmdpController;
    @Inject Picasso picasso;

    @BindView(R.id.cover) MusicCoverView musicCoverView;
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

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>((View) musicCoverView, ViewCompat.getTransitionName(musicCoverView)),
                new Pair<>((View) titleView, ViewCompat.getTransitionName(titleView)),
                new Pair<>((View) timeTextView, ViewCompat.getTransitionName(timeTextView)),
                new Pair<>((View) durationTextView, ViewCompat.getTransitionName(durationTextView)),
                new Pair<>((View) progressView, ViewCompat.getTransitionName(progressView)),
                new Pair<>((View) playPauseFab, ViewCompat.getTransitionName(playPauseFab)));
        Intents.maybeStartActivity(this, new Intent(this, PlayActivity.class), options.toBundle());
    }

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
    }

    @Override
    void onPreInflate() {
        getActivityComponent().inject(this);
    }

    @Override
    void onPostInflate() {
        ButterKnife.bind(this);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlistAdapter = new PlaylistAdapter(this);
        tracksRecyclerView.setAdapter(playlistAdapter);
    }

    @Override
    String getClassName() {
        return QueueActivity.class.getSimpleName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Track track) {
        trackTitleTextView.setText(track.title);
        trackArtistTextView.setText(track.artist);
        picasso.load(track.albumArt)
                .fit()
                .centerCrop()
                .into(musicCoverView);
        playlistAdapter.setCurrentTrack(track);
        if (playlistAdapter.getCurrentTrackIndex() != -1) {
            tracksRecyclerView.smoothScrollToPosition(playlistAdapter.getCurrentTrackIndex());
        }
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
                playlistAdapter.setCurrentTrack(track);
                if (playlistAdapter.getCurrentTrackIndex() != -1) {
                    tracksRecyclerView.smoothScrollToPosition(playlistAdapter.getCurrentTrackIndex());
                }
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
            case PAUSED:
                break;
            case PLAYING:
                break;
        }
    }
}
