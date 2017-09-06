package com.protovate.verity;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.protovate.verity.service.GeofenceTransitionsIntentService;
import com.protovate.verity.service.ImageUploadService;
import com.protovate.verity.service.VideoUploadService;
import com.protovate.verity.ui.ForgotPasswordActivity;
import com.protovate.verity.ui.LoginActivity;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.NotificationsActivity;
import com.protovate.verity.ui.SplashActivity;
import com.protovate.verity.ui.adapters.NotificationsAdapter;
import com.protovate.verity.ui.credits.BuyCreditsActivity;
import com.protovate.verity.ui.credits.PaymentMethodActivity;
import com.protovate.verity.ui.fragments.BuyCreditsFragment;
import com.protovate.verity.ui.fragments.MainFragment;
import com.protovate.verity.ui.fragments.PreviousJobsFragment;
import com.protovate.verity.ui.fragments.ProfileFragment;
import com.protovate.verity.ui.fragments.VLockActiveFragment;
import com.protovate.verity.ui.jobs.PreviousJobsActivity;
import com.protovate.verity.ui.registration.CreateAccountActivity;
import com.protovate.verity.ui.registration.RecordAudioActivity;
import com.protovate.verity.ui.vlock.ChangePasswordActivity;
import com.protovate.verity.ui.vlock.IdentityCheckActivity;
import com.protovate.verity.ui.vlock.LocationActivity;
import com.protovate.verity.ui.vlock.active.AddNoteActivity;
import com.protovate.verity.ui.vlock.active.ListenAudioActivity;
import com.protovate.verity.ui.vlock.active.ViewAudiosActivity;
import com.protovate.verity.ui.vlock.active.ViewNotesActivity;
import com.protovate.verity.ui.vlock.active.ViewPhotosActivity;
import com.protovate.verity.ui.vlock.active.ViewVideosActivity;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Singleton;

import dagger.Component;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Yan on 5/20/15.
 */
public class App extends MultiDexApplication {

    @Singleton
    @Component(modules = AppModule.class)
    public interface AppComponent {
        void inject(App app);

        void inject(LoginActivity login);

        void inject(MainActivity main);

        void inject(CreateAccountActivity register);

        void inject(ForgotPasswordActivity forgotPassword);

        void inject(SplashActivity splashActivity);

        void inject(MainFragment mainFragment);

        void inject(ProfileFragment mainFragment);

        void inject(ChangePasswordActivity profileSettingActivity);

        void inject(IdentityCheckActivity identityCheck);

        void inject(RecordAudioActivity recordAudio);

        void inject(com.protovate.verity.ui.registration.IdentityCheckActivity identityCheckActivity);

        void inject(LocationActivity locationActivity);

        void inject(VLockActiveFragment vLockActiveFragment);

        void inject(BuyCreditsActivity buyCreditsActivity);

        void inject(AddNoteActivity addNoteActivity);

        void inject(ViewNotesActivity viewNotesActivity);

        void inject(ListenAudioActivity listenAudioActivity);

        void inject(ImageUploadService uploadService);

        void inject(ViewPhotosActivity viewPhotosActivity);

        void inject(VideoUploadService videoUploadService);

        void inject(ViewVideosActivity viewVideosActivity);

        void inject(ViewAudiosActivity viewAudiosActivity);

        void inject(PreviousJobsActivity previousJobsActivity);

        void inject(PreviousJobsFragment previousJobsFragment);

        void inject(PaymentMethodActivity paymentMethodActivity);

        void inject(NotificationsActivity notificationsActivity);

        void inject(NotificationsAdapter notificationsAdapter);

        void inject(BuyCreditsFragment buyCreditsFragment);

        void inject(GeofenceTransitionsIntentService geofenceTransitionsIntentService);
    }

    public AppComponent component;

    @Override public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
        Fresco.initialize(this);
        Timber.plant(new Timber.DebugTree());

        component = DaggerApp_AppComponent.builder().appModule(new AppModule(this)).build();
        component().inject(this);

        Fabric.with(this, new Crashlytics());
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppComponent component() {
        return component;
    }
}
