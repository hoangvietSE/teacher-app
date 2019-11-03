package com.trinhbk.lecturelivestream.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public enum AppPreferences {

    INSTANCE;

    private SharedPreferences preferences;

    public static void init(Context context) {
        INSTANCE.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * GET & SET STRING
     * @return
     */
    public String getKeyString(String keyName) {
        return preferences.getString(keyName, "");
    }

    public boolean setKeyString(String keyName, String value) {
        return preferences.edit().putString(keyName, value).commit();
    }

    /**
     * GET & SET BOOLEAN
     * @return
     */
    public boolean getKeyBoolean(String keyName) {
        return preferences.getBoolean(keyName, false);
    }

    public boolean setKeyBoolean(String keyName, boolean value) {
        return preferences.edit().putBoolean(keyName, value).commit();
    }

}