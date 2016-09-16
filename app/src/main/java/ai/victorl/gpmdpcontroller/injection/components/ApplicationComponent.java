package ai.victorl.gpmdpcontroller.injection.components;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;
import ai.victorl.gpmdpcontroller.injection.modules.ApplicationModule;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import dagger.Component;

@Singleton
@Component(
        modules = ApplicationModule.class
)
public interface ApplicationComponent {
    void inject(Application application);

    Application application();
    @ApplicationScope Context context();
    LocalSettings localSettings();
    GpmdpLocalSettings gpmdpLocalSettings();
    GpmdpController gpmdpController();
}
