package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectActivity extends BaseActivity {
    @Inject GpmdpLocalSettings gpmdpLocalSettings;
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_ip_edittext) EditText ipEditText;
    @BindView(R.id.controller_connect_button) Button connectButton;

    @OnClick(R.id.controller_connect_button)
    void onClickConnect(View view) {
        gpmdpController.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);

        gpmdpController.connect();
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
        ipEditText.setText(gpmdpLocalSettings.getHostIpAddress());
    }

    @Override
    protected String getClassName() {
        return ConnectActivity.class.getSimpleName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpStateChangedEvent event) {
        switch (event.state) {
            case OPEN:
                Intents.maybeStartActivity(this, new Intent(this, PairActivity.class));
                break;
        }
    }
}
