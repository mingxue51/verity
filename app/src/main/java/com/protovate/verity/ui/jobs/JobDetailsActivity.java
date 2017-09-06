package com.protovate.verity.ui.jobs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.protovate.verity.R;
import com.protovate.verity.data.responses.Jobs;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.vlock.active.ViewAudiosActivity;
import com.protovate.verity.ui.vlock.active.ViewNotesActivity;
import com.protovate.verity.ui.vlock.active.ViewPhotosActivity;
import com.protovate.verity.ui.vlock.active.ViewVideosActivity;
import com.protovate.verity.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/19/15.
 */
public class JobDetailsActivity extends BaseActivity {
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.date) TextView mDate;
    @InjectView(R.id.location) TextView mLocation;
    @InjectView(R.id.image) SimpleDraweeView mImage;
    @InjectView(R.id.photosCount) TextView mPhotosCount;
    @InjectView(R.id.videosCount) TextView mVideosCount;
    @InjectView(R.id.audioCount) TextView mAudioCount;
    @InjectView(R.id.notesCount) TextView mNotesCount;
    @InjectView(R.id.btnViewPhotos) LinearLayout mBtnViewPhotos;
    @InjectView(R.id.btnViewVideos) LinearLayout mBtnViewVideos;
    @InjectView(R.id.btnViewAudios) LinearLayout mBtnViewAudios;
    @InjectView(R.id.btnViewNotes) LinearLayout mBtnViewNotes;
    @InjectView(R.id.startedAt) TextView mStartedAt;
    @InjectView(R.id.duration) TextView mDuration;
    @InjectView(R.id.endedAt) TextView mEndedAt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "V-Lock Info");

        Jobs.Data.Item job = (Jobs.Data.Item) getIntent().getSerializableExtra("job");

        mName.setText(job.getProvider().getFirstName() + " " + job.getProvider().getLastName());
        mImage.setImageURI(Uri.parse(job.getFilePicture().getOrig()));
        mLocation.setText(job.getAddressLine1() + " " + job.getAddressLine2() + ", " + job.getCity() + ", " + job.getCountry());
        mLocation.setText(mLocation.getText().toString().replace("null", ""));
        mDate.setText(Utils.getDate(job.getCreatedAt()));

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(DateTimeZone.UTC);
        DateTime startTime = dtf.parseDateTime(job.getCreatedAt());
        DateTime endTime = dtf.parseDateTime(job.getUnlockAt());

        String durationHour = String.valueOf((int) ((((endTime.getMillis() - startTime.getMillis()) / 1000) / 60) / 60));
        String durationMinute = String.valueOf((((endTime.getMillis() - startTime.getMillis()) / 1000) / 60) % 60);

        String min = " min";
        if (Integer.parseInt(durationMinute) != 1) min = " mins";
        mDuration.setText(durationHour + " hr " + durationMinute + min);

        DateTimeFormatter outputFormatter = DateTimeFormat
                .forPattern("HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(DateTimeZone.getDefault());
        mStartedAt.setText(outputFormatter.print(startTime));
        mEndedAt.setText(outputFormatter.print(endTime));

        if (job.getPhotoCount() > 0) {
            mPhotosCount.setVisibility(View.VISIBLE);
            mPhotosCount.setText(String.valueOf(job.getPhotoCount()));
        }

        mBtnViewPhotos.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewPhotosActivity.class);
            intent.putExtra("lock_id", job.getId());
            startActivity(intent);
        });

        if (job.getVideoCount() > 0) {
            mVideosCount.setVisibility(View.VISIBLE);
            mVideosCount.setText(String.valueOf(job.getVideoCount()));
        }

        mBtnViewVideos.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewVideosActivity.class);
            intent.putExtra("lock_id", job.getId());
            startActivity(intent);
        });

        if (job.getAudioCount() > 0) {
            mAudioCount.setVisibility(View.VISIBLE);
            mAudioCount.setText(String.valueOf(job.getAudioCount()));
        }

        mBtnViewAudios.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewAudiosActivity.class);
            intent.putExtra("lock_id", job.getId());
            startActivity(intent);
        });

        if (job.getNoteCount() > 0) {
            mNotesCount.setVisibility(View.VISIBLE);
            mNotesCount.setText(String.valueOf(job.getNoteCount()));
        }

        mBtnViewNotes.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewNotesActivity.class);
            intent.putExtra("lock_id", job.getId());
            startActivity(intent);
        });
    }
}
