package com.protovate.verity.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.Yanrialdialogs.AlertDialogWrapper;
import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.NotificationEvent;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.data.responses.LockResponse;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.fragments.BuyCreditsFragment;
import com.protovate.verity.ui.fragments.EmptyFragment;
import com.protovate.verity.ui.fragments.MainFragment;
import com.protovate.verity.ui.fragments.PreviousJobsFragment;
import com.protovate.verity.ui.fragments.ProfileFragment;
import com.protovate.verity.ui.fragments.VLockActiveFragment;
import com.protovate.verity.ui.navigation.NavigationDrawerCallbacks;
import com.protovate.verity.ui.navigation.NavigationDrawerFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements NavigationDrawerCallbacks {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LockResponse.Data lockData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).component().inject(this);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        setTitle(getSupportActionBar(), "Verity");

        if (getIntent().getBooleanExtra("notifications", false))
            startActivity(new Intent(this, NotificationsActivity.class));
        else {
            if (profile.getLatestLockId() != 0) {
                apiClient.getLock(String.valueOf(profile.getLatestLockId()), profile.getAccessToken(), "")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                                    if (profile.shouldUnlock()) {
                                        profile.setUnlock(false);
                                        lockData = response.getData();
                                        apiClient.unloackVlock(String.valueOf(lockData.getId()), lockData.getAddressLine1(), lockData.getAddressLine2(),
                                                lockData.getCity(), lockData.getState(), lockData.getCountry(), lockData.getZip(),
                                                lockData.getLat(), lockData.getLng(), profile.getAccessToken(), new Callback<User>() {
                                                    @Override public void success(User user, Response response) {
                                                        if (user.isSuccess()) {
                                                            profile.setLatestLockId(0);
                                                            getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
                                                        } else {
                                                            for (User.Error e : user.getErrors()) {
                                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override public void failure(RetrofitError error) {

                                                    }
                                                });
                                    } else {
                                        hideNotifications();
                                        setIntent(new Intent().putExtra("vlock_data", response.getData()));
                                        getFragmentManager().beginTransaction().replace(R.id.container, new VLockActiveFragment()).commit();
                                    }
                                }, error ->
                                        System.out.println("error = " + error)
                        );
            }
        }


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
    }


    @Override protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe public void onNotificationReceived(NotificationEvent notificationEvent) {
        getInvitations();
    }

    public void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        if (!gpsEnabled && !networkEnabled) {
            new AlertDialogWrapper.Builder(this)
                    .setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setPositiveButton("Yes", (paramDialogInterface, paramInt) -> {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(myIntent);
                    })
                    .setNegativeButton("No", (paramDialogInterface, paramInt) -> {
                    })
                    .show();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        locationEnabled();
        BusProvider.getInstance().register(this);
        getInvitations();
    }

    void getInvitations() {
        apiClient.me(profile.getAccessToken(), "pending_invitation_count")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            if (user.isSuccess()) {
                                setNotificationCount(user.getData().getInvitationCount());
                                profile.setLockCountToday(user.getData().getLockCount());
                                mNavigationDrawerFragment.setUserData(user.getData().getFirstName() + " " + user.getData().getLastName(), "Credits: " + user.getData().getCredits(), profile.getPhotoOrig());
                            } else {
                                if (user.errors[0].getField().equals("access_token")) {
                                    profile.setLoggedIn(false);
                                    Toast.makeText(this, user.errors[0].getMessage(), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        error -> System.out.println("error = " + error)
                );
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        // Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        switch (position) {
            case 0:
               /* if (getIntent().getSerializableExtra("vlock_data") != null) {
                    getFragmentManager().beginTransaction().replace(R.id.container, new VLockActiveFragment()).commit();
                } else if (getIntent().getBooleanExtra("empty", false)) {
                    getFragmentManager().beginTransaction().replace(R.id.container, new EmptyFragment()).commit();
                } else {
                    getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
                } */
                getFragmentManager().beginTransaction().replace(R.id.container, new EmptyFragment()).commit();
                Handler h = new Handler(); //handler is there so closing the drawer is smoother
                h.postDelayed(() -> {
                    if (profile.getLatestLockId() != 0) {
                        hideNotifications();
                        getFragmentManager().beginTransaction().replace(R.id.container, new VLockActiveFragment()).commit();
                    } else {
                        showNotifications();
                        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
                    }

                }, 250);
                break;
            case 1:
                showNotifications();
                getFragmentManager().beginTransaction().replace(R.id.container, new PreviousJobsFragment()).commit();
                break;
            case 2:
                showNotifications();
                getFragmentManager().beginTransaction().replace(R.id.container, new BuyCreditsFragment()).commit();
                break;
            case 3:
                showNotifications();
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                break;
            case 4:
                apiClient.logout(profile.getAccessToken(), new Callback<Info>() {
                    @Override public void success(Info info, Response response) {
                        if (info.isSuccess()) {
                            unlock();
                            profile.setLoggedIn(false);
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override public void failure(RetrofitError error) {
                    }
                });
            default:
                break;
        }


    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        System.out.println("id = " + id);

        if (id == 16908332) {
            mNavigationDrawerFragment.openDrawer();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void unlock() {
        LockResponse.Data lockData;
        lockData = ((LockResponse.Data) getIntent().getSerializableExtra("vlock_data"));
        try {
            apiClient.unloackVlock(String.valueOf(lockData.getId()), lockData.getAddressLine1(), lockData.getAddressLine2(),
                    lockData.getCity(), lockData.getState(), lockData.getCountry(), lockData.getZip(),
                    lockData.getLat(), lockData.getLng(), profile.getAccessToken(), new Callback<User>() {
                        @Override public void success(User user, Response response) {
                            profile.setLatestLockId(0);
                        }

                        @Override public void failure(RetrofitError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            profile.setLatestLockId(0);
        }
    }
}
