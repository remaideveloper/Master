package com.alegangames.master.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.ads.admob.AdMobRequest;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.fragment.FragmentAbstract;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.model.enums.JsonItemFragmentEnum;
import com.alegangames.master.ui.NavigationDrawerUtil;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.ContentHelper;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.MinecraftHelper;
import com.alegangames.master.util.ServerHelper;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.StrictModeUtil;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.UrlHelper;
import com.alegangames.master.util.permision.PermissionManager;
import com.alegangames.master.util.rules.GDPRHelper;
import com.google.android.gms.ads.MobileAds;

public class ActivityMain extends ActivityAppParent implements FragmentAbstract.InterfaceFragment, PermissionManager.InterfacePermission{

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
        AdMobRequest.setBundle(this);
        MobileAds.setRequestConfiguration(AdMobRequest.getRequestConfiguration(this));
        mAdMobBanner = new AdMobBanner(this);
        ToolbarUtil.setToolbarForDrawer(this, true);
        mNavigationViewUtil = new NavigationDrawerUtil(this);
        GDPRHelper.getRequestConsentInfo(this);

        //Инициализируем транзакцию первого фрагмента
        FragmentUtil.onTransactionFragmentByItem(this, R.id.main_container, JsonItemFragmentEnum.MAIN);
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
            case R.id.instagram:
                Uri address = Uri.parse("https://www.instagram.com/master_for_minecraft_pe/");
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openLinkIntent);
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

    @Override
    public void onPermissionSuccessResult(int requestCode) {
        
    }
}


