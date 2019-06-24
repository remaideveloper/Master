package com.alegangames.master;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;

public class App extends Application {
    private static final int MAX_SIZE = 10000000;
    public static final String TAG = "App";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Picasso picasso = new Picasso.Builder(getApplicationContext()).memoryCache(new LruCache(MAX_SIZE)).build();
        Picasso.setSingletonInstance(picasso);
        GlideBuilder builder = new GlideBuilder();

        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(this, MAX_SIZE));
        builder.setMemoryCache(new LruResourceCache(MAX_SIZE));
        builder.setBitmapPool(new LruBitmapPool(MAX_SIZE));

        GlideApp.init(this,builder);
        MobileAds.initialize(this, Config.ADMOB_APP_ID);
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
