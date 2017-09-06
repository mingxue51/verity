package com.protovate.verity.ui.registration;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.RegisterInfo;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.utils.Utils;
import com.rengwuxian.Yanrialedittext.YanrialEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateAccountActivity extends BaseActivity implements Callback<Info> {
    private static final String EMAIL = "email";
    @Inject ApiClient apiClient;

    @InjectView(R.id.email) YanrialEditText mEmail;
    @InjectView(R.id.password) YanrialEditText mPassword;
    @InjectView(R.id.confirmPassword) YanrialEditText mConfirmPassword;
    @InjectView(R.id.firstName) YanrialEditText mFirstName;
    @InjectView(R.id.lastName) YanrialEditText mLastName;
    private boolean processing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryColor));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Create Account");

        ((App) getApplication()).component().inject(this);
        ButterKnife.inject(this);
    }

    @Override
    public void success(Info info, Response response) {
        processing = false;
        if (!info.isSuccess()) {
            for (Info.Error e : info.getErrors()) {
                if (e.getField().equals(EMAIL)) {
                    mEmail.setError(e.getMessage());
                }
            }
        } else {
            mPassword.requestFocus();
        }
    }

    @Override
    public void failure(RetrofitError error) {
    }

    private void onCheckEmail() {
        String email = mEmail.getText().toString();

        if (!Utils.isValidEmail(email)) {
            mEmail.setError(getString(R.string.email_address_error));
        } else {
            apiClient.checkUserInfo(EMAIL, email, this);
        }
    }

    @OnEditorAction(R.id.email) boolean onEditorAction(int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            onCheckEmail();
            return true;
        }

        return false;
    }

    @OnFocusChange(R.id.email) void onFocusChange(boolean focusChanged) {
        if (!focusChanged) {
            onCheckEmail();
        }
    }


//    @OnClick(R.id.btnCancel)
//    void onCancel() {
//        finish();
//    }

    @OnClick(R.id.btnNext) void onNext() {
        if (!processing) {
            processing = true;
            onCheckEmailAndRegister();
        }
    }

    private void onCheckEmailAndRegister() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError(getString(R.string.error_first_name));
            processing = false;
        } else if (TextUtils.isEmpty(lastName)) {
            mLastName.setError(getString(R.string.error_last_name));
            processing = false;

        } else if (!Utils.isValidEmail(email)) {
            mEmail.setError(getString(R.string.email_address_error));
            processing = false;
        } else if (TextUtils.isEmpty(password)) {
            mPassword.setError("Please enter your password");
            processing = false;
        } else if (password.length() < 6) {
            mPassword.setError(getString(R.string.password_length_error));
            processing = false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPassword.setError("Please enter confirm password");
            processing = false;
        } else if (!password.equals(confirmPassword)) {
            mConfirmPassword.setError(getString(R.string.retype_password_error));
            processing = false;
        } else {
            apiClient.checkUserInfo(EMAIL, email, new Callback<Info>() {
                @Override public void success(Info info, Response response) {
                    processing = false;
                    if (!info.isSuccess()) {
                        for (Info.Error e : info.getErrors()) {
                            if (e.getField().equals(EMAIL)) {
                                mEmail.setError(e.getMessage());
                            }
                        }
                    } else {
                        RegisterInfo tempInfo = new RegisterInfo();
                        tempInfo.setFirstName(firstName);
                        tempInfo.setLastName(lastName);
                        tempInfo.setEmail(email);
                        tempInfo.setPassword(password);
                        Intent intent = new Intent(CreateAccountActivity.this, IdentityCheckActivity.class);
                        intent.putExtra("Register_Info", tempInfo);
                        startActivity(intent);

                    }
                }

                @Override public void failure(RetrofitError error) {

                }
            });

        }
    }
}
