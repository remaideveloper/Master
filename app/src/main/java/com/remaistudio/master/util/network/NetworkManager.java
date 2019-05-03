package com.remaistudio.master.util.network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.remaistudio.master.R;
import com.remaistudio.master.util.SnackbarToast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkManager implements LifecycleObserver {

    /**
     * Проверяем подключение к интернету.
     *
     * @param context
     * @return True если подключение есть, False если нету подключения.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Проверяет подключение к интернету,
     * и показывает сообщение в SnackBar если подключения нету.
     *
     * При нажатии кнопки, обновляет активити
     *
     * @param activity
     */
    public static void onNetworkCondition(@Nullable Activity activity) {
        if (activity != null && !isOnline(activity)) {
            new SnackbarToast(activity, R.string.no_network, R.string.retry, v -> activity.recreate());
        }
    }

    public static boolean isAvailableConnection(String url) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.connect();
            if (connection.getResponseCode() >= 400) return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
