package com.remaistudio.master.ads.admob;

import android.os.Bundle;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.remaistudio.master.Config;

public class AdMobRequest {
    public static final String EXTRA_ACR_KEY = "max_ad_content_rating";
    public static final String EXTRA_ACR_VALUE_G = "G"; //3+
    public static final String EXTRA_ACR_VALUE_PG = "PG"; //7+
    public static final String EXTRA_ACR_VALUE_T = "T"; //12+
    public static final String EXTRA_ACR_VALUE_MA = "MA"; //18+

    private static Bundle sExtraBundle = new Bundle();

    public static AdRequest getRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        builder.addTestDevice(Config.DEVICE_ID[0]);
        builder.addTestDevice(Config.DEVICE_ID[1]);
        builder.addTestDevice(Config.DEVICE_ID[2]);
        builder.addNetworkExtrasBundle(AdMobAdapter.class, sExtraBundle);
        return builder.build();
    }

    public static void setExtraBundle(String key, String value) {
        sExtraBundle.putString(key, value);
    }
}
