package ai.victorl.gpmdpcontroller.ui.controller;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.injection.Injector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ControllerConnectView extends LinearLayoutCompat {
    @Inject GpmdpLocalSettings gpmdpLocalSettings;
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_ip_edittext) EditText ipEditText;
    @BindView(R.id.controller_connect_button) Button connectButton;

    @OnClick(R.id.controller_connect_button)
    void onClickConnect(View view) {
        gpmdpController.connect();
    }

    public ControllerConnectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Injector.activityComponent(context).inject(this);
        }
        LayoutInflater.from(context).inflate(R.layout.controller_connect_view, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ipEditText.setText(gpmdpLocalSettings.getHostIpAddress());
    }
}
