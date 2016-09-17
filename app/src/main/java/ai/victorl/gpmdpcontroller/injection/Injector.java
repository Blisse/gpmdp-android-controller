package ai.victorl.gpmdpcontroller.injection;

import android.content.Context;

import ai.victorl.gpmdpcontroller.injection.components.ActivityComponent;
import ai.victorl.gpmdpcontroller.injection.components.ApplicationComponent;

public class Injector {
    private static final String APPLICATION_COMPONENT = ApplicationComponent.class.getName();
    private static final String ACTIVITY_COMPONENT = ActivityComponent.class.getName();

    @SuppressWarnings({"ResourceType", "WrongConstant"}) // Explicitly doing a custom service.
    public static ActivityComponent activityComponent(Context context) {
        return (ActivityComponent) context.getSystemService(ACTIVITY_COMPONENT);
    }

    public static boolean matchesActivityComponentService(String name) {
        return ACTIVITY_COMPONENT.equals(name);
    }

    @SuppressWarnings({"ResourceType", "WrongConstant"}) // Explicitly doing a custom service.
    public static ApplicationComponent applicationComponent(Context context) {
        return (ApplicationComponent) context.getSystemService(APPLICATION_COMPONENT);
    }

    public static boolean matchesApplicationComponentService(String name) {
        return APPLICATION_COMPONENT.equals(name);
    }

    private Injector() {
        throw new AssertionError("No instances.");
    }
}
