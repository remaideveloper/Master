package com.remaistudio.master.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.remaistudio.master.R;

public class DrawerToggleUtil {

    public static ActionBarDrawerToggle getActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout) {
        return new ActionBarDrawerToggle(activity, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                //Создать заного элементы меню
                activity.invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Создать заного элементы меню
                activity.invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
    }

    /**
     * Анимация ActionBarDrawerToggle
     *
     * @param values Значения анимации.
     *               0,1 анимация от Menu к Arrow
     *               1,0 обратная анимация от Arrow к Menu
     *               0 Menu
     *               1 Arrow
     */
    public static void onAnimateDrawerToggle(ActionBarDrawerToggle drawerToggle, DrawerLayout drawerLayout, float... values) {
        ValueAnimator anim = ValueAnimator.ofFloat(values);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            if (drawerToggle != null) {
                drawerToggle.onDrawerSlide(drawerLayout, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.start();

    }
}