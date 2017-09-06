package com.protovate.verity.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.protovate.verity.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Yan on 7/31/15.
 */
public class NotificationViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.text) TextView mTextView;
    @Optional @InjectView(R.id.when) TextView mWhen;
    @Optional @InjectView(R.id.btnAccept) Button mBtnAccept;
    @Optional @InjectView(R.id.btnDecline) Button mBtnDecline;
    @Optional @InjectView(R.id.viewFlipper) ViewFlipper mFlipper;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }
}
