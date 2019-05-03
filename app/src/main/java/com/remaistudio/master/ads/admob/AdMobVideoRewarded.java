package com.remaistudio.master.ads.admob;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.remaistudio.master.BuildConfig;

import static com.remaistudio.master.Config.VIDEO_REWARD_ID;

public final class AdMobVideoRewarded implements LifecycleObserver {

    private static final String TAG = "AdsVideoRewarded";
    private static final String VIDEO_REWARD_TEST_ID = "ca-app-pub-3940256099942544/5224354917";
    private RewardedVideoAd mRewardedVideoAd;
    private RewardedVideoAdListener mRewardedVideoAdListener;
    private boolean mRewarded;
    private Context mContext;

    public interface RewardedVideoAdListener {
        void onAdsClosed(boolean rewarded);

        void onAdsLoaded();
    }

    /**
     * Конструткор
     *
     * @param activity
     */
    public AdMobVideoRewarded(FragmentActivity activity) {
        activity.getLifecycle().addObserver(this);

        mContext = activity.getApplicationContext();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
        mRewardedVideoAd.setRewardedVideoAdListener(new com.google.android.gms.ads.reward.RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if (mRewardedVideoAdListener != null) mRewardedVideoAdListener.onAdsLoaded();
                Log.d(TAG, "onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d(TAG, "onRewardedVideoAdOpened");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d(TAG, "onRewardedVideoStarted");
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.d(TAG, "onRewardedVideoAdClosed");
                onInterfaceListener();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.d(TAG, "onRewarded: "
                        + "currency: " + rewardItem.getType()
                        + " amount: " + rewardItem.getAmount());
                setRewarded(true);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.d(TAG, "onRewardedVideoAdLeftApplication");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d(TAG, "onRewardedVideoAdFailedToLoad code " + i);

                onInterfaceListener();
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
    }

    /**
     * Отображает рекламу
     */
    public void onShow() {
        Log.d(TAG, "onShow");
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.show();
        } else {
            onInterfaceListener();
        }
    }

    public boolean isLoaded() {
        return mRewardedVideoAd != null && mRewardedVideoAd.isLoaded();
    }

    /**
     * Устанавиваем коллбек для передачи событий
     * когда реклама загружена, или реклама закрыта
     */
    public void setRewardedVideoAdListener(RewardedVideoAdListener rewardedVideoAdListener) {
        mRewardedVideoAdListener = rewardedVideoAdListener;
    }

    /**
     * Вызвать метод onAdsClosed интерфейса InterfaceListener
     */
    private void onInterfaceListener() {
        Log.d(TAG, "onInterfaceListener");
        if (mRewardedVideoAdListener != null) {
            mRewardedVideoAdListener.onAdsClosed(isRewarded());
            setRewarded(false);
        }
    }

    /**
     * Загружает рекламу, не проверяя была ли она загружена.
     * При потере соединения с сетью, происходт ошибка загрузки.
     * Но, по неизвестной причине, RewardedVideoAd.isLoaded() возвращает
     * true и показать рекламу после восстановления соединения невозможно.
     * Для обхода этого бага вызываем загрузку без проверки.
     */
    public void forceLoadRewardedVideo() {
        Log.d(TAG, "loadRewardedVideoAd");
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.loadAd(VIDEO_REWARD_ID, AdMobRequest.getRequest());
        }
    }

    /**
     * Загрузить рекламу, если она не загружена
     */
    public void loadRewardedVideoAd() {
        Log.d(TAG, "loadRewardedVideoAd");

        if (mRewardedVideoAd != null && !mRewardedVideoAd.isLoaded()) {
            android.util.Log.d(TAG, "loadRewardedVideoAd: not loaded yet");
            if (BuildConfig.DEBUG) {
                mRewardedVideoAd.loadAd(VIDEO_REWARD_TEST_ID, new AdRequest.Builder().build());
            } else {
                mRewardedVideoAd.loadAd(VIDEO_REWARD_ID, new AdRequest.Builder().build());
            }
        }
    }

    private boolean isRewarded() {
        return mRewarded;
    }

    private void setRewarded(boolean rewarded) {
        mRewarded = rewarded;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (mRewardedVideoAd != null) mRewardedVideoAd.pause(mContext);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (mRewardedVideoAd != null) mRewardedVideoAd.resume(mContext);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        setRewardedVideoAdListener(null);
        if (mRewardedVideoAd != null) mRewardedVideoAd.destroy(mContext);
    }

}
