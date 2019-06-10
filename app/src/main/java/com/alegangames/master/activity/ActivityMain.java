package com.alegangames.master.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.fragment.FragmentAbstract;
import com.alegangames.master.model.enums.JsonItemFragmentEnum;
import com.alegangames.master.ui.NavigationDrawerUtil;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.StrictModeUtil;
import com.alegangames.master.util.rules.GDPRHelper;

public class ActivityMain extends ActivityAppParent implements FragmentAbstract.InterfaceFragment {

    private static final String TAG = ActivityMain.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_main;

    public NavigationDrawerUtil mNavigationViewUtil;
    public AdMobBanner mAdMobBanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(LAYOUT);
        StrictModeUtil.init();
        mAdMobBanner = new AdMobBanner(this);
        ToolbarUtil.setToolbarForDrawer(this, true);
        mNavigationViewUtil = new NavigationDrawerUtil(this);
        GDPRHelper.getRequestConsentInfo(this);

        //Инициализируем транзакцию первого фрагмента
        FragmentUtil.onTransactionFragmentByItem(this, R.id.main_container, JsonItemFragmentEnum.MAIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToolbarUtil.setCoinsSubtitle(this);
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
                if (FragmentUtil.isNotLastFragment(this)) {
                    onBackPressed();
                } else {
                    mNavigationViewUtil.onToggleDrawer();
                }
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

        //Если открыт DrawerLayout, закрыть его
        if (mNavigationViewUtil.isOpenDrawer()) {
            mNavigationViewUtil.onCloseDrawer();
            return;
        }

        //Если в стеке фрагментов один фрагмент
        if (FragmentUtil.isLastFragment(this)) {
            AppUtil.onExit(this);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 2){
            mNavigationViewUtil.onCloseDrawer();
        }


        //Если в стеке фрагментов более одного фрагмента, убираем последний из стека
        FragmentUtil.onBackFragment(this, R.id.main_container);
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
}


