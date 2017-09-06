package com.protovate.verity.ui.vlock.active;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.protovate.verity.data.responses.Photos;
import com.protovate.verity.data.responses.Success;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.adapters.ViewPhotosAdapter;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 7/13/15.
 */
public class ViewPhotosActivity extends BaseActivity implements Callback<Photos> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.pager) ViewFlipper mPager;
    @InjectView(R.id.noContent) TextView mNoContent;

    private YanrialDialog md;
    private ViewPhotosAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Attached Photos");


        md = new YanrialDialog.Builder(this)
                .content("Please wait...")
                .cancelable(false)
                .progress(true, 0).show();

        if (getIntent().getIntExtra("lock_id", 0) != 0) {
            apiClient.getVlockPhotos(String.valueOf(getIntent().getIntExtra("lock_id", 0)), "photo", profile.getAccessToken(), this);
        } else if (profile.getLatestLockId() != 0) {
            apiClient.getVlockPhotos(String.valueOf(profile.getLatestLockId()), "photo", profile.getAccessToken(), this);
        }
    }


    @Override public void success(Photos photos, Response response) {
        md.dismiss();
        if (photos.data.getItems().length > 0) {
            mList.setLayoutManager(new GridLayoutManager(this, 4));
            mAdapter = new ViewPhotosAdapter(this, Arrays.asList(photos.getData().getItems()));
            mList.setAdapter(mAdapter);
        } else {
            mPager.showNext();
            mNoContent.setText("No photos uploaded yet.");
        }
    }

    @Override public void failure(RetrofitError error) {
        md.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnEdit:
                if (mAdapter != null) {
                    menu.findItem(R.id.btnEdit).setVisible(false);
                    menu.findItem(R.id.btnDelete).setVisible(true);

                    mAdapter.editable(true);
                }
                return true;
            case R.id.btnDelete:
                menu.findItem(R.id.btnEdit).setVisible(true);
                menu.findItem(R.id.btnDelete).setVisible(false);

                mAdapter.editable(false);

                String ids = "";
                for (Photos.Data.Item photo : mAdapter.getItems()) {
                    if (photo.isChecked()) {
                        ids += photo.getId() + ",";
                    }
                }
                md = new YanrialDialog.Builder(this)
                        .content("Please wait...")
                        .cancelable(false)
                        .progress(true, 0).show();

                if (ids.length() > 0) {
                    apiClient.delete(String.valueOf(profile.getLatestLockId()), ids.substring(0, ids.length() - 1), profile.getAccessToken(), new Callback<Success>() {
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
