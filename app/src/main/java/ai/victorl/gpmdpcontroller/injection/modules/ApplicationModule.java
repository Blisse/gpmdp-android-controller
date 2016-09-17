package ai.victorl.gpmdpcontroller.injection.modules;

import android.app.Application;
import android.content.Context;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpManager;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpSettings;
import ai.victorl.gpmdpcontroller.data.storage.GpmdpSharedPreferences;
import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import ai.victorl.gpmdpcontroller.ui.views.DefaultViewContainer;
import ai.victorl.gpmdpcontroller.ui.views.ViewContainer;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    protected final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationScope
    Context provideContext() {
        return application;
    }

    @Provides
    ViewContainer provideViewContainer() {
        return new DefaultViewContainer();
    }

    @Provides
    LocalSettings provideLocalSettings(@ApplicationScope Context context) {
        return new GpmdpSharedPreferences(context);
    }

    @Provides
    GpmdpLocalSettings provideGpmdpLocalSettings(LocalSettings localSettings) {
        return new GpmdpSettings(localSettings);
    }

    @Provides
    GpmdpController provideGpmdpController(GpmdpLocalSettings gpmdpLocalSettings) {
        return new GpmdpManager(gpmdpLocalSettings);
    }
}
