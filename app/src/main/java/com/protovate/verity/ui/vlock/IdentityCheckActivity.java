package com.protovate.verity.ui.vlock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pnikosis.Yanrialishprogress.ProgressWheel;
import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.Vlock;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Yan on 5/30/15.
 */
public class IdentityCheckActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.image) SimpleDraweeView mImage;
    @InjectView(R.id.iconStatus) ImageView mIconStatus;
    @InjectView(R.id.progress_wheel) ProgressWheel mProgressWheel;
    @InjectView(R.id.errorLayout) RelativeLayout mErrorLayout;
    @InjectView(R.id.btnCapturePhoto) Button mBtnCapturePhoto;

    private Uri outputFileUri;
    private File imageFile;
    final int SELFIE_PICTURE = 1;
    private boolean shouldProceed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlock_identity_check);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Identity Check");
    }

    private int getOrientation(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    @OnClick(R.id.btnCapturePhoto) void capturePhoto() {
        if (!shouldProceed) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            new File(Environment.getExternalStorageDirectory().getPath() + "/Verity/ProfilePictures/").mkdirs();
            File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Verity/ProfilePictures/" + "photo" + System.currentTimeMillis() + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            outputFileUri = Uri.fromFile(photo);
            startActivityForResult(cameraIntent, SELFIE_PICTURE);

        } else {
            Vlock vlock = new Vlock();
            vlock.setPhoto(imageFile);
            Intent intent = new Intent(this, VoiceVerificationActivity.class);
            intent.putExtra("vlock", vlock);
            startActivity(intent);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELFIE_PICTURE) {
                    mIconStatus.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);

                    Uri selectedImage = outputFileUri;
                    String selectedImagePath;
                    Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                    if (cursor == null) {
                        selectedImagePath = selectedImage.getPath();
                    } else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        selectedImagePath = cursor.getString(idx);
                    }
                    final File image = new File(selectedImagePath);
                    imageFile = downscaleBitmapAndRotate(4, image);
                    mImage.setImageURI(Uri.fromFile(imageFile));

                    YanrialDialog md = new YanrialDialog.Builder(this)
                            .content("Identifying Face")
                            .progress(true, 0)
                            .show();

                    TypedFile photo = new TypedFile("image/*", imageFile);
                    apiClient.step1(photo, profile.getAccessToken(), new Callback<Info>() {
                        @Override public void success(Info info, Response response) {
                            md.dismiss();

                            if (info.isSuccess()) {
                                mIconStatus.setBackgroundResource(R.drawable.correct_icon_with_green_bg);
                                mIconStatus.setVisibility(View.VISIBLE);
                                shouldProceed = true;
                                mBtnCapturePhoto.setText("Continue");
                            } else {
                                mIconStatus.setBackgroundResource(R.drawable.wrong_icon_with_red_bg);
                                mIconStatus.setVisibility(View.VISIBLE);
                                mErrorLayout.setVisibility(View.VISIBLE);
                                shouldProceed = false;
                            }
                        }

                        @Override public void failure(RetrofitError error) {
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File downscaleBitmapAndRotate(final int sampleSize, final File image) {
        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = sampleSize;
        bitmapOptions.inTargetDensity = 1;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bitmapOptions);
        final Bitmap finalBitmap;

        int orientation = getOrientation(image.getAbsolutePath());
        System.out.println("orientation: " + orientation);
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        finalBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        finalBitmap.setDensity(Bitmap.DENSITY_NONE);
        File f = new File(getApplicationContext().getCacheDir(), "scaledImage" + System.currentTimeMillis()+ ".jpg");
        /*if(f.exists()) {
            f.delete();
            f = new File(getApplicationContext().getCacheDir(), "scaledImage.jpg");
        } */
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
