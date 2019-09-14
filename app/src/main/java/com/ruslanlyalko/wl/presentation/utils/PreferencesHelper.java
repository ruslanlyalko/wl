package com.ruslanlyalko.wl.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ruslan Lyalko
 * on 05.10.2018.
 */
public class PreferencesHelper {

    private static final String KEY_NIGHT_MODE = "night";
    private static final String KEY_HIDE_EDIT_MODE = "hide_edit_mode";
    private static final String KEY_OLD_STYLE_CALENDAR = "old_style_calendar";
    private static final String KEY_SNOW = "snow";
    private static PreferencesHelper ourInstance;
    private final SharedPreferences mPrefs;

    private PreferencesHelper(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesHelper getInstance(Context context) {
        if(ourInstance == null)
            ourInstance = new PreferencesHelper(context);
        return ourInstance;
    }

    public boolean getNightMode() {
        return mPrefs.getBoolean(KEY_NIGHT_MODE, false);
    }

    public void setNightMode(boolean isNightModeEnabled) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(KEY_NIGHT_MODE, isNightModeEnabled);
        editor.apply();
        editor.commit();
    }

    public boolean getSnow() {
        return mPrefs.getBoolean(KEY_SNOW, false);
    }
    public void setSnow(boolean snow) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(KEY_SNOW, snow);
        editor.apply();
        editor.commit();
    }

    public boolean getOld() {
        return mPrefs.getBoolean(KEY_OLD_STYLE_CALENDAR, false);
    }
    public void setOld(boolean snow) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(KEY_OLD_STYLE_CALENDAR, snow);
        editor.apply();
        editor.commit();
    }

    public long getHideEditModeMessageDate() {
        return mPrefs.getLong(KEY_HIDE_EDIT_MODE, 0);
    }

    public void setHideEditModeMessageDate(long date) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(KEY_HIDE_EDIT_MODE, date);
        editor.apply();
        editor.commit();
    }
}
