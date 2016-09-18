package ai.victorl.gpmdpcontroller.ui.controller;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

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
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ControllerPlayView extends LinearLayoutCompat {
    @Inject GpmdpController gpmdpController;
    @Inject Picasso picasso;

    @BindView(R.id.controller_track_title_textview) TextView trackTitleTextView;
    @BindView(R.id.controller_track_artist_textview) TextView trackArtistTextView;
    @BindView(R.id.controller_track_album_textview) TextView trackAlbumTextView;
    @BindView(R.id.controller_time_current_textview) TextView timeCurrentTextView;
    @BindView(R.id.controller_time_total_textview) TextView timeTotalTextView;
    @BindView(R.id.controller_time_current_progressbar) ProgressBar timeCurrentProgressBar;
    @BindView(R.id.controller_track_albumart_imageview) ImageView trackAlbumArtImageView;
    @BindView(R.id.controller_repeat_textview) TextView repeatTextView;
    @BindView(R.id.controller_shuffle_textview) TextView shuffleTextView;
    @BindView(R.id.controller_rating_liked_textview) TextView ratingLikedTextView;
    @BindView(R.id.controller_rating_disliked_textview) TextView ratingDislikedTextView;
    @BindView(R.id.controller_playstate_textview) TextView playStateTextView;

    public ControllerPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Injector.activityComponent(context).inject(this);
        }
        LayoutInflater.from(context).inflate(R.layout.controller_play_view, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TrackResponse response) {
        Track trackInfo = response.trackPayload;
        trackTitleTextView.setText(trackInfo.title);
        trackArtistTextView.setText(trackInfo.artist);
        trackAlbumTextView.setText(trackInfo.album);
        picasso.load(trackInfo.albumArt).into(trackAlbumArtImageView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TimeResponse response) {
        Time timeInfo = response.timePayload;
        int current = (int) TimeUnit.MILLISECONDS.toSeconds(timeInfo.current);
        int total = (int) TimeUnit.MILLISECONDS.toSeconds(timeInfo.total);
        timeCurrentTextView.setText(String.valueOf(current));
        timeTotalTextView.setText(String.valueOf(total));
        timeCurrentProgressBar.setMax(total);
        timeCurrentProgressBar.setProgress(current);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RepeatResponse response) {
        repeatTextView.setText(response.repeat.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShuffleResponse response) {
        shuffleTextView.setText(response.shuffle.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RatingResponse response) {
        ratingLikedTextView.setText(response.ratingPayload.liked.toString());
        ratingDislikedTextView.setText(response.ratingPayload.disliked.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayStateResponse response) {
        playStateTextView.setText(response.playState ? "Playing" : "Paused");
    }
}
