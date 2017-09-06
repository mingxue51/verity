package com.protovate.verity.ui.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ViewFlipper;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.NotificationEvent;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.LoginActivity;
import com.protovate.verity.ui.adapters.JobsAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 7/15/15.
 */
public class PreviousJobsActivity extends BaseActivity implements Callback<Jobs> {
    @Inject @Named("previous_jobs") Observable<Jobs.Data.Item> mObservable;
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.viewFlipper) ViewFlipper mViewFlipper;

    private ArrayList<Jobs.Data.Item> mJobsList = new ArrayList<>();
    private JobsAdapter mAdapter = new JobsAdapter(mJobsList);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_jobs);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Previous V-Locks");

        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(mAdapter);

        mViewFlipper.showNext();
        mObservable.subscribe(
                job -> {
                    mJobsList.add(job);
                    mAdapter.notifyDataSetChanged();
                }, error -> System.out.println("error = " + error)
        );
//        apiClient.getPreviousLocks24Hrs("provider,audio_count,video_count,photo_count,note_count", profile.getAccessToken(), this);
    }

    @Override public void success(Jobs jobs, Response response) {
        mViewFlipper.showNext();
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new JobsAdapter(Arrays.asList(jobs.data.getItems())));
    }

    @Override public void failure(RetrofitError error) {

    }

    @Subscribe public void onNotificationReceived(NotificationEvent notificationEvent) {
        getInvitations();
    }

    @Override protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        getInvitations();
    }

    @Override protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public void getInvitations() {
        apiClient.me(profile.getAccessToken(), "pending_invitation_count")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            if (user.isSuccess()) {
                                setNotificationCount(user.getData().getInvitationCount());
                                profile.setLockCountToday(user.getData().getLockCount());
                            } else {
                                if (user.errors[0].getField().equals("access_token")) {
                                    profile.setLoggedIn(false);
                                    Intent intent = new Intent(PreviousJobsActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        error -> System.out.println("error = " + error)
                );
    }

}
