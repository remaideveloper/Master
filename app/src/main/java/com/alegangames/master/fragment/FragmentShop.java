package com.alegangames.master.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.holder.PurchaseHolder;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.billing.Product;
import com.alegangames.master.util.billing.PurchaseManager;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.List;

public class FragmentShop extends FragmentAbstract implements PurchaseHolder.PurchaseListener, PurchaseManager.InterfacePurchase {

    public static final String TAG = FragmentShop.class.getSimpleName();

    private static final int LAYOUT = R.layout.activity_shop;

    private View mRootView;

    //Награда за просмотр рекламы
    public static final int ADS_REWARD = 10;

    private LinearLayout productsLayout;
    private AdMobVideoRewarded mAdMobVideoRewarded;
    private PurchaseHolder adsHolder;
//    public BillingManager billingManager;

    public static FragmentShop getInstance() {
        return new FragmentShop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(LAYOUT, container, false);

//        billingManager = ((ActivityShop)getActivity()).billingManager;

        productsLayout = mRootView.findViewById(R.id.linearLayoutPurchases);

        //Продукт - вознаграждаемая реклама
        Product adsProduct = new Product("ads", getString(R.string.free_coins), getString(R.string.watch_and_gain)
                , R.drawable.button_video, ADS_REWARD, 0);
        adsHolder = new PurchaseHolder(getContext(), productsLayout, adsProduct, (s) -> mAdMobVideoRewarded.onShow());
        adsHolder.setButtonText(getString(R.string.watch));
        adsHolder.setButtonEnabled(false);
//        adsHolder.getRoot().setVisibility(View.GONE);
        productsLayout.addView(adsHolder.getRoot());

        //Загружаем продукты
        List<Product> products = PurchaseManager.getProducts(getContext());
        for (Product product : products) {
            PurchaseHolder holder = new PurchaseHolder(getContext(), productsLayout, product, this);
//            holder.getRoot().setVisibility(View.GONE);
            productsLayout.addView(holder.getRoot());
            //Получаем информацию о продукте
//            billingManager.getProductDetailsAsynk(product.getId()).observe(this, skuDetails -> {
//                if (skuDetails != null) {
//                    String text = skuDetails.priceText + " " + skuDetails.currency;
//                    holder.setButtonText(text);
////                    holder.getRoot().setVisibility(View.VISIBLE);
//                }
//            });
        }

        mAdMobVideoRewarded = new AdMobVideoRewarded(getActivity());
        mAdMobVideoRewarded.getRewardedVideoAd().setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoAdLoaded();
                //Делаем кнопку активной
                adsHolder.setButtonEnabled(true);
//                adsHolder.getRoot().setVisibility(View.VISIBLE);
            }

            @Override
            public void onRewardedVideoAdClosed() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoAdClosed();
                //Делаем кнопку неактивной
                adsHolder.setButtonEnabled(false);
//                adsHolder.getRoot().setVisibility(View.GONE);
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewarded(rewardItem);
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoAdFailedToLoad(i);
            }

            @Override
            public void onRewardedVideoAdOpened() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoAdOpened();
            }

            @Override
            public void onRewardedVideoStarted() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoStarted();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoAdLeftApplication();
            }

            @Override
            public void onRewardedVideoCompleted() {
                mAdMobVideoRewarded.getDefaultVideoRewardAdListener().onRewardedVideoCompleted();

            }
        });
        //Загружаем видео для доступа без задержек
        mAdMobVideoRewarded.forceLoadRewardedVideo();

        return mRootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if(!hidden)
//            setActionBarTitle(getString(R.string.shop));
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRootView.findViewById(R.id.linearLayoutPurchases).setPadding(getResources().getDimensionPixelSize(R.dimen.parent_layout_padding), 0, getResources().getDimensionPixelSize(R.dimen.parent_layout_padding), 0);
    }

    @Override
    public void onPurchase(Product product) {
        Log.d(TAG, "onPurchase: " + product.getId());
//        billingManager.onPurchaseProduct(product.getId());
    }

    /**
     * Вызывается из BillingManager при успешной покупке продукта
     *
     * @param productId id купленного продукта.
     */
    @Override
    public void onProductPurchased(String productId) {
        ToolbarUtil.setCoinsSubtitle(((AppCompatActivity) getActivity()));
    }

    @Override
    public void onBillingError(int errorCode) {
        Log.d(TAG, "onBillingError " + errorCode);
        mAdMobVideoRewarded.onShowRewardVideoDialog();
    }

//    /**
//     * BillingManager получает результат от плей маркета
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (!billingManager.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
