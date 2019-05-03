package com.remaistudio.master.ui;

import android.content.res.Configuration;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.remaistudio.master.R;
import com.remaistudio.master.util.FragmentUtil;
import com.remaistudio.master.util.SocialManager;

public class NavigationDrawerUtil implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentActivity mActivity;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private float mAnimateValues = 0.0f;

    public NavigationDrawerUtil(FragmentActivity activity) {
        mActivity = activity;
        initNavigationDrawer();
        initDrawerToggle();
    }

    private void initNavigationDrawer() {
        mDrawerLayout = mActivity.findViewById(R.id.drawer_layout);
        mNavigationView = mActivity.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        initDrawerToggle();
    }

    private void initDrawerToggle() {
        mDrawerToggle = DrawerToggleUtil.getActionBarDrawerToggle(mActivity, mDrawerLayout);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.setDrawerSlideAnimationEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle); //Назначить ActionBarDrawerToggle слушателем для DrawerLayout
    }

    /**
     * Обновляет состояние и доступность nawDrawer. Вызывать из фрагмента.
     */
    public void onUpdateDrawerToggle() {
        float[] floats = FragmentUtil.isNotLastFragment(mActivity) ? new float[]{0, 1} : new float[]{1, 0};
        if (floats[0] != mAnimateValues) return;
        mAnimateValues = floats[1];
        DrawerToggleUtil.onAnimateDrawerToggle(mDrawerToggle, mDrawerLayout, floats);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_drawer:
//                mActivity.startActivity(new Intent(mActivity, ActivityFavorite.class));
                break;
            case R.id.rate_drawer:
                SocialManager.onAppRate(mActivity);
                break;
            case R.id.share_drawer:
                SocialManager.onShareApp(mActivity);
                break;
            case R.id.feedback_drawer:
                SocialManager.onSendMail(mActivity, null, null, null);
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onOpenDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void onCloseDrawer() {
        mDrawerLayout.closeDrawer(mNavigationView);
    }

    public boolean isOpenDrawer() {
        return mDrawerLayout.isDrawerOpen(mNavigationView);
    }

    //Синхронизировать состояние ActionBarDrawerToggle с состоянием выдвижной панели
    public void onSyncStateDrawerToggle() {
        mDrawerToggle.syncState();
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }
}
