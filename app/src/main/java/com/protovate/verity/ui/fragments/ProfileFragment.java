package com.protovate.verity.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.vlock.ChangePasswordActivity;
import com.protovate.verity.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yan on 6/5/2015.
 */
public class ProfileFragment extends Fragment {
    @Inject Profile profile;
    @Inject ApiClient apiClient;

    @InjectView(R.id.profile_image) SimpleDraweeView mImage;
    @InjectView(R.id.profile_username) TextView mName;
    @InjectView(R.id.profile_email) EditText mEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);
        ((App) getActivity().getApplication()).component().inject(this);

        ((MainActivity) getActivity()).setTitle(((MainActivity) getActivity()).getSupportActionBar(), "Profile Settings");

        mName.setText(profile.getFirstName() + " " + profile.getLastName());
        mEmail.setText(profile.getEmail());
        mImage.setImageURI(Uri.parse(profile.getPhotoOrig()));

        mEmail.setSelection(mEmail.getText().length());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btnChangePassword) void changePassword() {
        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
    }

    @OnClick(R.id.btnUpdate) void update() {
        if (TextUtils.isEmpty(mEmail.getText().toString()) || !Utils.isValidEmail(mEmail.getText().toString())) {
            mEmail.setError(getString(R.string.email_address_error));
        } else if (!mEmail.getText().toString().equals(profile.getEmail())) {
            YanrialDialog md = new YanrialDialog.Builder(getActivity())
                    .content("Please wait")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            apiClient.update(mEmail.getText().toString(), profile.getAccessToken(), new Callback<User>() {
                @Override public void success(User user, Response response) {
                    md.dismiss();
                    if (user.isSuccess()) {
                        Toast.makeText(getActivity(), "Email address has been changed.", Toast.LENGTH_SHORT).show();
                        profile.setEmail(mEmail.getText().toString());
                    } else {
                        for (User.Error error : user.getErrors()) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override public void failure(RetrofitError error) {
                    md.dismiss();
                    error.printStackTrace();
                }
            });
        }
    }
}
