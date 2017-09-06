package com.protovate.verity.ui.vlock.active;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.TypeEvent;
import com.protovate.verity.data.responses.Note;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 6/23/15.
 */
public class AddNoteActivity extends BaseActivity implements Callback<Note> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;
    @InjectView(R.id.date) TextView mDate;
    @InjectView(R.id.note) EditText mNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);
        setStatusBarColor();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getSupportActionBar(), "New Note");

        DateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
        mDate.setText(sdf.format(new Date()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            //save
            case R.id.btnSave:
                Utils.hideKeyboard(mNote, this);
                String note = mNote.getText().toString();
                String id = String.valueOf(getIntent().getIntExtra("id", 0));

                if (TextUtils.isEmpty(note)) {
                    Toast.makeText(this, "Please enter note.", Toast.LENGTH_SHORT).show();
                } else {
                    apiClient.createVlockNote(id, mNote.getText().toString(), profile.getAccessToken(), this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override public void success(Note note, Response response) {
        if (note.isSuccess()) {
            BusProvider.getInstance().post(new TypeEvent());
            finish();
        } else {
            for (User.Error error : note.getErrors()) {
                Toast.makeText(AddNoteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override public void failure(RetrofitError error) {

    }
}
