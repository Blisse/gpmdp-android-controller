package ai.victorl.gpmdpcontroller.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpStateChangedEvent;
import ai.victorl.gpmdpcontroller.ui.views.Intents;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.philio.pinentry.PinEntryView;

public class PairActivity extends BaseActivity {
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_pin_textview) TextView pinTextView;
    @BindView(R.id.controller_pin_pinentryview) PinEntryView pinEntryView;
    @BindView(R.id.controller_pin_button) Button pairButton;
    @BindString(R.string.controller_pin_textview_error) String pinTextViewError;

    @OnClick(R.id.controller_pin_button)
    void onClickPin(View view) {
        gpmdpController.pin(pinEntryView.getText().toString());
    }

    @Override
    public void onBackPressed() {
        Intents.maybeStartActivity(this, new Intent(this, ConnectActivity.class));
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
                Intents.maybeStartActivity(this, new Intent(this, ConnectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpAuthorizedEvent event) {
        Intents.maybeStartActivity(this, new Intent(this, PlayActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
