package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.protovate.verity.R;
import com.protovate.verity.data.responses.Photos;

import java.util.List;

/**
 * Created by Yan on 7/13/15.
 */
public class ViewPhotosAdapter extends RecyclerView.Adapter<ViewPhotosAdapter.ViewHolder> {
    private List<Photos.Data.Item> mDataset;
    private Context mContext;

    public ViewPhotosAdapter(Context context, List<Photos.Data.Item> credits) {
        this.mDataset = credits;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView mImage;
        public ImageView mCheckIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mCheckIcon = (ImageView) itemView.findViewById(R.id.checkIcon);
            mCheckIcon.setVisibility(View.GONE);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImage.setImageURI(Uri.parse(mDataset.get(position).getFile().getThumb()));

        if (mDataset.get(position).isCheckbox()) {
            holder.mCheckIcon.setVisibility(View.VISIBLE);

            holder.mImage.setOnClickListener(null);
            holder.mImage.setOnClickListener(view -> {
                if (!mDataset.get(position).isChecked()) {
                    holder.mCheckIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checkbox_checked));
                    mDataset.get(position).setChecked(true);
                } else {
                    holder.mCheckIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checkbox_circle));
                    mDataset.get(position).setChecked(false);
                }
            });
        } else {
            holder.mCheckIcon.setVisibility(View.GONE);
            holder.mImage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mDataset.get(position).getFile().getOrig()), "image/*");
                mContext.startActivity(intent);
            });
        }

    }

    public void editable(boolean editable) {
        for (Photos.Data.Item item : mDataset) {
            item.setCheckbox(editable);
        }
        notifyDataSetChanged();
    }

    public List<Photos.Data.Item> getItems() {
        return mDataset;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
