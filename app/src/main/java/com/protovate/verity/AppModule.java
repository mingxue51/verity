package com.protovate.verity;

import android.content.Context;
import android.location.LocationManager;
import android.widget.Toast;

import com.protovate.verity.annotations.ForApplication;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.utils.Utils;
import com.squareup.okhttp.OkHttpClient;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 5/20/15.
 */
@Module
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton @ForApplication Context provideApplicationContext() {
        return app;
    }

    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides @Singleton ApiClient provideApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10 * 60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(10 * 60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(10 * 60 * 1000, TimeUnit.MILLISECONDS);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.API_URL)
                .setRequestInterceptor(request -> {
                    if (!Utils.isNetworkAvailable(provideApplicationContext())) {
                        Toast.makeText(provideApplicationContext(), provideApplicationContext().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(okHttpClient))
                .build();

        return adapter.create(ApiClient.class);
    }

    @Provides @Singleton Profile provideProfile() {
        return new Profile(app);
    }

    @Provides @Named("previous_jobs") Observable<Jobs.Data.Item> getPreviousJobs() {
        return provideApiClient()
                .getPreviousLocks24Hrs("provider,audio_count,video_count,photo_count,note_count", provideProfile().getAccessToken())
                .map(jobs -> Arrays.asList(jobs.data.getItems()))
                .flatMap(Observable::from)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
