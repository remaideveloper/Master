package com.alegangames.master.util.rules;

import android.content.Context;

import com.alegangames.master.util.AppUtil;

import java.util.Locale;

public class COPPAHelper {

    private static boolean sChildDirectedTreatment = false;

    public static void setChildDirectedTreatment(Context context) {
        if (AppUtil.getCurrentLocale(context).equals(Locale.US)) {
            sChildDirectedTreatment = true;
        }
    }

    public static boolean isChildDirectedTreatment() {
        return sChildDirectedTreatment;
    }
}
