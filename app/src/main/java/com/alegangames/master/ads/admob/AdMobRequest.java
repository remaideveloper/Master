package com.alegangames.master.ads.admob;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alegangames.master.util.preference.UtilPreference;
import com.alegangames.master.util.rules.COPPAHelper;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.alegangames.master.Config;

public class AdMobRequest {

    private static final String TAG = AdMobRequest.class.getSimpleName();

    public static final String EXTRA_ACR_KEY = "max_ad_content_rating";
    public static final String EXTRA_UAC_KEY = "tag_for_under_age_of_consent";
    public static final String EXTRA_NPA_KEY = "npa";
    public static final String EXTRA_ACR_VALUE_G = "G"; //3+
    public static final String EXTRA_ACR_VALUE_PG = "PG"; //7+
    public static final String EXTRA_ACR_VALUE_T = "T"; //12+
    public static final String EXTRA_ACR_VALUE_MA = "MA"; //18+
    public static final String EXTRA_NPA_VALUE = "1";

    public static Bundle bundle = new Bundle();
    public static boolean isChildDirectTreatmentPref;


    public static AdRequest getRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        builder.addTestDevice(Config.DEVICE_ID[0]);
        builder.addTestDevice(Config.DEVICE_ID[1]);
        builder.addTestDevice(Config.DEVICE_ID[2]);
        builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
        if (isChildDirectTreatmentPref) {
            builder.tagForChildDirectedTreatment(true);
        }
        return builder.build();
    }

    /**
     * Вызывать каждый раз, для загрузки рекламных ограничений пользователя
     */
    public static void setBundle(Context context) {
        if (UtilPreference.getMaxAdContentRatingPref(context).equals(EXTRA_ACR_VALUE_G)) {
            bundle.putString(EXTRA_ACR_KEY, EXTRA_ACR_VALUE_G);
            Log.d(TAG, "setBundle: getMaxAdContentRatingPref");
        }
        if (UtilPreference.isUserUnderAgeOfConsentPref(context)) {
            bundle.putBoolean(EXTRA_UAC_KEY, true);
            Log.d(TAG, "setBundle: isUserUnderAgeOfConsentPref");
        }
        if (UtilPreference.getNonPersonAdsPref(context).equals(EXTRA_NPA_VALUE)) {
            bundle.putString(EXTRA_NPA_KEY, EXTRA_NPA_VALUE);
            Log.d(TAG, "setBundle: getNonPersonAdsPref");
        }
        if (COPPAHelper.isChildDirectedTreatment(context)) {
            isChildDirectTreatmentPref = true;
            Log.d(TAG, "setBundle: isChildDirectedTreatment");
        }
    }

}
