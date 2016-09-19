package ai.victorl.gpmdpcontroller.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
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
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.PlayStateResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RatingResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.RepeatResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.ShuffleResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Time;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TimeResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;
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

    @BindView(R.id.controller_rating_liked_textview) TextView ratingLikedTextView;
    @BindView(R.id.controller_rating_disliked_textview) TextView ratingDislikedTextView;

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

    @BindColor(R.color.colorAccent) int accentColor;
    @BindColor(android.R.color.white) int whiteColor;

    @Override
    public void onBackPressed() {
        onFabClick(null);
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        musicCoverView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);
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
    protected void onPostInflate() {
        ButterKnife.bind(this);
    }

    @Override
    protected String getClassName() {
        return PlayActivity.class.getSimpleName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TrackResponse response) {
        Track trackInfo = response.trackPayload;
        trackTitleTextView.setText(trackInfo.title);
        trackArtistTextView.setText(trackInfo.artist);
        picasso.load(trackInfo.albumArt)
                .fit()
                .centerCrop()
                .into(musicCoverView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TimeResponse response) {
        Time timeInfo = response.timePayload;
        int currentSeconds = timeInfo.current / 1000;
        int totalSeconds = timeInfo.total / 1000;

        timeTextView.setText(DateUtils.formatElapsedTime(currentSeconds));
        durationTextView.setText(DateUtils.formatElapsedTime(totalSeconds));
        if (progressView.getMax() != totalSeconds) {
            progressView.setMax(totalSeconds);
        }
        progressView.setProgress(currentSeconds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RepeatResponse response) {
        switch (response.repeat) {
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
    public void onEvent(ShuffleResponse response) {
        switch (response.shuffle) {
            case ALL_SHUFFLE:
                shuffleImageView.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
                break;
            case NO_SHUFFLE:
                shuffleImageView.getDrawable().clearColorFilter();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RatingResponse response) {
        ratingLikedTextView.setText(response.ratingPayload.liked.toString());
        ratingDislikedTextView.setText(response.ratingPayload.disliked.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayStateResponse response) {
        if (response.playState) {
            musicCoverView.start();
        } else {
            musicCoverView.stop();
        }
    }
}
