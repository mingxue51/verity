package com.protovate.verity.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Credits;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.adapters.CreditsAdapter;
import com.protovate.verity.ui.credits.PaymentMethodActivity;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 8/1/15.
 */
public class BuyCreditsFragment extends Fragment {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.recyclerView) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buy_credits, container, false);
        ((MainActivity) getActivity()).setTitle(((MainActivity) getActivity()).getSupportActionBar(), "Buy Credits");

        ButterKnife.inject(this, v);

        ((App) getActivity().getApplication()).component().inject(this);

        apiClient.credits(profile.getAccessToken(), new Callback<Credits>() {
            @Override public void success(Credits credits, Response response) {
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new CreditsAdapter(Arrays.asList(credits.data));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override public void failure(RetrofitError error) {

            }
        });

        return v;
    }

    @OnClick(R.id.btnAddCouponCode) void addCoupon() {
        Intent intent = new Intent(getActivity(), PaymentMethodActivity.class);
        intent.putExtra("coupon_code", true);
        startActivity(intent);
    }
}
