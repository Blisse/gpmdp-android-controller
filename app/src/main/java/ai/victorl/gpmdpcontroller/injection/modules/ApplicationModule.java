package ai.victorl.gpmdpcontroller.injection.modules;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Singleton;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpController;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpLocalSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpService;
import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpSettings;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestSerializer;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpResponseDeserializer;
import ai.victorl.gpmdpcontroller.data.storage.ApplicationSharedPreferences;
import ai.victorl.gpmdpcontroller.data.storage.LocalSettings;
import ai.victorl.gpmdpcontroller.injection.scopes.ApplicationScope;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@Module
public class ApplicationModule {
    static final int DISK_CACHE_SIZE = 52428800; // 50MB

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
    @Singleton
    LocalSettings provideLocalSettings(@ApplicationScope Context context) {
        return new ApplicationSharedPreferences(context);
    }

    @Provides
    @Singleton
    GpmdpLocalSettings provideGpmdpLocalSettings(LocalSettings localSettings) {
        return new GpmdpSettings(localSettings);
    }

    @Provides
    @Singleton
    GpmdpController provideGpmdpController(GpmdpLocalSettings gpmdpLocalSettings, Gson gson, EventBus eventBus) {
        return new GpmdpService(gpmdpLocalSettings, gson, eventBus);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(GpmdpRequest.class, new GpmdpRequestSerializer())
                .registerTypeAdapter(GpmdpResponse.class, new GpmdpResponseDeserializer())
                .create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        return new OkHttpClient.Builder().cache(cache).build();
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    @Provides
    EventBus provideEventBus() {
        return EventBus.builder()
                .logNoSubscriberMessages(true)
                .logSubscriberExceptions(true)
                .build();
    }
}
