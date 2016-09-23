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
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PairActivity extends BaseActivity {
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_pin_edittext) EditText pinEditText;
    @BindView(R.id.controller_pin_button) Button pairButton;

    @OnClick(R.id.controller_pin_button)
    void onClickPin(View view) {
        gpmdpController.pin(pinEditText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        EventBusUtils.safeRegister(gpmdpController.getEventBus(), this);

        gpmdpController.tryAuthorize();
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
        return PairActivity.class.getSimpleName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpStateChangedEvent event) {
        switch (event.state) {
            case CLOSED:
                Intents.maybeStartActivity(this, new Intent(this, ConnectActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpAuthorizedEvent event) {
        Intents.maybeStartActivity(this, new Intent(this, PlayActivity.class));
    }
}
