package ai.victorl.gpmdpcontroller.injection.modules;

import android.app.Activity;
import android.content.Context;

import ai.victorl.gpmdpcontroller.injection.scopes.ActivityScope;
import ai.victorl.gpmdpcontroller.ui.views.BaseActivity;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        activity = activity;
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
