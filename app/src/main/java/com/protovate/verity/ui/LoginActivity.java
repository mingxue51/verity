package com.protovate.verity.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.registration.CreateAccountActivity;
import com.protovate.verity.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 5/20/15.
 */
public class LoginActivity extends Activity implements Callback<User> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.email) EditText mEmail;
    @InjectView(R.id.password) EditText mPassword;
    @InjectView(R.id.viewFlipper) ViewFlipper mViewFlipper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((App) getApplication()).component().inject(this);
        ButterKnife.inject(this);

        if (!profile.didPlaySound())
            playSound();

        mPassword.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                login();
            }
            return false;
        });
    }

    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.welcome_to_verity);
        mediaPlayer.start();
        profile.setDidPlaySound(true);
    }

    @OnClick(R.id.btnLogin) void login() {
        Utils.hideKeyboard(mEmail, this);
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (email.length() == 0) {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show();
        } else if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, "Please enter valid email address.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
        } else {
            mViewFlipper.showNext();
            if (Utils.isNetworkAvailable(this)) {
                apiClient.login(email, password, getIntent().getStringExtra("push_token"), "android", Utils.getDeviceId(this), Utils.getDeviceName(), Utils.getDeviceVersion(), this);
            } else {
                Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void success(User user, Response response) {
        if (user.isSuccess()) {
            profile.setId(user.getData().getId());
            profile.setFirstName(user.getData().getFirstName());
            profile.setLastName(user.getData().getLastName());
            profile.setEmail(user.getData().getEmail());
            profile.setAccessToken(user.getData().getAccessToken());
            profile.setCredits(user.getData().getCredits());
            profile.setPhotoOrig(user.getData().getPhoto().getOrig());
            profile.setPhotoThumb(user.getData().getPhoto().getThumb());
            profile.setLoggedIn(true);
            profile.setPassword(mPassword.getText().toString());

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            mViewFlipper.showPrevious();
            for (User.Error e : user.errors) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        mViewFlipper.showPrevious();
    }

    @OnClick(R.id.btnRegister) void register() {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }

    @OnClick(R.id.btnForgotPassword) void forgot() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Email Address");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) Utils.convertDpToPixel(20, this);
        params.rightMargin = (int) Utils.convertDpToPixel(20, this);
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Forgot Password")
                .setMessage("Enter your email address to receive new password")
                .setView(container)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", (alertDialog, whichButton) -> {
                }).create();

        dialog.setOnShowListener(dialog1 -> {
            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(v -> {
                Editable value = input.getText();
                Utils.hideKeyboard(input, LoginActivity.this);
                apiClient.forgotPassword(value.toString(), new Callback<Info>() {
                    @Override public void success(Info info, Response response) {
                        if (info.isSuccess()) {
                            Toast.makeText(LoginActivity.this, "New password is sent to your email", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else
                            Toast.makeText(LoginActivity.this, info.getErrors()[0].getMessage(), Toast.LENGTH_LONG).show();
                        Utils.hideKeyboard(input, LoginActivity.this);
                    }

                    @Override public void failure(RetrofitError error) {

                    }
                });
            });
        });
        dialog.show();
    }
}
