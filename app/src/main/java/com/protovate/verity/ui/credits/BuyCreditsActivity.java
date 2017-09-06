package com.protovate.verity.ui.credits;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.NotificationEvent;
import com.protovate.verity.data.responses.Credits;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.LoginActivity;
import com.protovate.verity.ui.adapters.CreditsAdapter;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 6/23/15.
 */
public class BuyCreditsActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.recyclerView) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_credits);
        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Buy Credits");

        ((App) getApplication()).component().inject(this);

        apiClient.credits(profile.getAccessToken(), new Callback<Credits>() {
            @Override public void success(Credits credits, Response response) {
                mLayoutManager = new LinearLayoutManager(BuyCreditsActivity.this);
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new CreditsAdapter(Arrays.asList(credits.data));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override public void failure(RetrofitError error) {

            }
        });
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
                                    Intent intent = new Intent(BuyCreditsActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        error -> System.out.println("error = " + error)
                );
    }

    @OnClick(R.id.btnAddCouponCode) void addCoupon() {
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.putExtra("coupon_code", true);
        startActivity(intent);
    }

}
