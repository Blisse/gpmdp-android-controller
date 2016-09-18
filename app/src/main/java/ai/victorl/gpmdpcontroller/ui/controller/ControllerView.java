package ai.victorl.gpmdpcontroller.ui.controller;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpAuthorizedEvent;
import ai.victorl.gpmdpcontroller.data.gpmdp.events.GpmdpConnectStateChangedEvent;
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.ui.views.BetterViewAnimator;
import ai.victorl.gpmdpcontroller.utils.EventBusUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ControllerView extends LinearLayoutCompat {
    @Inject GpmdpLocalSettings gpmdpLocalSettings;
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_viewanimator) BetterViewAnimator viewAnimator;

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Injector.activityComponent(context).inject(this);
        }
        LayoutInflater.from(context).inflate(R.layout.controller_view, this, true);
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
    public void onEvent(GpmdpConnectStateChangedEvent event) {
        switch (event.state) {
            case CREATED:
            case CONNECTING:
            case CLOSING:
            case CLOSED:
            default:
                viewAnimator.setDisplayedChildId(R.id.controller_connect_view);
                break;
            case OPEN:
                viewAnimator.setDisplayedChildId(R.id.controller_pair_view);
                if (gpmdpController.isAuthorized()) {
                    gpmdpController.authorize();
                } else {
                    gpmdpController.pair();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpmdpAuthorizedEvent event) {
        viewAnimator.setDisplayedChildId(R.id.controller_play_view);
    }
}
