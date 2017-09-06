package com.protovate.verity.ui.credits;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Success;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.utils.Utils;
import com.rengwuxian.Yanrialedittext.YanrialEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 7/22/15.
 */
public class PaymentMethodActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.flipper) ViewFlipper mFlipper;
    @InjectView(R.id.couponCode) YanrialEditText mCouponCode;
    @InjectView(R.id.credits) TextView mCredits;
    @InjectView(R.id.amount) TextView mAmount;
    @InjectView(R.id.textCreditCardName) YanrialEditText mTextCreditCardName;
    @InjectView(R.id.textCreditCardNumber) YanrialEditText mTextCreditCardNumber;
    @InjectView(R.id.textCreditCardExpiry) YanrialEditText mTextCreditCardExpiry;
    @InjectView(R.id.textCreditCardCvv) YanrialEditText mTextCreditCardCvv;
    @InjectView(R.id.btnCreditCardBuy) Button mBtnCreditCardBuy;

    @InjectView(R.id.subtitle) TextView mSubtitle;

    private int planId;
    private int size;
    private YanrialDialog md;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        ButterKnife.inject(this);

        ((App) getApplication()).component().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Buy Credits");

        planId = getIntent().getIntExtra("id", 0);
        mCredits.setText(getIntent().getStringExtra("credits"));
        mAmount.setText(getIntent().getStringExtra("amount"));
        mBtnCreditCardBuy.setText("Pay " + getIntent().getStringExtra("amount"));

        mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));

        setTextWatcher();
        setTextListeners();

        md = new YanrialDialog.Builder(this)
                .cancelable(false)
                .content("Please wait")
                .progress(true, 0)
                .build();

        if (getIntent().getBooleanExtra("coupon_code", false)) {
            mFlipper.setDisplayedChild(1);
            findViewById(R.id.content).setVisibility(View.GONE);
            mSubtitle.setText("Enter Coupon Code");
        }
    }

    private void setTextListeners() {
        mTextCreditCardCvv.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                buyFromCard();
            }
            return false;
        });

        mTextCreditCardNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 16) {
                    mTextCreditCardNumber.setText(editable.toString().substring(0, 16));
                    mTextCreditCardNumber.setSelection(mTextCreditCardNumber.length());
                }
            }
        });
    }

    private void setTextWatcher() {
        mTextCreditCardExpiry.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override public void afterTextChanged(Editable editable) {
                if (size < editable.toString().length()) {
                    if (editable.toString().length() == 2) {
                        mTextCreditCardExpiry.setText(editable.toString() + "/");
                        mTextCreditCardExpiry.setSelection(mTextCreditCardExpiry.length());
                    }
                } else if (editable.toString().length() == 2) {
                    mTextCreditCardExpiry.setText(editable.toString().substring(0, 1));
                    mTextCreditCardExpiry.setSelection(mTextCreditCardExpiry.length());
                }
                size = mTextCreditCardExpiry.length();
            }
        });
    }

    @OnClick(R.id.textCreditCardExpiry) void expiry() {
        mTextCreditCardExpiry.setSelection(mTextCreditCardExpiry.getText().toString().length());
    }

    @OnClick(R.id.btnCouponCodes) void couponCodes() {
        findViewById(R.id.content).setVisibility(View.GONE);
        mFlipper.setDisplayedChild(1);
        mSubtitle.setText("Enter Coupon Code");
    }

    @OnClick(R.id.btnValidateCoupon) void validateCoupon() {
        Utils.hideKeyboard(mCouponCode, this);

        if (Utils.isNetworkAvailable(this)) {
            md = new YanrialDialog.Builder(this)
                    .content("Please wait...")
                    .cancelable(false)
                    .progress(true, 0).show();

            apiClient.purchaseCreditsWithCoupon(mCouponCode.getText().toString(), profile.getAccessToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            coupon -> {
                                md.dismiss();
                                if (coupon.isSuccess())
                                    mFlipper.setDisplayedChild(2);
                                else {
                                    if (coupon.getErrors()[0].getField().equals("code")) {
                                        Toast.makeText(this, coupon.getErrors()[0].getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            error -> {
                                md.dismiss();
                            }
                    );
        } else Toast.makeText(this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnStripe) void stripe() {
        findViewById(R.id.content).setVisibility(View.GONE);
        mFlipper.setDisplayedChild(3);
    }

    @OnClick(R.id.btnCreditCardBuy) void buyFromCard() {
        if (TextUtils.isEmpty(mTextCreditCardName.getText().toString())) {
            mTextCreditCardName.setError("Please enter card holder name");
        } else if (TextUtils.isEmpty(mTextCreditCardNumber.getText().toString()) || mTextCreditCardNumber.getText().toString().length() > 16) {
            mTextCreditCardNumber.setError("Please enter correct credit card number");
        } else if (TextUtils.isEmpty(mTextCreditCardExpiry.getText().toString()) && isExpiryValid()) {
            mTextCreditCardExpiry.setError("Please enter expiry date");
        } else if (TextUtils.isEmpty(mTextCreditCardCvv.getText().toString())) {
            mTextCreditCardCvv.setError("Please enter credit card CVV");
        } else {
            md.show();
            apiClient.purchaseCreditsWithCard(
                    String.valueOf(planId),
                    mTextCreditCardName.getText().toString(),
                    mTextCreditCardNumber.getText().toString(),
                    getExpiryDateMonth(),
                    getExpiryDateYear(),
                    mTextCreditCardCvv.getText().toString(),
                    profile.getAccessToken(),
                    callback);
        }
    }

    public boolean isExpiryValid() {
        boolean valid = true;
        String text = mTextCreditCardExpiry.getText().toString();
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i)) || !Character.toString(text.charAt(i)).equals("/")) {
                valid = false;
            }
        }

        return valid;
    }

    public String getExpiryDateMonth() {
        String date = mTextCreditCardExpiry.getText().toString();
        return date.substring(0, 2);
    }

    public String getExpiryDateYear() {
        String date = mTextCreditCardExpiry.getText().toString();
        return date.substring(3);
    }

    Callback<Success> callback = new Callback<Success>() {
        @Override public void success(Success success, Response response) {
            md.dismiss();
            if (success.isSuccess()) {
                showAlert("You have successfully bought " + mCredits.getText().toString() + " credits");
            } else {
                for (com.protovate.verity.data.responses.Error error : success.getErrors()) {
                    Toast.makeText(PaymentMethodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override public void failure(RetrofitError error) {
            md.dismiss();
        }
    };

    public void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this, R.style.DialogTheme);
        builder.setTitle("Congratulations");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("coupon_code", false)) {
            super.onBackPressed();
        } else {
            if (mFlipper.getDisplayedChild() == 0) {
                super.onBackPressed();
            } else {
                mFlipper.setDisplayedChild(0);
                findViewById(R.id.content).setVisibility(View.VISIBLE);
                mSubtitle.setText("Payment Method");

            }
        }
    }
}
