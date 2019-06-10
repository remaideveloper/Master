package com.alegangames.master.util.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsHelper {

    /**
     * Send FireBaseAnalytics event Version Minecraft PE
     * @param context Context
     * @param event Название события
     * @param paramKey Ключ параметра
     * @param paramValue Значение параметра
     */
    @SuppressLint("MissingPermission")
    public static void sendEvent(Context context, String event, String paramKey, String paramValue){
        Bundle params = new Bundle();
        params.putString(paramKey, paramValue);
        FirebaseAnalytics.getInstance(context.getApplicationContext()).logEvent(event, params);
    }
}
