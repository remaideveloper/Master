package com.alegangames.master.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.activity.ActivityItem;
import com.alegangames.master.activity.ActivityShop;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.apps.skins.ActivitySkinsCustom;
import com.alegangames.master.holder.PurchaseHolder;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.billing.BillingManager;
import com.alegangames.master.util.billing.Product;
import com.alegangames.master.util.billing.PurchaseManager;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import static com.alegangames.master.Config.API_KEY;

public class FragmentBuy extends BottomSheetDialogFragment implements PurchaseHolder.PurchaseListener, PurchaseManager.InterfacePurchase {

    private static final int LAYOUT = R.layout.activity_shop;

    private View mRootView;

    //Награда за просмотр рекламы
    public static final int ADS_REWARD = 10;

    private LinearLayout productsLayout;
    private AdMobVideoRewarded mAdMobVideoRewarded;
    private PurchaseHolder adsHolder;
    public BillingManager billingManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(LAYOUT, container, false);

        if (getActivity()!= null && getActivity() instanceof ActivityItem) {
            billingManager = ((ActivityItem) getActivity()).mBillingManager;
            mAdMobVideoRewarded = ((ActivityItem)getActivity()).mAdMobVideoRewarded;
        } else {
            billingManager = ((ActivitySkinsCustom) getActivity()).mBillingManager;
            mAdMobVideoRewarded = ((ActivitySkinsCustom)getActivity()).mAdMobVideoRewarded;
        }

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
            billingManager.getProductDetailsAsynk(product.getId()).observe(getViewLifecycleOwner(), skuDetails -> {
                if (skuDetails != null) {
                    String text = skuDetails.getPrice() + " " + skuDetails.getPriceCurrencyCode();
                    holder.setButtonText(text);
//                    holder.getRoot().setVisibility(View.VISIBLE);
                }
            });
        }

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
    public void onPurchase(Product product) {
        billingManager.onPurchaseProduct(product.getId());
    }

    @Override
    public void onProductPurchased(String productId) {
        ToolbarUtil.setCoinsSubtitle(((AppCompatActivity) getActivity()));
    }

    @Override
    public void onBillingError(int errorCode) {

    }
}
