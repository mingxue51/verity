package com.protovate.verity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Yan on 5/20/15.
 */
public class Profile {
    public SharedPreferences prefs;

    public Profile(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static final String ID = "com.protovate.verity.ID";
    public static final String FIRST_NAME = "com.protovate.verity.FIRST_NAME";
    public static final String LAST_NAME = "com.protovate.verity.LAST_NAME";
    public static final String ACCESS_TOKEN = "com.protovate.verity.ACCESS_TOKEN";
    public static final String EMAIL = "com.protovate.verity.EMAIL";
    public static final String CREDITS = "com.protovate.verity.CREDITS";
    public static final String PHOTO_ORIG = "com.protovate.verity.PHOTO_ORIG";
    public static final String PHOTO_THUMB = "com.protovate.verity.PHOTO_THUMB";
    public static final String LOGGED_IN = "com.protovate.verity.LOGGED_IN";
    public static final String LOCK_COUNT_TODAY = "com.protovate.verity.LOCK_COUNT_TODAY";
    public static final String PASSWORD = "com.protovate.verity.PASSWORD";
    public static final String PUSH_TOKEN = "com.protovate.verity.PUSH_TOKEN";
    public static final String LATEST_LOCK_ID = "com.protovate.verity.LATEST_LOCK_ID";
    public static final String DID_PLAY_SOUND = "com.protovate.verity.DID_PLAY_SOUND";
    public static final String UNIT = "com.protovate.verity.UNIT";
    public static final String UNLOCK = "com.protovate.verity.UNLOCK";

    public void setUnlock(boolean unlock) {
        prefs.edit().putBoolean(UNLOCK, unlock).apply();
    }

    public boolean shouldUnlock() {
        return prefs.getBoolean(UNLOCK, false);
    }

    public void setUnit(String unit) {
        prefs.edit().putString(UNIT, unit).apply();
    }

    public String getUnit() {
        return prefs.getString(UNIT, "");
    }

    public void setDidPlaySound(boolean playSound) {
        prefs.edit().putBoolean(DID_PLAY_SOUND, playSound).apply();
    }

    public boolean didPlaySound() {
        return prefs.getBoolean(DID_PLAY_SOUND, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(LOGGED_IN, false);
    }

    public void setId(int id) {
        prefs.edit().putInt(ID, id).apply();
    }

    public int getId() {
        return prefs.getInt(ID, -1);
    }

    public void setFirstName(String firstName) {
        prefs.edit().putString(FIRST_NAME, firstName).apply();
    }

    public String getFirstName() {
        return prefs.getString(FIRST_NAME, "");
    }

    public int getLatestLockId() {
        return prefs.getInt(LATEST_LOCK_ID, 0);
    }

    public void setLatestLockId(int latestLockId) {
        prefs.edit().putInt(LATEST_LOCK_ID, latestLockId).apply();
    }

    public void setLastName(String lastName) {
        prefs.edit().putString(LAST_NAME, lastName).commit();
    }

    public String getLastName() {
        return prefs.getString(LAST_NAME, "");
    }

    public void setAccessToken(String accessToken) {
        prefs.edit().putString(ACCESS_TOKEN, accessToken).commit();
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, "");
    }

    public void setEmail(String email) {
        prefs.edit().putString(EMAIL, email).apply();
    }

    public String getEmail() {
        return prefs.getString(EMAIL, "");
    }

    public void setCredits(int credits) {
        prefs.edit().putInt(CREDITS, credits).apply();
    }

    public int getCredits() {
        return prefs.getInt(CREDITS, 0);
    }


    public void setLockCountToday(int lockcount) {
        prefs.edit().putInt(LOCK_COUNT_TODAY, lockcount).apply();
    }

    public int getLockCountToday() {
        return prefs.getInt(LOCK_COUNT_TODAY, 0);
    }

    public void setPhotoOrig(String photoOrig) {
        prefs.edit().putString(PHOTO_ORIG, photoOrig).apply();
    }

    public String getPhotoOrig() {
        return prefs.getString(PHOTO_ORIG, "");
    }

    public void setPhotoThumb(String photoThumb) {
        prefs.edit().putString(PHOTO_THUMB, photoThumb).apply();
    }

    public String getPhotoThumb() {
        return prefs.getString(PHOTO_THUMB, "");
    }

    public void setPassword(String password) {
        prefs.edit().putString(PASSWORD, password).apply();
    }

    public String getPassword() {
        return prefs.getString(PASSWORD, "");
    }

    public String getPushToken() {
        return prefs.getString(PUSH_TOKEN, "");
    }

    public void setPushToken(String pushToken) {
        prefs.edit().putString(PUSH_TOKEN, pushToken).commit();
    }

    @Override
    public String toString() {
        return getFirstName() + " | " + getLastName() + "\n" +
                getAccessToken() + " | " + getPhotoOrig();
    }


}
