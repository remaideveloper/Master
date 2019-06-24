package com.alegangames.master.ui;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.alegangames.master.activity.ActivityShop;
import com.google.android.material.navigation.NavigationView;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityFavorite;
import com.alegangames.master.fragment.FragmentSettings;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.SocialManager;

import carbon.widget.BackdropLayout;

public class NavigationDrawerUtil implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentActivity mActivity;
    private BackdropLayout mBackdropLayout;
    private boolean opened;

    public NavigationDrawerUtil(FragmentActivity activity) {
        mActivity = activity;
        mBackdropLayout = mActivity.findViewById(R.id.backdrop);
        ((AppCompatActivity)mActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        opened = false;
        ((NavigationView) mActivity.findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
    }

    private void initDrawerToggle() {
//        mDrawerToggle = DrawerToggleUtil.getActionBarDrawerToggle(mActivity, mDrawerLayout);
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        mDrawerToggle.setDrawerSlideAnimationEnabled(true);
//        mDrawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(mDrawerToggle); //Назначить ActionBarDrawerToggle слушателем для DrawerLayout
    }

    /**
     * Обновляет состояние и доступность nawDrawer. Вызывать из фрагмента.
     */
    public void onUpdateDrawerToggle() {
//        float[] floats = FragmentUtil.isNotLastFragment(mActivity) ? new float[]{0, 1} : new float[]{1, 0};
//        if (floats[0] != mAnimateValues) return;
//        mAnimateValues = floats[1];
//        DrawerToggleUtil.onAnimateDrawerToggle(mDrawerToggle, mDrawerLayout, floats);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.rate_drawer:
                SocialManager.onAppRate(mActivity);
                break;
            case R.id.share_drawer:
                SocialManager.onShareApp(mActivity);
                break;
            case R.id.feedback_drawer:
                SocialManager.onSendMail(mActivity, null, null, null);
                break;
            case R.id.favorite_drawer:
                mActivity.startActivity(new Intent(mActivity, ActivityFavorite.class));
                return true;
            case R.id.shop:
                mActivity.startActivity(new Intent(mActivity, ActivityShop.class));
                return true;
            case R.id.settings_drawer:
                FragmentUtil.onTransactionFragment(mActivity, R.id.main_container, new FragmentSettings());
                openFragment();
                return true;
        }
        onCloseDrawer();
        return true;
    }

    public void onOpenDrawer() {
        mBackdropLayout.openLayout(BackdropLayout.Side.TOP);
        opened = true;
        ((AppCompatActivity)mActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    }

    public void onToggleDrawer(){
        if (opened)
            onCloseDrawer();
        else
            onOpenDrawer();
    }

    public void onCloseDrawer() {
       mBackdropLayout.closeLayout();
       opened = false;
        ((AppCompatActivity)mActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    public boolean isOpenDrawer() {
        return opened;
    }

    public void openFragment(){
        onCloseDrawer();
        ((AppCompatActivity)mActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
    }

}
