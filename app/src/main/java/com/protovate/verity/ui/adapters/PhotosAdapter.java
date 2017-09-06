package com.protovate.verity.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.protovate.verity.R;
import com.protovate.verity.data.PhotoItem;

import java.io.File;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private List<PhotoItem> mDataset;
    private Activity mActivity;

    public static Uri outputFileUri;
    public static final int SELFIE_PICTURE = 1;

    public Uri getOutputFileUri() {
        return outputFileUri;
    }

    public PhotosAdapter(Activity activity, List<PhotoItem> credits) {
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (mDataset.get(position).getFullImageUri() == null) {
            holder.mCheckIcon.setVisibility(View.GONE);
            holder.mBtnTakePhoto.setVisibility(View.VISIBLE);
            holder.mBtnTakePhoto.setOnClickListener(view -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                new File(Environment.getExternalStorageDirectory().getPath() + "/Verity/pictures/").mkdirs();
                File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Verity/pictures/" + "photo" + System.currentTimeMillis() + ".jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                outputFileUri = Uri.fromFile(photo);
                mActivity.startActivityForResult(cameraIntent, SELFIE_PICTURE);
            });

            holder.mBtnTakePhoto.setTag(holder);
        } else {
            holder.mBtnTakePhoto.setVisibility(View.GONE);

            if (mDataset.get(position).getFullImageUri().toString().contains("file://")) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mDataset.get(position).getThumbnailUri())
                        .setAutoRotateEnabled(true)
                        .build();

                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.mImage.getController())
                        .setImageRequest(request)
                        .build();

                holder.mImage.setController(controller);
            } else {
                holder.mImage.setImageURI(Uri.fromFile(new File(String.valueOf(mDataset.get(position).getThumbnailUri()))));
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
