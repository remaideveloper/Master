package com.alegangames.master.ads.admob;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.BuildConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.alegangames.master.Config;
import com.alegangames.master.R;

public final class AdMobBanner implements LifecycleObserver {
    private static final String TAG = "AdMobBanner";
    private Activity mActivity;
    private AdRequest mAdRequest;
    private AdView mAdView;
    private ViewGroup mViewGroup;

    public AdMobBanner(FragmentActivity fragmentActivity) {
        Log.d(TAG, TAG);
        this.mActivity = fragmentActivity;
        fragmentActivity.getLifecycle().addObserver(this);
    }

    public AdMobBanner(Activity activity) {
        Log.d(TAG, TAG);
        this.mActivity = activity;
    }

    public void onCreate() {
        if (!Config.ADMIN_MODE && !Config.NO_ADS) {
            Log.d(TAG, "onCreate");
            this.mAdView = new AdView(this.mActivity);
            this.mAdView.setAdSize(AdSize.SMART_BANNER);
            if (!BuildConfig.DEBUG)
                this.mAdView.setAdUnitId(Config.BANNER_ID);
            else
                this.mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            this.mAdRequest = AdMobRequest.getRequest();
            this.mViewGroup = (ViewGroup) this.mActivity.findViewById(R.id.bannerLayout);
            if (this.mViewGroup != null) {
                this.mViewGroup.removeAllViews();
                this.mViewGroup.addView(this.mAdView);
                this.mViewGroup.setVisibility(View.VISIBLE);
            }
            if (this.mAdView != null) {
                this.mAdView.loadAd(this.mAdRequest);
                this.mAdView.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnLifecycleEvent(Event.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "onPause");
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    @OnLifecycleEvent(Event.ON_RESUME)
    public void onResume() {
        Log.d(TAG, "onResume");
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (this.mAdView != null) {
            this.mAdView.setVisibility(View.GONE);
            this.mAdView.removeAllViews();
            this.mAdView.setAdListener(null);
            this.mAdView.destroy();
            this.mAdRequest = null;
        }
        if (this.mViewGroup != null) {
            this.mViewGroup.setVisibility(View.GONE);
            this.mViewGroup.removeAllViews();
        }
    }
}
