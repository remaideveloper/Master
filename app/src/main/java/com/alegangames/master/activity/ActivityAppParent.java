package com.alegangames.master.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.model.JsonItem;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.json.SerializableJSONObject;
import com.alegangames.master.util.network.NetworkManager;
import com.alegangames.master.util.permision.PermissionManager;

import static com.alegangames.master.activity.ActivityItem.JSON_OBJECT_KEY;

public abstract class ActivityAppParent extends AppCompatActivity {

    public static final String TAG = ActivityAppParent.class.getSimpleName();

    public static final String FRAGMENT_DATA = "FRAGMENT_DATA";
    public static final String FRAGMENT_TITLE = "FRAGMENT_TITLE";
    public static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";
    public static final String FRAGMENT_BANNER = "FRAGMENT_BANNER";
    public static final String FRAGMENT_INTERSTITIAL = "FRAGMENT_INTERSTITIAL";
    public static final String FRAGMENT_SHUFFLE = "FRAGMENT_SHUFFLE";
    public static final String FRAGMENT_COLUMN = "FRAGMENT_COLUMN";
    public static final String VERSION = "VERSION";

    public PermissionManager.InterfacePermission mInterfacePermission;
    public AdMobInterstitial mAdMobInterstitial;
    public AdMobVideoRewarded mAdMobVideoRewarded;

    public int countShowAd = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        NetworkManager.getInstance(this);

        mAdMobInterstitial = new AdMobInterstitial(this, Config.INTERSTITIAL_ID);
        mAdMobVideoRewarded = new AdMobVideoRewarded(this);
        mAdMobVideoRewarded.forceLoadRewardedVideo();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        NetworkManager.onNetworkCondition(this);
        super.onResume();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        //Для избежания TransactionTooLargeException
//            super.onSaveInstanceState(outState);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, R.string.error);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mInterfacePermission != null)
                    mInterfacePermission.onPermissionSuccessResult(requestCode);
            } else {
                //Если пользователь не одобрил разрешение
                ToastUtil.show(this, R.string.permission_denied);
            }
        }
    }

    public void onOpenItem(JsonItem item) {
        Intent intent = new Intent(this, JsonItemFactory.getClass(item.getId()));
        SerializableJSONObject serializableJSONObject = new SerializableJSONObject(item.getJsonObject());
        intent.putExtra(JSON_OBJECT_KEY, serializableJSONObject);
        startActivity(intent);
        if (countShowAd !=0 && (countShowAd == 1 || countShowAd % 4 == 0)) {
            mAdMobInterstitial.onShowAd();
        }
        ++countShowAd;
    }

}
