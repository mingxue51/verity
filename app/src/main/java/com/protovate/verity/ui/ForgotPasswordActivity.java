package com.protovate.verity.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.responses.Info;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.utils.Utils;
import com.rengwuxian.Yanrialedittext.YanrialEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 5/27/15.
 */
public class ForgotPasswordActivity extends Activity implements Callback<Info> {
    @Inject
    ApiClient apiClient;
    @InjectView(R.id.email)
    YanrialEditText mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);
    }

    @OnEditorAction(R.id.email) boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            String email = mEmail.getText().toString();

            if (Utils.isValidEmail(email)) {
                apiClient.forgotPassword(email, this);
            } else {
                mEmail.setError(getString(R.string.email_address_error));
            }

            return true;
        }
        return false;
    }

    @Override
    public void success(Info info, Response response) {
        if (info.isSuccess()) {
            Toast.makeText(this, R.string.new_password_sent, Toast.LENGTH_SHORT).show();
        } else {
            for (Info.Error e : info.getErrors()) {
                if (e.getField().equals("email")) mEmail.setError(e.getMessage());
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
