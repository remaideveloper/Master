package com.remaistudio.master.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.remaistudio.master.R;
import com.remaistudio.master.ads.admob.AdMobBanner;
import com.remaistudio.master.fragment.FragmentAbstract;
import com.remaistudio.master.model.enums.JsonItemFragmentEnum;
import com.remaistudio.master.ui.NavigationDrawerUtil;
import com.remaistudio.master.ui.ToolbarUtil;
import com.remaistudio.master.util.AppUtil;
import com.remaistudio.master.util.FragmentUtil;
import com.remaistudio.master.util.StrictModeUtil;
import com.remaistudio.master.util.rules.GDPRHelper;

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
        mNavigationViewUtil = new NavigationDrawerUtil(this);
        ToolbarUtil.setToolbarForDrawer(this, true);
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
                    mNavigationViewUtil.onOpenDrawer();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavigationViewUtil.onSyncStateDrawerToggle();
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


