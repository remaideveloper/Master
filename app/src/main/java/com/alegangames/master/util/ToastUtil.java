package com.alegangames.master.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;


public final class ToastUtil {

    private ToastUtil() {
    }

    public static void show(@Nullable Context context, @Nullable int stringRes) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast toast = Toast.makeText(context, stringRes, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(@Nullable Context context, @Nullable String string) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
