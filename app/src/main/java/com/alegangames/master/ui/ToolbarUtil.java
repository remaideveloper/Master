package com.alegangames.master.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alegangames.master.R;
import com.alegangames.master.util.billing.PurchaseManager;

public class ToolbarUtil {

    private static final int TOOLBAR = R.id.toolbar;

    public static void setToolbar(AppCompatActivity activity, boolean homeButtonEnable) {
        Toolbar toolbar = activity.findViewById(TOOLBAR);
        activity.setSupportActionBar(toolbar);
        setActionBar(activity, homeButtonEnable);
        if (homeButtonEnable) {
            toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        }
    }

    public static void setToolbarForDrawer(AppCompatActivity activity, boolean homeButtonEnable) {
        Toolbar toolbar = activity.findViewById(TOOLBAR);
        activity.setSupportActionBar(toolbar);
        setActionBar(activity, homeButtonEnable);
    }

    public static void setActionBar(AppCompatActivity activity, boolean homeButtonEnable) {
        ActionBar actionbar = activity.getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(homeButtonEnable);
            actionbar.setDisplayShowHomeEnabled(homeButtonEnable);
            actionbar.setHomeButtonEnabled(homeButtonEnable);
        }
    }

    public static void setCoinsSubtitle(AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            String subtitle = activity.getString(R.string.coins_amount_format, PurchaseManager.getCoins(activity.getApplicationContext()));
            activity.getSupportActionBar().setSubtitle(subtitle);
        }
    }

}
