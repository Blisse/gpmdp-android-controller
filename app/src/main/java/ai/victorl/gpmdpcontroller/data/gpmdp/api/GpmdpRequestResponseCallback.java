package ai.victorl.gpmdpcontroller.data.gpmdp.api;

import org.greenrobot.eventbus.EventBus;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;

public interface GpmdpRequestResponseCallback {
    void onSuccess(GpmdpRequestResponse requestResponse, final GpmdpState state, final EventBus eventBus);

    void onError(GpmdpRequestResponse requestResponse, final GpmdpState state, final EventBus eventBus);
}
