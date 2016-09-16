package ai.victorl.gpmdpcontroller.eventbus;

import org.greenrobot.eventbus.EventBus;

public interface EventBusSubscriber {
    Object register(EventBus eventBus);
    void unregister(EventBus eventBus);
}
