package com.protovate.verity.ui.vlock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Location;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.Vlock;
import com.protovate.verity.data.responses.LockResponse;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.service.BackgroundLocationService;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.utils.Utils;
import com.rengwuxian.Yanrialedittext.YanrialEditText;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Yan on 6/13/15.
 */
public class   LocationActivity extends BaseActivity implements OnMapReadyCallback {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.houseNumber) YanrialEditText mHouseNumber;
    @InjectView(R.id.street) YanrialEditText mStreet;
    @InjectView(R.id.unitApartment) YanrialEditText mUnitApartment;
    @InjectView(R.id.city) YanrialEditText mCity;
    @InjectView(R.id.state) YanrialEditText mState;
    @InjectView(R.id.country) YanrialEditText mCountry;
    @InjectView(R.id.zip) YanrialEditText mZip;
    @InjectView(R.id.notes) YanrialEditText mNotes;
    @InjectView(R.id.viewFlipper) ViewFlipper mViewFlipper;

    private GoogleMap mGoogleMap;
    private Vlock vlock;
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ((App) getApplication()).component().inject(this);

        vlock = (Vlock) getIntent().getSerializableExtra("vlock");

        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Location");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.btnStart:

                checkDataAndLock();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            startService(new Intent(this, BackgroundLocationService.class));
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe public void gotLocation(Location location) {
        this.location = location;
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses.get(0) != null) {
                    mCity.setText(addresses.get(0).getLocality());
                    mState.setText(addresses.get(0).getCountryName());
                    mCountry.setText(addresses.get(0).getCountryName());
                    mStreet.setText(addresses.get(0).getThoroughfare());
                    mHouseNumber.setText(addresses.get(0).getSubThoroughfare());
                    mZip.setText(addresses.get(0).getPostalCode());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeSpace(EditText editText) {
        String text = editText.getText().toString();
        if (text.startsWith(" ")) {
            text = text.substring(1);
            editText.setText(text);
            removeSpace(editText);
        }
    }

    private void checkDataAndLock() {
        removeSpace(mZip);
        removeSpace(mStreet);
        removeSpace(mHouseNumber);
        removeSpace(mCity);
        removeSpace(mState);
        removeSpace(mCountry);
        Utils.hideKeyboard(mZip, this);
        String street = mStreet.getText().toString();
        String houseNumber = mHouseNumber.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getText().toString();
        String country = mCountry.getText().toString();
        String zip = mZip.getText().toString();

        if (TextUtils.isEmpty(street)) {
            mStreet.setError("Please enter street address");
        } else if (TextUtils.isEmpty(city)) {
            mCity.setError("Please enter city name");
        } else if (TextUtils.isEmpty(state)) {
            mState.setError("Please enter state name");
        } else if (TextUtils.isEmpty(country)) {
            mCountry.setText("Please enter country name");
        } else if (TextUtils.isEmpty(zip)) {
            mZip.setError("Please enter zip code");
        } else {
            YanrialDialog md = new YanrialDialog.Builder(this)
                    .content("Please wait")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            String streetValue = (!TextUtils.isEmpty(mUnitApartment.getText().toString()))
                    ? (mUnitApartment.getText().toString() + ", " + street) : street;

            apiClient.lock(
                    new TypedFile("audio/mp3", vlock.getVoiceFile()),
                    new TypedFile("image/*", vlock.getPhoto()),
                    vlock.getVoiceAnswer(),
                    streetValue, houseNumber, city, state, country, zip,
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()),
                    mNotes.getText().toString(),
                    profile.getAccessToken(),
                    new Callback<LockResponse>() {
                        @Override public void success(LockResponse lockResponse, Response response) {
                            if (lockResponse.isSuccess()) {
                                profile.setLatestLockId(lockResponse.getData().getId());
                                Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                                intent.putExtra("vlock_data", lockResponse.getData());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                //mViewFlipper.showPrevious();
                                md.dismiss();
                                for (LockResponse.Error e : lockResponse.getErrors()) {
                                    Toast.makeText(LocationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override public void failure(RetrofitError error) {
                            //mViewFlipper.showPrevious();
                            md.dismiss();
                        }
                    }
            );
        }
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_menu, menu);
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enable GPS in order to fetch your location.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
