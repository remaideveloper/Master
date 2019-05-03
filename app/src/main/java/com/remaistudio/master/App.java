package com.remaistudio.master;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;

public class App extends Application {
    public static final String TAG = "App";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Picasso.setSingletonInstance(new Builder(getApplicationContext()).memoryCache(new LruCache(10000000)).build());
//        MobileAds.initialize(this, Config.ADMOB_APP_ID);
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
