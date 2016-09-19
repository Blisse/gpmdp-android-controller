package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.ui.views.ProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueueActivity extends BaseActivity {

    @BindView(R.id.cover) MusicCoverView musicCoverView;
    @BindView(R.id.title) View titleView;
    @BindView(R.id.progress) ProgressView progressView;
    @BindView(R.id.fab) FloatingActionButton playPauseFab;
    @BindView(R.id.time) TextView timeTextView;
    @BindView(R.id.duration) TextView durationTextView;
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
        Intents.maybeStartActivity(this, new Intent(this, QueueActivity.class), options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
    }

    @Override
    void onPreInflate() {
        getActivityComponent().inject(this);
    }

    @Override
    void onPostInflate() {
        ButterKnife.bind(this);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    String getClassName() {
        return QueueActivity.class.getSimpleName();
    }
}
