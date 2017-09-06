package com.protovate.verity.ui.vlock.active;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.protovate.verity.R;
import com.protovate.verity.data.VideoItem;
import com.protovate.verity.data.VideoSelected;
import com.protovate.verity.service.VideoUploadService;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.adapters.VideosAdapter;
import com.protovate.verity.utils.VideoProvider;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/8/15.
 */
public class AddVideosActivity extends BaseActivity {
    public static final int RESULT_CODE = 200;

    @InjectView(R.id.list) RecyclerView mList;
    @InjectView(R.id.flipper) ViewFlipper mFlipper;

    private VideosAdapter mAdapter;
    private List<VideoItem> mVideos = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videos);
        ButterKnife.inject(this);

        setStatusBarColor();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getSupportActionBar(), "Choose Videos");

        new Thread(() -> {
            Looper.prepare();
            mVideos = new ArrayList<>();
            mVideos.add(new VideoItem(null, null));
            mVideos.addAll(VideoProvider.getVideos(AddVideosActivity.this));
            mAdapter = new VideosAdapter(this, mVideos);
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
            ArrayList<VideoItem> videos = new ArrayList<>();
            VideoItem item = new VideoItem(null, VideosAdapter.outputFileUri.getPath());

            try {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(VideosAdapter.outputFileUri.getPath(),
                        MediaStore.Images.Thumbnails.MINI_KIND);
                item.setThumb(getRealPathFromURI(getImageUri(this, thumb)));

            } catch (Exception e) {
            }


            videos.add(item);

            VideoSelected videosSelected = new VideoSelected();
            videosSelected.setVideos(videos);

            Intent intent = new Intent(this, VideoUploadService.class);
            intent.putExtra("videos", Parcels.wrap(videosSelected));
            startService(intent);

            setResult(RESULT_CODE);
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
                VideoSelected videosSelected = new VideoSelected();
                List<VideoItem> videos = new ArrayList<>();
                for (VideoItem photoItem : mVideos) {
                    if (photoItem.isSelected()) {
                        photoItem.setBitmap(null);
                        try {
                            photoItem.setThumb(getRealPathFromURI(getImageUri(this, photoItem.getBitmap())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        videos.add(photoItem);
                    }
                }
                videosSelected.setVideos(videos);

                if (videos.size() > 0) {
                    Intent intent = new Intent(this, VideoUploadService.class);
                    intent.putExtra("videos", Parcels.wrap(videosSelected));
                    startService(intent);

                    setResult(RESULT_CODE);
                }

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
