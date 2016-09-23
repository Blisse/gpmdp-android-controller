package ai.victorl.gpmdpcontroller.utils;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {
    public static void safeRegister(EventBus eventBus, Object subscriber) {
        if (!eventBus.isRegistered(subscriber)) {
            eventBus.register(subscriber);
        }
    }

    public static void safeUnregister(EventBus eventBus, Object subscriber) {
        if (eventBus.isRegistered(subscriber)) {
            eventBus.unregister(subscriber);
        }
    }

    public static <T> void safePost(EventBus eventBus, T object) {
        if (object != null) {
            eventBus.post(object);
        }
    }
}
