package com.protovate.verity.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.ui.navigation.NavigationDrawerCallbacks;
import com.protovate.verity.ui.navigation.NavigationItem;

import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private View mSelectedView;
    private int mSelectedPosition;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
        mData = data;
    }

    public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
        return mNavigationDrawerCallbacks;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_row, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(v1 -> {
                    if (mSelectedView != null) {
                        mSelectedView.setSelected(false);
                    }

                    mSelectedPosition = viewHolder.getAdapterPosition();
                    v1.setSelected(true);

                    mSelectedView = v1;
                    if (mNavigationDrawerCallbacks != null)
                        mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(viewHolder.getAdapterPosition());

                    if (mData.get(i).isSelected()) {
                        viewHolder.dot.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.dot.setVisibility(View.GONE);
                    }
                    viewHolder.dot.setVisibility(View.VISIBLE);
                    for (int c = 0; c < mData.size(); c++) {
                        if (c == mSelectedPosition) {
                            mData.get(c).setSelected(true);
                        } else {
                            mData.get(c).setSelected(false);
                        }
                    }

                    notifyDataSetChanged();
                }
        );

        mData.get(0).setSelected(true);

        viewHolder.itemView.setBackgroundResource(R.drawable.row_selector);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(mData.get(i).getText());
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(i).getDrawable(), null, null, null);

        if (mData.get(i).isSelected()) {
            viewHolder.dot.setVisibility(View.VISIBLE);
        } else {
            viewHolder.dot.setVisibility(View.GONE);
        }

        if (mSelectedPosition == i) {
            if (mSelectedView != null) {
            }

            mSelectedPosition = i;
            mSelectedView = viewHolder.itemView;
            mSelectedView.setSelected(true);
        }
    }


    public void selectPosition(int position) {
        mSelectedPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView dot;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            dot = (ImageView) itemView.findViewById(R.id.dot);
        }
    }
}