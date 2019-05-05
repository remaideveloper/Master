package com.remaistudio.master.ui;

import android.content.res.Configuration;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.remaistudio.master.R;
import com.remaistudio.master.util.FragmentUtil;
import com.remaistudio.master.util.SocialManager;

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

}
