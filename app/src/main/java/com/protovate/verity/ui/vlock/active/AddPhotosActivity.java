package com.protovate.verity.ui.vlock.active;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.protovate.verity.R;
import com.protovate.verity.data.PhotoItem;
import com.protovate.verity.data.PhotosSelected;
import com.protovate.verity.data.Profile;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.service.ImageUploadService;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.adapters.PhotosAdapter;
import com.protovate.verity.utils.PhotoGalleryImageProvider;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/1/15.
 */
public class AddPhotosActivity extends BaseActivity {
    public static final int RESULT_CODE = 100;
    @Inject ApiClient apiClient;
    @Inject Profile profile;
    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.flipper) ViewFlipper mFlipper;

    private PhotosAdapter mAdapter;
    private List<PhotoItem> photoList = new ArrayList<>();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);
        ButterKnife.inject(this);

        setStatusBarColor();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getSupportActionBar(), "Choose Photos");

        new Thread(() -> {
            photoList = new ArrayList<>();
            photoList.add(new PhotoItem(null, null));
            photoList.addAll(PhotoGalleryImageProvider.getAlbumThumbnails(AddPhotosActivity.this));
            mAdapter = new PhotosAdapter(this, photoList);
            runOnUiThread(() -> {
                mFlipper.showNext();
                mList.setLayoutManager(new GridLayoutManager(this, 4));
                mList.setAdapter(mAdapter);
            });
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note, menu);
        return true;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            PhotosSelected photosSelected = new PhotosSelected();
            List<PhotoItem> selectedPhotos = new ArrayList<>();
            selectedPhotos.add(new PhotoItem(PhotosAdapter.outputFileUri, PhotosAdapter.outputFileUri));

            if (selectedPhotos.size() > 0) {
                photosSelected.setPhotos(selectedPhotos);
                Intent intent = new Intent(this, ImageUploadService.class);
                intent.putExtra("photos", Parcels.wrap(photosSelected));
                startService(intent);

                setResult(RESULT_CODE);
            }

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //save
            case R.id.btnSave:
                PhotosSelected photosSelected = new PhotosSelected();
                List<PhotoItem> selectedPhotos = new ArrayList<>();
                for (PhotoItem photoItem : photoList) {
                    if (photoItem.isSelected()) {
                        selectedPhotos.add(photoItem);
                    }
                }

                if (selectedPhotos.size() > 0) {
                    photosSelected.setPhotos(selectedPhotos);
                    Intent intent = new Intent(this, ImageUploadService.class);
                    intent.putExtra("photos", Parcels.wrap(photosSelected));
                    startService(intent);

                    setResult(RESULT_CODE);
                }

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}