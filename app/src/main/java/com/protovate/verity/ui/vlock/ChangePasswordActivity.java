package com.protovate.verity.ui.vlock;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Success;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.rengwuxian.Yanrialedittext.YanrialEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 6/5/2015.
 */


public class ChangePasswordActivity extends BaseActivity implements Callback<Success> {

    @Inject
    ApiClient apiClient;

    @Inject
    Profile profile;

    @InjectView(R.id.profile_oldpassword)
    YanrialEditText mOldPassWord;
    @InjectView(R.id.profile_newpassword)
    YanrialEditText mNewPassWord;
    @InjectView(R.id.profile_confirmpassword)
    YanrialEditText mConfirmPassWord;

    private YanrialDialog md;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Change Password");

        ((App) getApplication()).component().inject(this);
        ButterKnife.inject(this);
    }

    @OnEditorAction(R.id.profile_newpassword) boolean onEditorAction(int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (mNewPassWord.length() < 6) {
                mNewPassWord.setError(getString(R.string.password_length_error));

            }
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnUpdatePassword) void changePassword() {

        if (!mNewPassWord.getText().toString().equals(mConfirmPassWord.getText().toString()))
            mConfirmPassWord.setError(getString(R.string.retype_password_error));
        else {
            String oldPassword = mOldPassWord.getText().toString();
            String newPassword = mNewPassWord.getText().toString();
            String accessToken = profile.getAccessToken();

            md = new YanrialDialog.Builder(this)
                    .content("Please wait")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            apiClient.changePassword(oldPassword, newPassword, accessToken, this);
        }
    }

    @Override
    public void success(Success user, Response response) {
        md.dismiss();
        if (user.isSuccess()) {
            profile.setPassword(mNewPassWord.getText().toString());
            showAlert();
        } else {
            for (com.protovate.verity.data.responses.Error error : user.errors) {
                Toast.makeText(ChangePasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        // means there is no internet because we dont have status codes in backend
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.DialogTheme);
        builder.setTitle("Your password has been successfully changed!");
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.show();
    }
}
