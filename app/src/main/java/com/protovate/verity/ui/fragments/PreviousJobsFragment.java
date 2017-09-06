package com.protovate.verity.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.adapters.JobsAdapter;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

/**
 * Created by Yan on 7/19/15.
 */
public class PreviousJobsFragment extends Fragment {
    @Inject @Named("previous_jobs") Observable<Jobs.Data.Item> mObservable;

    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.viewFlipper) ViewFlipper mViewFlipper;

    private ArrayList<Jobs.Data.Item> mJobsList = new ArrayList<>();
    private JobsAdapter mAdapter = new JobsAdapter(mJobsList);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_previous_jobs, container, false);
        ButterKnife.inject(this, v);

        ((MainActivity) getActivity()).setTitle(((MainActivity) getActivity()).getSupportActionBar(), "Previous V-Locks");

        ((App) getActivity().getApplication()).component().inject(this);

        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setAdapter(mAdapter);

        mViewFlipper.showNext();
        mObservable.subscribe(
                job -> {
                    mJobsList.add(job);
                    mAdapter.notifyDataSetChanged();
                },
                error ->
                        System.out.println("error = " + error)
        );

        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
