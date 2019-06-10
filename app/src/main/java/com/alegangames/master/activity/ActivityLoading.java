package com.alegangames.master.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.R;
import com.alegangames.master.util.StrictModeUtil;

public class ActivityLoading extends AppCompatActivity {
    private static final String TAG = "ActivityLoading";

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_loading);
        StrictModeUtil.init();
    }

    protected void onResume() {
        super.onResume();
//        ContentRatingDialog.getInstance().onShow(this);
    }
}
