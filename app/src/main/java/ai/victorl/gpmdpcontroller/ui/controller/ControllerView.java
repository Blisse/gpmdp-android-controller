package ai.victorl.gpmdpcontroller.ui.controller;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Channel;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.GpmdpResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.TrackResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpResponseEvent;
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ControllerView extends LinearLayoutCompat {
    @Inject GpmdpLocalSettings gpmdpLocalSettings;
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_ip_edittext) EditText ipEditText;
    @BindView(R.id.controller_connect_button) Button connectButton;
    @BindView(R.id.controller_track_title_textview) TextView trackTitleTextView;
    @BindView(R.id.controller_track_artist_textview) TextView trackArtistTextView;

    @OnTextChanged(R.id.controller_ip_edittext)
    void onTextChangedIp(CharSequence text) {
        gpmdpLocalSettings.saveGpmdpIpAddress(ipEditText.getText().toString());
    }

    @OnClick(R.id.controller_connect_button)
    void onClickConnect(View view) {
        gpmdpController.connect();
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Injector.activityComponent(context).inject(this);
        }
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

        ipEditText.setText(gpmdpLocalSettings.getGpmdpIpAddress());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpResponseEvent event) {
        GpmdpResponse response = event.response;
        Channel channel = response.channel;
        switch (channel) {
            case TRACK:
                Track trackInfo = ((TrackResponse) response).trackPayload;
                trackTitleTextView.setText(trackInfo.title);
                trackArtistTextView.setText(trackInfo.artist);
                break;
        }
    }
}
