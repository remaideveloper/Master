package com.alegangames.master.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alegangames.master.R;
import com.alegangames.master.fragment.FragmentShop;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.billing.BillingManager;
import com.alegangames.master.util.billing.PurchaseManager;
import com.alegangames.master.util.network.NetworkManager;

import static com.alegangames.master.Config.API_KEY;
import static com.alegangames.master.Config.PRODUCT_ID;

public class ActivityShop extends ActivityAppParent implements PurchaseManager.InterfacePurchase {
    private static final int LAYOUT = R.layout.activity_frame;
    private static final String TAG = ActivityShop.class.getSimpleName();
    public BillingManager billingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkManager.onNetworkCondition(this);
        setContentView(LAYOUT);
        billingManager = new BillingManager(this, PRODUCT_ID);
        billingManager.registerInterfacePurchase(this);
        billingManager.initBilling(API_KEY);
        ToolbarUtil.setToolbar(this, true);
        FragmentUtil.onTransactionFragment(this, R.id.main_container, FragmentShop.getInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingManager.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onProductPurchased(String productId) {

    }

    @Override
    public void onBillingError(int errorCode) {

    }
}
