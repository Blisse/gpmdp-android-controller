package ai.victorl.gpmdpcontroller.ui.controller;

import android.os.Bundle;
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
import ai.victorl.gpmdpcontroller.ui.views.BaseActivity;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ControllerActivity extends BaseActivity {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);

        ipEditText.setText(gpmdpLocalSettings.getGpmdpIpAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.safeUnregister(gpmdpController.getEventBus(), this);
    }

    @Override
    protected void onPostInflate() {
        super.onPostInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_controller;
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
