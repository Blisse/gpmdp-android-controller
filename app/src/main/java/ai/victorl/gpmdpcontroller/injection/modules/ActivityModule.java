package ai.victorl.gpmdpcontroller.injection.modules;

import android.app.Activity;
import android.content.Context;

import ai.victorl.gpmdpcontroller.injection.scopes.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    Activity provideActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    Context provideActivityContext() {
        return activity;
    }
}
