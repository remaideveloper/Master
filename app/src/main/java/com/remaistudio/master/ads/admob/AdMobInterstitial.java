package com.remaistudio.master.ads.admob;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.remaistudio.master.Config;
import com.remaistudio.master.interfaces.InterfaceCallback;

public final class AdMobInterstitial implements LifecycleObserver {
    private static final String TAG = "AdMobInterstitial";
    private InterstitialAd mInterstitialAd;

    public AdMobInterstitial(FragmentActivity fragmentActivity, String str) {
        Log.d(TAG, TAG);
        this.mInterstitialAd = new InterstitialAd(fragmentActivity.getApplicationContext());
        this.mInterstitialAd.setAdUnitId(str);
        fragmentActivity.getLifecycle().addObserver(this);
    }

    private void onLoadAd() {
        Log.d(TAG, "onLoadAd");
        if (!this.mInterstitialAd.isLoaded() && !this.mInterstitialAd.isLoading()) {
            this.mInterstitialAd.loadAd(AdMobRequest.getRequest());
        }
    }

    public void setAdListener(AdListener adListener) {
        this.mInterstitialAd.setAdListener(adListener);
    }

    public void onShowAd() {
        Log.d(TAG, "onShowAd");
        if (!(Config.ADMIN_MODE || Config.INTERSTITIAL_DISABLE || !this.mInterstitialAd.isLoaded())) {
            this.mInterstitialAd.show();
        }
    }

    public void onEventAfterShow(final InterfaceCallback interfaceCallback) {
        if (isLoaded()) {
            setAdListener(new AdListener() {
                public void onAdClosed() {
                    interfaceCallback.onCallback();
                }
            });
            onShowAd();
            return;
        }
        interfaceCallback.onCallback();
    }

    public boolean isLoaded() {
        return this.mInterstitialAd.isLoaded();
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    public void onDestroy() {
        this.mInterstitialAd.setAdListener(null);
    }

    @OnLifecycleEvent(Event.ON_RESUME)
    public void onResume() {
        onLoadAd();
    }
}
