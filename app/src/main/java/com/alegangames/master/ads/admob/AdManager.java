package com.alegangames.master.ads.admob;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.Config;

import java.util.ArrayList;
import java.util.List;

public class AdManager implements LifecycleObserver {

    private List<AdMobNativeAdvanceUnified> mNativeList = new ArrayList<>();

    public AdManager(FragmentActivity fragmentActivity) {
        fragmentActivity.getLifecycle().addObserver(this);
    }

    public AdMobNativeAdvanceUnified getNativeAd(int id){
        int pos = id-1 % 5;
        if (mNativeList.size()>=pos){
            AdMobNativeAdvanceUnified adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
            mNativeList.add(pos, adMobNativeAdvanceUnified);
            return adMobNativeAdvanceUnified;
        } else {
            return mNativeList.get(pos);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mNativeList.clear();
    }
}
