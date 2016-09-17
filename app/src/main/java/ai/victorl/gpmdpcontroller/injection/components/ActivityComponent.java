package ai.victorl.gpmdpcontroller.injection.components;

import android.app.Activity;
import android.content.Context;

import ai.victorl.gpmdpcontroller.injection.modules.ActivityModule;
import ai.victorl.gpmdpcontroller.injection.scopes.ActivityScope;
import ai.victorl.gpmdpcontroller.injection.scopes.PerActivity;
import ai.victorl.gpmdpcontroller.ui.controller.ControllerView;
import ai.victorl.gpmdpcontroller.ui.views.MainActivity;
import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                ActivityModule.class
        }
)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(ControllerView view);

    Activity activity();
    @ActivityScope Context context();
}
