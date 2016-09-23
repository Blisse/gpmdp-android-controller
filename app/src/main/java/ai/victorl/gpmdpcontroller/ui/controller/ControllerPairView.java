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
import ai.victorl.gpmdpcontroller.injection.Injector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ControllerPairView extends LinearLayoutCompat {
    @Inject GpmdpController gpmdpController;

    @BindView(R.id.controller_pin_edittext) EditText pinEditText;
    @BindView(R.id.controller_pin_button) Button pairButton;

    @OnClick(R.id.controller_pin_button)
    void onClickPair(View view) {
        gpmdpController.pin(pinEditText.getText().toString());
    }

    public ControllerPairView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Injector.activityComponent(context).inject(this);
        }
        LayoutInflater.from(context).inflate(R.layout.controller_pair_view, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
