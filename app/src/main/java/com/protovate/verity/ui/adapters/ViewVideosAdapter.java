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
import com.protovate.verity.data.responses.Videos;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Yan on 7/15/15.
 */
public class ViewVideosAdapter extends RecyclerView.Adapter<ViewVideosAdapter.ViewHolder> {
    private List<Videos.Data.Item> mDataset;
    private Context mContext;

    public ViewVideosAdapter(Context context, List<Videos.Data.Item> items) {
        this.mDataset = items;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView mImage;
        public ImageView mCheckIcon;
        public ImageView mPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mCheckIcon = (ImageView) itemView.findViewById(R.id.checkIcon);
            mPlay = (ImageView) itemView.findViewById(R.id.play);

            mCheckIcon.setVisibility(View.GONE);
            mPlay.setVisibility(View.VISIBLE);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(mContext).load(mDataset.get(position).getFile2().getThumb()).resize(200, 160).centerCrop().into(holder.mImage);
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getFile()));
                intent.setDataAndType(Uri.parse(mDataset.get(position).getFile()), "video/mp4");
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<Videos.Data.Item> getItems() {
        return mDataset;
    }


    public void editable(boolean editable) {
        for (Videos.Data.Item item : mDataset) {
            item.setCheckbox(editable);
        }
        notifyDataSetChanged();
    }
}
