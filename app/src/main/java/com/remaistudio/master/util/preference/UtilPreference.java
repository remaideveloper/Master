package com.remaistudio.master.util.preference;

import android.content.Context;

import com.remaistudio.master.R;

public class UtilPreference {

    public static boolean isFirstLaunch(Context context) {
        return SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 1) == 1;
    }

    public static boolean isSecondLaunch(Context context) {
        return SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 1) == 2;
    }

    public static void addLaunchPref(Context context) {
        int i = SharedPreferenceManager.getInstance(context).getInt(context.getString(R.string.launch_pref), 1);
        SharedPreferenceManager.getInstance(context).putInt(context.getString(R.string.launch_pref), ++i);
    }
}
