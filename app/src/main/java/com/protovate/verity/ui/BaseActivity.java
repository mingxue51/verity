package com.protovate.verity.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.protovate.verity.R;

/**
 * Created by Yan on 6/4/15.
 */

public class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
    }

    public void setBackButton(ActionBar actionBar) {
        setStatusBarColor();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setTitle(ActionBar actionBar, String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(title);
        if (title.contains("Verity")) {
            ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.nav_app_logo, 0, 0, 0);
        } else {
            ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        actionBar.setTitle("");
    }

    public void hideNotifications() {
        mToolbar.findViewById(R.id.btnBell).setVisibility(View.GONE);
        mToolbar.findViewById(R.id.notificationCount).setVisibility(View.GONE);
    }

    public void showNotifications() {
        mToolbar.findViewById(R.id.btnBell).setVisibility(View.VISIBLE);
        mToolbar.findViewById(R.id.notificationCount).setVisibility(View.VISIBLE);
    }

    public void setNotificationCount(int count) {
        ((TextView) mToolbar.findViewById(R.id.notificationCount)).setText(String.valueOf(count));
        mToolbar.findViewById(R.id.btnBell).setOnClickListener(v ->
                        startActivity(new Intent(this, NotificationsActivity.class))
        );
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.myPrimaryColor));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);
        return true;
    }
}
