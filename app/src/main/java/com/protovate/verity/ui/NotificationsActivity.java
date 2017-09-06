package com.protovate.verity.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.adapters.NotificationsAdapter;
import com.tonicartos.superslim.LayoutManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/30/15.
 */
public class NotificationsActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Notifications");

        NotificationsAdapter adapter = new NotificationsAdapter(getApplication());

        mRecyclerView.setLayoutManager(new LayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }
}
