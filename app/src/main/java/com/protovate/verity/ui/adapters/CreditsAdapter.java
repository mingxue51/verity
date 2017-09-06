package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.data.responses.Credits;
import com.protovate.verity.ui.credits.PaymentMethodActivity;

import java.util.List;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {
    private List<Credits.Data> mDataset;
    private Context mContext;

    public CreditsAdapter(List<Credits.Data> credits) {
        this.mDataset = credits;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemDescription;
        public TextView mItemPrice;
        public Button mBtnBuyNow;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemDescription = (TextView) itemView.findViewById(R.id.itemDescription);
            mItemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            mBtnBuyNow = (Button) itemView.findViewById(R.id.btnBuyNow);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credits, parent, false);
        mContext = parent.getContext();
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mItemPrice.setText(String.format("$%d.%02d", mDataset.get(position).getAmount(), 0));
        holder.mItemDescription.setText(String.valueOf(mDataset.get(position).getCredit()));

        holder.mBtnBuyNow.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PaymentMethodActivity.class);
            intent.putExtra("credits", holder.mItemDescription.getText().toString());
            intent.putExtra("amount", holder.mItemPrice.getText().toString());
            intent.putExtra("id", mDataset.get(position).getId());
            mContext.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
