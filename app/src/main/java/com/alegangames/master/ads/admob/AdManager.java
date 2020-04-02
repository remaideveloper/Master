package com.alegangames.master.ads.admob;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.Config;

import java.util.HashMap;
import java.util.Map;

public class AdManager implements LifecycleObserver {

    AdMobNativeAdvanceUnified adMobNativeAdvanceUnified;
    private static Map<Activity, AdManager> instances =  new HashMap<>();

    public static AdManager getInstance(FragmentActivity fragmentActivity){
        AdManager adManager = instances.get(fragmentActivity);
        if (adManager == null) {
            adManager = new AdManager(fragmentActivity);
            instances.put(fragmentActivity, adManager);
        }
        return adManager;
    }

    private AdManager(FragmentActivity fragmentActivity) {
        adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(fragmentActivity, Config.NATIVE_ADVANCE_ID, 5);
    }

    public void getNativeAd(View viewGroup, int id){
        int pos = (id-1) % 5;
        adMobNativeAdvanceUnified.updateAdvanceView(viewGroup, pos);
//        if (mNativeList.size()>=pos){
//            AdMobNativeAdvanceUnified adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
//            mNativeList.add(pos, adMobNativeAdvanceUnified);
//            return adMobNativeAdvanceUnified;
//        } else {
//            return mNativeList.get(pos);
//        }
    }

    public static void onDestroy(FragmentActivity activity) {
        AdManager instance = instances.get(activity);
        if (instance != null) {
            instance.adMobNativeAdvanceUnified.onDestroy();
            instances.remove(activity);
        }
    }

    public void onDestroy(){
        adMobNativeAdvanceUnified.onDestroy();
    }
}
