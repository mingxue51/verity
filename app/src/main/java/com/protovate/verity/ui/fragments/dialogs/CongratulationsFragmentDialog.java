package com.protovate.verity.ui.fragments.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.protovate.verity.R;
import com.protovate.verity.ui.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yan on 8/1/15.
 */
public class CongratulationsFragmentDialog extends DialogFragment {
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogNoBorder);
    }

    public static CongratulationsFragmentDialog newInstance() {
        return new CongratulationsFragmentDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.dialog_congratulations, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @OnClick(R.id.btnLearnMore) void learn() {
        Toast.makeText(getActivity(), "Video coming soon.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnContinueToDashboard) void continueToDashboard() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
