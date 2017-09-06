package com.protovate.verity.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by Yan on 5/28/2015.
 */
public class NumberGridAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mDigitArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public NumberGridAdapter(Context context) {
        mContext = context;
    }

    public int getCount() {
        return mDigitArray.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView digitView;
        if (convertView == null) {
            digitView = new TextView(mContext);
            digitView.setLayoutParams(new GridView.LayoutParams(90, 90));
            digitView.setBackgroundColor(Color.GRAY);
            digitView.setGravity(Gravity.CENTER);
            digitView.setPadding(2, 2, 2, 2);
            digitView.setText(String.format("%d", mDigitArray[position]));
        } else {
            digitView = (TextView) convertView;
        }
        return digitView;
    }

}