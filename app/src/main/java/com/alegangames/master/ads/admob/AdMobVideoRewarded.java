package com.alegangames.master.ads.admob;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.R;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.billing.PurchaseManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.alegangames.master.BuildConfig;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import static com.alegangames.master.Config.VIDEO_REWARD_ID;

public final class AdMobVideoRewarded implements LifecycleObserver {

    private static final String TAG = AdMobVideoRewarded.class.getSimpleName();
    private static final String VIDEO_REWARD_TEST_ID = "ca-app-pub-3940256099942544/5224354917";
    private RewardedVideoAd mRewardedVideoAd;
    private Context mContext;
    private FragmentActivity mActivity;
    private boolean isRewarded;
    private static int COUNT_COINS = 10;

    /**
     * Конструткор
     *
     * @param activity
     */
    public AdMobVideoRewarded(FragmentActivity activity) {
        Log.d(TAG, "AdMobVideoRewarded");
        activity.getLifecycle().addObserver(this);
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
    }

    /**
     * Отображает рекламу
     */
    public void onShow() {
        Log.d(TAG, "onShow");
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.show();
        }
    }

    private boolean isLoaded() {
        return mRewardedVideoAd != null && mRewardedVideoAd.isLoaded();
    }

    /**
     * Загружает рекламу, не проверяя была ли она загружена.
     * При потере соединения с сетью, происходт ошибка загрузки.
     * Но, по неизвестной причине, RewardedVideoAd.isLoaded() возвращает
     * true и показать рекламу после восстановления соединения невозможно.
     * Для обхода этого бага вызываем загрузку без проверки.
     */
    public void forceLoadRewardedVideo() {
        Log.d(TAG, "forceLoadRewardedVideo");
        if (mRewardedVideoAd != null) {
            if (!BuildConfig.DEBUG)
                mRewardedVideoAd.loadAd(VIDEO_REWARD_ID, AdMobRequest.getRequest());
            else
                mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", AdMobRequest.getRequest());
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

    private void onReward() {
        Log.d(TAG, "onReward");
        //Если пользователь не вознагражден прервать
        if (!isRewarded()) return;
        //Вознаграждаем пользователя монеткой
//        PurchaseManager.addCoins(COUNT_COINS, mContext);
////        ToolbarUtil.setCoinsSubtitle((AppCompatActivity) mActivity);
//        String message = mContext.getString(R.string.you_earned_coins, COUNT_COINS);
//        ToastUtil.show(mContext, message);
        //Возвращаем значение в прежнее состояние
        setRewarded(false);
    }

    /**
     * Предлагаем посмотреть видеорекламу
     */
    public void onShowRewardVideoDialog() {
        Log.d(TAG, "onShowRewardVideoDialog");
        mActivity.runOnUiThread(() -> {
            if (isLoaded() && !mActivity.isFinishing()) {
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.free_coins)
                        .setMessage(R.string.watch_and_gain)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> onShow())
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
            }
        });
    }

    private boolean isRewarded() {
        return isRewarded;
    }

    private void setRewarded(boolean rewarded) {
        isRewarded = rewarded;
    }

    public RewardedVideoAd getRewardedVideoAd() {
        return mRewardedVideoAd;
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
        if (mRewardedVideoAd != null) mRewardedVideoAd.destroy(mContext);
    }


    public RewardedVideoAdListener getDefaultVideoRewardAdListener() {
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdClosed() {
                Log.d(TAG, "onRewardedVideoAdClosed");
                forceLoadRewardedVideo();
                onReward();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.d(TAG, "onRewarded: " + "currency: " + rewardItem.getType() + " amount: " + rewardItem.getAmount());
                setRewarded(true);
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                Log.d(TAG, "onRewardedVideoAdFailedToLoad code " + errorCode);
            }

            @Override
            public void onRewardedVideoAdLoaded() {
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
            public void onRewardedVideoAdLeftApplication() {
                Log.d(TAG, "onRewardedVideoAdLeftApplication");
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.d(TAG, "onRewardedVideoCompleted");
            }
        };
    }
}