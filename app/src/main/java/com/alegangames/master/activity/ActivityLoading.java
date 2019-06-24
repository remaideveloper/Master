package com.alegangames.master.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobRequest;
import com.alegangames.master.ui.ContentRatingDialog;
import com.alegangames.master.util.StrictModeUtil;
import com.alegangames.master.util.network.NetworkManager;
import com.alegangames.master.util.preference.UtilPreference;

public class ActivityLoading extends AppCompatActivity {

    private static final String TAG = ActivityLoading.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_loading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        StrictModeUtil.init();
        AdMobRequest.setBundle(this);
        NetworkManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!UtilPreference.getUnderAgePref(this)) {
            try {
                ContentRatingDialog.getInstance().onShow(ActivityLoading.this);
            } catch (WindowManager.BadTokenException e){
                e.printStackTrace();
            }

        } else {

            //Задержка для появления экрана загрузки
            try {
                Thread.sleep(1000);
                startActivity(new Intent(ActivityLoading.this, ActivityMain.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
