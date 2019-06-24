package com.alegangames.master.util.rules;

import android.content.Context;

import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.preference.UtilPreference;

import java.util.Locale;

public class COPPAHelper {

    /**
     * Узнать является ли пользователь гражданином США,
     * Пометить весь контент как ориентированный на детей.
     */
    public static void setChildDirectedTreatment(Context context, boolean bool) {
        if (AppUtil.getCurrentLocale(context).equals(Locale.US)) {
            UtilPreference.setChildDirectedTreatmentPref(context, bool);
        }
    }

    public static boolean isChildDirectedTreatment(Context context) {
        return UtilPreference.isChildDirectedTreatmentPref(context);
    }
}
