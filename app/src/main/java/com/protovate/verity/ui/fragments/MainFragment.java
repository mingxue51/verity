package com.protovate.verity.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.Yanrialdialogs.AlertDialogWrapper;
import com.facebook.drawee.view.SimpleDraweeView;
import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.service.BackgroundLocationService;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.credits.BuyCreditsActivity;
import com.protovate.verity.ui.jobs.PreviousJobsActivity;
import com.protovate.verity.ui.vlock.IdentityCheckActivity;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 5/30/15.
 */
public class MainFragment extends Fragment {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.image) SimpleDraweeView mImage;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.location) TextView mLocation;
    @InjectView(R.id.credits) TextView mCredits;
    @InjectView(R.id.todayjobs) TextView mLockCount;
    @InjectView(R.id.jobsToday) TextView mJobsToday;

    private Intent mIntent;
    private boolean shouldProceed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, view);

        ((App) getActivity().getApplication()).component().inject(this);
        ((MainActivity) getActivity()).setTitle(((MainActivity) getActivity()).getSupportActionBar(), "Verity");

        mName.setText(profile.getFirstName() + " " + profile.getLastName());
        mImage.setImageURI(Uri.parse(profile.getPhotoOrig()));

        mLockCount.setText(String.valueOf(profile.getLockCountToday()));
        mCredits.setText(String.valueOf(profile.getCredits()));

        return view;
    }

    private void onDisplayInfo() {

        apiClient.me(profile.getAccessToken(), "lock_count_today,pending_invitation_count")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            if (user.isSuccess()) {
                                try {
                                    if (user.getData().getLockCount() == 1) {
                                        mJobsToday.setText("V-Lock Today");
                                    } else {
                                        mJobsToday.setText("V-Locks Today");
                                    }
                                    mLockCount.setText(String.valueOf(user.getData().getLockCount()));
                                    profile.setLockCountToday(user.getData().getLockCount());
                                    mCredits.setText(String.valueOf(user.getData().getCredits()));
                                    profile.setCredits(user.getData().getCredits());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> System.out.println("error = " + error)
                );
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        onDisplayInfo();
        mIntent = new Intent(getActivity(), BackgroundLocationService.class);
        getActivity().startService(mIntent);

        apiClient.step0(profile.getAccessToken()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                stepZero -> shouldProceed = stepZero.isData(),
                error -> Toast.makeText(getActivity(), "No internet", Toast.LENGTH_SHORT).show()
        );
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        getActivity().stopService(mIntent);
    }

    @Subscribe public void gotLocation(com.protovate.verity.data.Location location) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.get(0) != null) {
                mLocation.setText(addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btnStartVlock) void startVlock() {
        if (shouldProceed)
            startActivity(new Intent(getActivity(), IdentityCheckActivity.class));
        else {
            new AlertDialogWrapper.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("We are sorry but you don't have sufficient credits. Please buy some more credits.")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Buy Now", ((dialog1, which1) -> startActivity(new Intent(getActivity(), BuyCreditsActivity.class))))
                    .show();
        }
    }

    @OnClick(R.id.btnBuyCredits) void buyCredits() {
        startActivity(new Intent(getActivity(), BuyCreditsActivity.class));
    }

    @OnClick(R.id.btnPreviousJobs) void previousJobs() {
        startActivity(new Intent(getActivity(), PreviousJobsActivity.class));
    }
}
