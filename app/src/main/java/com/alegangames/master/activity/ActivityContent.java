package com.alegangames.master.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.fragment.FragmentAbstract;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.StrictModeUtil;
import com.alegangames.master.util.permision.PermissionManager;

public class ActivityContent extends ActivityAppParent implements FragmentAbstract.InterfaceFragment, PermissionManager.InterfacePermission {
    private static final String TAG = ActivityContent.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_main;

    public AdMobBanner mAdMobBanner;
    public String mFragmentTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(LAYOUT);
        StrictModeUtil.init();

        mAdMobBanner = new AdMobBanner(this);
        mAdMobInterstitial = new AdMobInterstitial(this, Config.INTERSTITIAL_ID);
        ToolbarUtil.setToolbarForDrawer(this, true);

        mFragmentTitle = getIntent().getStringExtra(FRAGMENT_DATA);

        //Инициализируем транзакцию первого фрагмента
        FragmentUtil.onTransactionFragmentByName(this, mFragmentTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //mNavigationViewUtil.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mNavigationViewUtil.onSyncStateDrawerToggle();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");


        //Если в стеке фрагментов один фрагмент
        if (FragmentUtil.isLastFragment(this)) {
            finish();
        }
    }

    @Override
    public void onShowBannerOfFragment(boolean b) {
        if (b) mAdMobBanner.onCreate();
        else mAdMobBanner.onDestroy();
    }



    @Override
    public void onShowInterstitialOfFragment(boolean b) {
        if (b) mAdMobInterstitial.onShowAd();
    }

    @Override
    public void onPermissionSuccessResult(int requestCode) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(mFragmentTitle);
    }
}
