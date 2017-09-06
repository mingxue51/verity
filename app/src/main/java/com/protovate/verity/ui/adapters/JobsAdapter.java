package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.ui.jobs.JobDetailsActivity;
import com.protovate.verity.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/19/15.
 */
public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {
    public List<Jobs.Data.Item> mItems;
    private Context mContext;

    public JobsAdapter(List<Jobs.Data.Item> items) {
        this.mItems = items;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        mContext = parent.getContext();
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mLocation.setText(mItems.get(position).getAddressLine1().trim() + " " +
                (TextUtils.isEmpty(mItems.get(position).getAddressLine2()) ? "" : mItems.get(position).getAddressLine2().trim()));

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(DateTimeZone.UTC);

        DateTime unlockTime = dtf.parseDateTime(mItems.get(position).getUnlockAt());

        DateTimeFormatter outputFormatter = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(DateTimeZone.getDefault());

        holder.mDate.setText(outputFormatter.print(unlockTime));
        String date = holder.mDate.getText().toString();
        holder.mDate.setText(Utils.getDate(date) + date.substring(10));
        holder.mItem.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, JobDetailsActivity.class);
            intent.putExtra("job", mItems.get(position));
            mContext.startActivity(intent);
        });
    }

    @Override public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.location) TextView mLocation;
        @InjectView(R.id.date) TextView mDate;
        @InjectView(R.id.item) LinearLayout mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
