package com.alegangames.master.util.preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceManager {

    private static final String TAG = SharedPreferenceManager.class.getSimpleName();
    private static SharedPreferenceManager sPreferenceManager;
    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor sEditor;

    private SharedPreferenceManager(Context context) {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sEditor = sSharedPreferences.edit();
        sEditor.apply();
    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (sPreferenceManager == null) {
            sPreferenceManager = new SharedPreferenceManager(context.getApplicationContext());
        }
        return sPreferenceManager;
    }

    public void putBoolean(String key, boolean value) {
        sEditor.putBoolean(key, value).apply();
    }

    public void putInt(String key, int value) {
        sEditor.putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sEditor.putLong(key, value).apply();
    }

    public void putString(String key, String value) {
        sEditor.putString(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sSharedPreferences.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sSharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sSharedPreferences.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sSharedPreferences.getString(key, defValue);
    }
}
