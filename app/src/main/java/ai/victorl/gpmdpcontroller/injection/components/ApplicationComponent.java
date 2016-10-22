package ai.victorl.gpmdpcontroller.injection.components;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import ai.victorl.gpmdpcontroller.GpmdpControllerApplication;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.media.GpmdpMediaController;
import ai.victorl.gpmdpcontroller.data.media.GpmdpMediaProvider;
import ai.victorl.gpmdpcontroller.data.media.GpmdpMediaService;
import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;
import ai.victorl.gpmdpcontroller.injection.modules.ApplicationModule;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(
        modules = ApplicationModule.class
)
public interface ApplicationComponent {
    void inject(GpmdpControllerApplication application);
    void inject(GpmdpMediaService service);

    Application application();
    @ApplicationScope Context context();
    LocalSettings localSettings();
    GpmdpLocalSettings gpmdpLocalSettings();
    GpmdpController gpmdpController();
    GpmdpMediaProvider gpmdpMediaProvider();
    GpmdpMediaController gpmdpMediaController();
    Gson gson();
    OkHttpClient okHttpClient();
    Picasso picasso();
}
