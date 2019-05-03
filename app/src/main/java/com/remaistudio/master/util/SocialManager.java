package com.remaistudio.master.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.remaistudio.master.BuildConfig;
import com.remaistudio.master.R;
import com.remaistudio.master.util.preference.SharedPreferenceManager;

import java.io.File;

import static com.remaistudio.master.util.AppUtil.onOpenGooglePlay;

public class SocialManager {

    private static final String TAG = SocialManager.class.getSimpleName();

    /**
     * Поделиться файлом
     *
     * @param context контекст
     * @param file    файл с изображением
     * @param title   описание итема
     */
    public static void onShareImage(Context context, File file, String title) {
        if (context != null && file != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, title);
            intent.setType("*/*");
            Uri fileUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fileUri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                fileUri = Uri.parse("file://" + file.getAbsolutePath());
            }

            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
        }
    }

    /**
     * Поделиться приложением
     *
     * @param context
     */
    public static void onShareApp(Context context) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_on_google_play) + " " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
            sendIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_choise)));
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }
    }

    /**
     * Оценить приложение на Google Play
     *
     * @param context
     */
    public static void onAppRate(Context context) {
        onOpenGooglePlay(context, context.getPackageName());
        onAppRatePref(context);
    }

    /**
     * Сохранить предпочтение
     * Пользователь уже поставил оценку или  не захотел ставить оценку
     *
     * @param context
     */
    public static void onAppRatePref(Context context) {
        SharedPreferenceManager.getInstance(context).putBoolean(context.getString(R.string.appreciated_pref), true);
    }

    public static void onSendMail(Context context, String subject, String body, String filelocation) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.support_gmail)});
        if (subject != null) {
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (body != null) {
            i.putExtra(Intent.EXTRA_TEXT, body);
        }
        if (filelocation != null) {
            File file = new File(filelocation);
            Uri path = Uri.fromFile(file);
            i.putExtra(Intent.EXTRA_STREAM, path);
        }
        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.send)));
        } catch (android.content.ActivityNotFoundException e) {
//            Crashlytics.logException(e);
            ToastUtil.show(context, R.string.not_email_client);
        }
    }

    public static void onShowRateDialog(Activity activity) {

        Bundle params = new Bundle();
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.like_app, activity.getString(R.string.app_name)))
                .setMessage(R.string.rate_us_on_google)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> SocialManager.onAppRate(activity))
                .setNegativeButton(R.string.never, (dialog, which) -> SocialManager.onAppRatePref(activity))
                .setNeutralButton(R.string.later, null)
                .create()
                .show();
    }

    public static void onShowRateDialogOrToastFileSave(Activity activity) {

        if (!SharedPreferenceManager.getInstance(activity).getBoolean(activity.getString(R.string.appreciated_pref), false)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.file_saved)
                    .setMessage(R.string.rate_us_on_google)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> SocialManager.onAppRate(activity))
                    .setNegativeButton(R.string.never, (dialog, which) -> SocialManager.onAppRatePref(activity))
                    .setNeutralButton(R.string.later, null)
                    .create()
                    .show();
        } else {
            ToastUtil.show(activity, R.string.file_saved);
        }

    }

}
