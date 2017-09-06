package com.protovate.verity.ui.vlock.active;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.protovate.verity.R;
import com.protovate.verity.ui.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Yan on 7/1/15.
 */
public class NoteDetailsActivity extends BaseActivity {
    @InjectView(R.id.text) TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Note Details");

        mText.setText(getIntent().getStringExtra("note"));
    }


}
