package com.protovate.verity.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protovate.verity.R;

/**
 * Created by Yan on 7/7/15.
 */
public class EmptyFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emtpy, container, false);
        return v;
    }
}
