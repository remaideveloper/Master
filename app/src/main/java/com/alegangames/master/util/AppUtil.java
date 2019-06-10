package com.alegangames.master.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.alegangames.master.BuildConfig;
import com.alegangames.master.R;

import java.io.File;
import java.util.Locale;

public class AppUtil {

    private static final String TAG = AppUtil.class.getSimpleName();

    /**
     * Открыть приложение в Google Play
     *
     * @param context     Context
     * @param packageName Название пакета приложения
     */
    public static void onOpenGooglePlay(Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    /**
     * Открыть приложение по названию пакета
     *
     * @param context
     * @param packageName Название пакета приложения
     */
    public static void onOpenApp(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent == null) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            onOpenGooglePlay(context, packageName);
        }
    }

    /**
     * Открыть приложение по названию пакета с файлом
     *
     * @param context
     */
    public static void onOpenFileWithApp(Context context, File file, String packageName) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            data = Uri.fromFile(file);
        }
        newIntent.setPackage(packageName);
        newIntent.setDataAndType(data, "*/*");
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(newIntent);
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
            ToastUtil.show(context, R.string.error);
        }
    }

    /**
     * Получить название версии данного приложения
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo != null ? pInfo.versionName : "Error";
    }

    /**
     * Получить текущую локаль пользователя в Android
     *
     * @param context
     * @return Объект Locale представляет определенный географический, политический или культурный регион.
     */
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    private static long exitTime = 0;
    private static final int EXIT_TIME = 2000;

    /**
     * При первом нажатии на кнопку Back, пользователю показывает предупреждение
     * Если течении EXIT_TIME милисекунд пользователь повторно нажал Back, приложение должно закрытся
     *
     * @param activity
     */
    public static void onExit(@Nullable Activity activity) {
        //
        if ((System.currentTimeMillis() - exitTime) > EXIT_TIME) {
            ToastUtil.show(activity, R.string.back_pressed);
            exitTime = System.currentTimeMillis();
        } else {
            if (activity != null) {
                activity.finish();
            }
        }
    }

}
