package ai.victorl.gpmdpcontroller;

import android.app.Application;
import android.content.Context;

import ai.victorl.gpmdpcontroller.injection.components.ApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.components.DaggerApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.modules.ApplicationModule;

public class GpmdpControllerApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        applicationComponent.inject(this);
    }

    public static GpmdpControllerApplication get(Context context) {
        return (GpmdpControllerApplication) context.getApplicationContext();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
