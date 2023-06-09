package com.alegangames.master.ads.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alegangames.master.BuildConfig;
import com.alegangames.master.R;
import com.alegangames.master.util.ButtonColorManager;
import com.alegangames.master.util.ColorList;
import com.annimon.stream.Stream;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd.Image;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdMobNativeAdvanceUnified {
    public static final String TAG = AdMobNativeAdvanceUnified.class.getSimpleName();
    public static final String TEST_NATIVE_ADVANCE_ID = "ca-app-pub-3940256099942544/2247696110";

    private String mAdUnitId;
    private UnifiedNativeAdView mNativeAdView;
    private UnifiedNativeAd mNativeAd;
    private boolean loading = false;
    private int mSize;
    private List<UnifiedNativeAd> mListAds;

    public AdMobNativeAdvanceUnified(Context context, String adUnitId, int size) {
        this.mAdUnitId = adUnitId;
        mSize = size;
        mListAds = new ArrayList<>();
        loadNativeAds(context);
    }

    public AdMobNativeAdvanceUnified(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    public void addNativeAdvanceView(ViewGroup viewGroup){
        AdLoader.Builder builder = new AdLoader.Builder(viewGroup.getContext(), (BuildConfig.DEBUG) ? TEST_NATIVE_ADVANCE_ID : mAdUnitId);

        // OnUnifiedNativeAdLoadedListener implementation.
        final AdLoader adLoader = builder.forUnifiedNativeAd(unifiedNativeAd -> {
            mNativeAd = unifiedNativeAd;
            mNativeAdView = (UnifiedNativeAdView) ((Activity) viewGroup.getContext()).getLayoutInflater()
                        .inflate(R.layout.layout_native_unified_app, null);
            viewGroup.removeAllViews();
            viewGroup.addView(mNativeAdView);
                populateUnifiedNativeAdView(unifiedNativeAd, mNativeAdView);
                viewGroup.setVisibility(View.VISIBLE);
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "onAdFailedToLoad: " + errorCode);
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

            }
        }).build();

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        adLoader.loadAd(AdMobRequest.getRequest());
    }

    public void loadNativeAds(Context context) {

        loading = true;

        AdLoader.Builder builder = new AdLoader.Builder(context, (BuildConfig.DEBUG) ? TEST_NATIVE_ADVANCE_ID : mAdUnitId);

        // OnUnifiedNativeAdLoadedListener implementation.
        final AdLoader adLoader = builder.forUnifiedNativeAd(unifiedNativeAd -> {
//            mNativeAd = unifiedNativeAd;
//            mNativeAdView = (UnifiedNativeAdView) ((Activity) viewGroup.getContext()).getLayoutInflater()
//                        .inflate(R.layout.layout_native_unified_app, null);
//            viewGroup.removeAllViews();
//            viewGroup.addView(mNativeAdView);
//                populateUnifiedNativeAdView(unifiedNativeAd, mNativeAdView);
            mListAds.add(unifiedNativeAd);
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "onAdFailedToLoad: " + errorCode);
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

            }
        }).build();

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        adLoader.loadAds(AdMobRequest.getRequest(), mSize);
    }

    public void onDestroy(){
        Stream.of(mListAds).forEach(UnifiedNativeAd::destroy);
        mListAds.clear();
    }

    public void updateAdvanceView(final View viewGroup, int position){
        UnifiedNativeAdView mNativeAdView = viewGroup.findViewById(R.id.nativeAdView);
        if (mListAds.isEmpty()) {
            mNativeAdView.setVisibility(View.GONE);
            return;
        }
        int pos = position;
        if (mListAds.size()<=position){
            pos = mListAds.size()-1;
        }
            mNativeAdView.setVisibility(View.VISIBLE);

                UnifiedNativeAd mNativeAd = mListAds.get(pos);
            populateUnifiedNativeAdView(mNativeAd, mNativeAdView);
//            viewGroup.removeAllViews();
//            ViewGroup parent = (ViewGroup) mNativeAdView.getParent();
//            if (parent!=null)
//                parent.removeAllViews();
//            viewGroup.addView(mNativeAdView);
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                Log.d(TAG, "onVideoEnd: Video status: Video playback has ended.");
                super.onVideoEnd();
            }
        });

        ((MaterialButton) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//        ButtonColorManager.setBackgroundButton(((Button) adView.getCallToActionView()), ColorList.BLUE);
        ButtonColorManager.setTextColorButton(((Button) adView.getCallToActionView()), ColorList.WHITE);

        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
            Log.d(TAG, "inflateAppInstallAdView: " + String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<Image> images = nativeAd.getImages();

            if (images != null && images.size() > 0) {
                mainImageView.setImageDrawable(images.get(0).getDrawable());
            }

            Log.d(TAG, "inflateAppInstallAdView: Video status: Ad does not contain a video asset.");
        }

        adView.getPriceView().setVisibility(View.GONE);
        adView.getStoreView().setVisibility(View.GONE);
        adView.getStarRatingView().setVisibility(View.GONE);
    }
}
