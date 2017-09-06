package com.protovate.verity.ui.vlock.active;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Audios;
import com.protovate.verity.data.responses.Success;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.adapters.ViewAudiosAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 7/15/15.
 */
public class ViewAudiosActivity extends BaseActivity implements Callback<Audios> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.pager) ViewFlipper mPager;
    @InjectView(R.id.noContent) TextView mNoContent;

    private YanrialDialog md;
    private ViewAudiosAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Attached Audio Notes");

        md = new YanrialDialog.Builder(this)
                .content("Please wait...")
                .cancelable(false)
                .progress(true, 0).show();

        if (getIntent().getIntExtra("lock_id", 0) != 0) {
            apiClient.getVlockAudios(String.valueOf(getIntent().getIntExtra("lock_id", 0)), "audio", profile.getAccessToken(), this);
        } else if (profile.getLatestLockId() != 0) {
            apiClient.getVlockAudios(String.valueOf(profile.getLatestLockId()), "audio", profile.getAccessToken(), this);
        }
    }

    @Override public void success(Audios audios, Response response) {
        md.dismiss();
        if (audios.data.getItems().length > 0) {
            mList.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ViewAudiosAdapter(ViewAudiosActivity.this, Arrays.asList(audios.data.getItems()));
            mList.setAdapter(mAdapter);

        } else {
            mPager.showNext();
            mNoContent.setText("No audio uploaded yet.");
        }
    }

    @Override public void failure(RetrofitError error) {
        md.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnEdit:
                if (mAdapter != null && mAdapter.getItems().size() > 0) {
                    menu.findItem(R.id.btnEdit).setVisible(false);
                    menu.findItem(R.id.btnDelete).setVisible(true);

                    mAdapter.editable(true);
                }
                return true;
            case R.id.btnDelete:
                menu.findItem(R.id.btnEdit).setVisible(true);
                menu.findItem(R.id.btnDelete).setVisible(false);

                mAdapter.editable(false);

                md = new YanrialDialog.Builder(this)
                        .content("Please wait...")
                        .cancelable(false)
                        .progress(true, 0).show();

                List<String> ids = new ArrayList<>();
                for (Audios.Data.Item audioItem : mAdapter.getItems()) {
                    if (audioItem.isChecked()) {
                        ids.add(String.valueOf(audioItem.getId()));
                    }
                    System.out.println("audioItem = " + audioItem.getTranscriptAudio() + " : " + audioItem.isChecked());
                }

                if (ids.size() > 0) {
                    String idz = "";
                    for (String s : ids) {
                        idz += s + ", ";
                    }

                    apiClient.delete(String.valueOf(profile.getLatestLockId()), idz.substring(0, idz.length() - 2), profile.getAccessToken(), new Callback<Success>() {
                        @Override public void success(Success success, Response response) {
                            finish();
                        }

                        @Override public void failure(RetrofitError error) {
                            md.dismiss();
                        }
                    });
                } else {
                    md.dismiss();
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (getIntent().getBooleanExtra("editable", false)) {
            inflater.inflate(R.menu.edit_menu, menu);
        }

        this.menu = menu;
        return true;
    }
}
