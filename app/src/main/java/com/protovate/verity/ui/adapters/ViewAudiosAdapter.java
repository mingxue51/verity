package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.data.responses.Audios;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/15/15.
 */
public class ViewAudiosAdapter extends RecyclerView.Adapter<ViewAudiosAdapter.ViewHolder> {
    private List<Audios.Data.Item> mDataset;
    private Context mContext;

    public ViewAudiosAdapter(Context context, List<Audios.Data.Item> items) {
        this.mDataset = items;
        this.mContext = context;

        for (Audios.Data.Item item : mDataset) {
            item.setDuration("00:00");
        }
        notifyDataSetChanged();

        new AsyncTask<String, Void, List<Audios.Data.Item>>() {
            @Override protected List<Audios.Data.Item> doInBackground(String... voids) {
                for (Audios.Data.Item item : mDataset) {
                    MediaPlayer mp = new MediaPlayer();
                    try {

                        mp.setDataSource(context, Uri.parse(item.getFile()));
                        mp.prepare();

                        int durationSeconds = mp.getDuration() / 1000;
                        int durationMinutes = mp.getDuration() / 1000 / 60;

                        if (durationMinutes > 0) {
                            durationSeconds -= durationMinutes * 60;
                        }
                        item.setDuration(String.format("%02d:%02d", durationMinutes, durationSeconds));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return items;
            }

            @Override protected void onPostExecute(List<Audios.Data.Item> items) {
                super.onPostExecute(items);
                notifyDataSetChanged();
            }
        }.execute();

        this.mDataset = items;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.play) LinearLayout mPlay;
        @InjectView(R.id.transcription) TextView mTranscription;
        @InjectView(R.id.duration) TextView mDuration;
        @InjectView(R.id.at) TextView mAt;
        @InjectView(R.id.date) TextView mDate;
        @InjectView(R.id.checkbox) CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_note, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mDataset.get(position).getTranscriptAudio()))
            holder.mTranscription.setText(mDataset.get(position).getTranscriptAudio());

//        try {
//            MediaPlayer mp = new MediaPlayer();
//            mp.setDataSource(mContext, Uri.parse(mDataset.get(position).getFile()));
//            mp.prepare();
//
//            int durationSeconds = mp.getDuration() / 1000;
//            int durationMinutes = mp.getDuration() / 1000 / 60;
//
//            if (durationMinutes > 0) {
//                durationSeconds -= durationMinutes * 60;
//            }
//            holder.mDuration.setText(String.format("%02d:%02d", durationMinutes, durationSeconds));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        holder.mDuration.setText(mDataset.get(position).getDuration());

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);

        DateTime unlockTime = dtf.parseDateTime(mDataset.get(position).getCreatedAt());

        DateTimeFormatter hourFormatter = DateTimeFormat
                .forPattern("HH:mm:ss")
                .withZone(DateTimeZone.getDefault());

        DateTimeFormatter dateTimeFormatter = DateTimeFormat
                .forPattern("MMM dd, yyyy")
                .withZone(DateTimeZone.getDefault());

        if (mDataset.get(position).isCheckbox()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);

            holder.mCheckBox.setOnCheckedChangeListener((compoundButton, b) ->
                            mDataset.get(position).setChecked(b)
            );
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }

        holder.mAt.setText("At " + hourFormatter.print(unlockTime));
        holder.mDate.setText(dateTimeFormatter.print(unlockTime));

        holder.mPlay.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getFile()));
            intent.setDataAndType(Uri.parse(mDataset.get(position).getFile()), "audio/*");
            mContext.startActivity(intent);
        });
    }

    public void editable(boolean editable) {
        for (Audios.Data.Item item : mDataset) {
            item.setCheckbox(editable);
        }
        notifyDataSetChanged();
    }

    public List<Audios.Data.Item> getItems() {
        return mDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
