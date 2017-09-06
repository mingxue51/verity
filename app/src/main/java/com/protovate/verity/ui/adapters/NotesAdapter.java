package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.data.responses.Notes;
import com.protovate.verity.ui.vlock.active.NoteDetailsActivity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Notes.Items> mDataset;
    private Context mContext;

    public NotesAdapter(List<Notes.Items> credits) {
        this.mDataset = credits;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.timestamp) TextView mTimestamp;
        @InjectView(R.id.text) TextView mText;
        @InjectView(R.id.checkbox) CheckBox mCheckBox;
        @InjectView(R.id.container) LinearLayout mContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mText.setText(String.valueOf(mDataset.get(position).getNotes()));

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(DateTimeZone.UTC);
        DateTime createdAt = dtf.parseDateTime(mDataset.get(position).getCreatedAt());
        DateTime now = DateTime.now().withZone(DateTimeZone.UTC);
        String timestampHour = String.valueOf((int) ((((now.getMillis() - createdAt.getMillis()) / 1000) / 60) / 60));
        String timestampMinute = String.valueOf((((now.getMillis() - createdAt.getMillis()) / 1000) / 60) % 60);

        if (Integer.parseInt(timestampHour) == 0) {
            holder.mTimestamp.setText(timestampMinute + " minutes ago");
        } else holder.mTimestamp.setText(timestampHour + " hr " + timestampMinute + " min ago");

        if (mDataset.get(position).isCheckbox()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);

            holder.mCheckBox.setOnCheckedChangeListener((compoundButton, b) ->
                            mDataset.get(position).setChecked(b)
            );
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }

        holder.mContainer.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, NoteDetailsActivity.class);
            intent.putExtra("note", mDataset.get(position).getNotes());
            mContext.startActivity(intent);
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<Notes.Items> getItems() {
        return mDataset;
    }

    public void editable(boolean editable) {
        for (Notes.Items item : mDataset) {
            item.setCheckbox(editable);
        }
        notifyDataSetChanged();
    }
}
