package ai.victorl.gpmdpcontroller;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.injection.components.ApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.components.DaggerApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.modules.ApplicationModule;

public class GpmdpControllerApplication extends Application {
    private ApplicationComponent applicationComponent;

    public ApplicationComponent getApplicationComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getApplicationComponent().inject(this);
    }

    public static GpmdpControllerApplication get(Context context) {
        return (GpmdpControllerApplication) context.getApplicationContext();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesApplicationComponentService(name)) {
            return getApplicationComponent();
        }
        return super.getSystemService(name);
    }
}
