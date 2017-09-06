package com.protovate.verity.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.protovate.verity.R;
import com.protovate.verity.data.VideoItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * Created by Yan on 7/8/15.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    private List<VideoItem> mDataset;
    private Activity mActivity;

    public static Uri outputFileUri;
    public static final int SELFIE_PICTURE = 1;

    public VideosAdapter(Activity activity, List<VideoItem> credits) {
        this.mDataset = credits;
        this.mActivity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView mImage;
        public ImageView mCheckIcon;
        public ImageView mBtnTakePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mCheckIcon = (ImageView) itemView.findViewById(R.id.checkIcon);
            mBtnTakePhoto = (ImageView) itemView.findViewById(R.id.btnTakePhoto);
        }
    }


    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override public void onBindViewHolder(VideosAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset.get(position).getPath() == null) {
            holder.mCheckIcon.setVisibility(View.GONE);
            holder.mBtnTakePhoto.setVisibility(View.VISIBLE);
            holder.mBtnTakePhoto.setOnClickListener(view -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                new File(Environment.getExternalStorageDirectory().getPath() + "/Verity/pictures/").mkdirs();
                File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Verity/pictures/" + "photo" + System.currentTimeMillis() + ".mp4");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                outputFileUri = Uri.fromFile(photo);
                mActivity.startActivityForResult(cameraIntent, SELFIE_PICTURE);
            });

            holder.mBtnTakePhoto.setTag(holder);
        } else {
            holder.mBtnTakePhoto.setVisibility(View.GONE);
            if (mDataset.get(position).getBitmap() != null) {
                holder.mImage.setImageURI(getImageUri(mActivity, mDataset.get(position).getBitmap()));
            }

            if (mDataset.get(position).isSelected()) {
                holder.mCheckIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.checkbox_checked));
                mDataset.get(position).setSelected(true);
            } else {
                holder.mCheckIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.checkbox_circle));
                mDataset.get(position).setSelected(false);
            }

            holder.mImage.setOnClickListener(v -> {
                if (!mDataset.get(position).isSelected()) {
                    holder.mCheckIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.checkbox_checked));
                    mDataset.get(position).setSelected(true);
                } else {
                    holder.mCheckIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.checkbox_circle));
                    mDataset.get(position).setSelected(false);
                }
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
