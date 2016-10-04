package ai.victorl.gpmdpcontroller.injection.components;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpMediaProvider;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpMediaService;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpSocketController;
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
    void inject(Application application);
    void inject(GpmdpMediaService service);

    Application application();
    @ApplicationScope Context context();
    LocalSettings localSettings();
    GpmdpLocalSettings gpmdpLocalSettings();
    GpmdpSocketController gpmdpSocketController();
    GpmdpController gpmdpController();
    GpmdpMediaProvider gpmdpMediaProvider();
    Gson gson();
    OkHttpClient okHttpClient();
    Picasso picasso();
}
