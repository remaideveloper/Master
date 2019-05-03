package com.remaistudio.master.activity;

import com.remaistudio.master.fragment.FragmentAbstract;

public class ActivityContent extends ActivityAppParent implements FragmentAbstract.InterfaceFragment {
    @Override
    public void onShowBannerOfFragment(boolean b) {

    }

    @Override
    public void onShowInterstitialOfFragment(boolean b) {

    }
//    private static final String TAG = ActivityMain.class.getSimpleName();
//    private AdMobBanner mAdMobBanner;
//    private ProgressBar mProgressBar;
//
//    public void onCreate(Bundle bundle) {
//        super.onCreate(bundle);
//        Log.d(TAG, "onCreate");
//        setContentView((int) R.layout.activity_category);
//        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        this.mAdMobBanner = new AdMobBanner((FragmentActivity) this);
//        this.mAdMobBanner.onCreate();
//        this.mAdMobInterstitial = new AdMobInterstitial(this, "ca-app-pub-2531835920111883/7237095417");
//        ToolbarUtil.setToolbar(this, true);
//        FragmentViewModel.get(this).getFragmentMapLiveData().observe(this, this::onChanged);
//    }
//
//    protected void onResume() {
//        super.onResume();
//        ToolbarUtil.setCoinsSubtitle(this);
//    }
//
//    public void onChanged(Map<String, JsonItemFragment> map) {
//        FragmentUtil.onTransactionFragmentByItem(this, map.get(getIntent().getStringExtra(FRAGMENT_DATA)), null, false);
//        this.mProgressBar.setVisibility(View.GONE);
//    }
//
//    public void onShowBannerOfFragment(boolean z) {
//        if (z) {
//            this.mAdMobBanner.onCreate();
//        } else {
//            this.mAdMobBanner.onDestroy();
//        }
//    }
//
//    public void onShowInterstitialOfFragment(boolean z) {
//        if (z) {
//            this.mAdMobInterstitial.onShowAd();
//        }
//    }
}
