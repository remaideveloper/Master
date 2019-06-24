package com.alegangames.master.util.network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.R;
import com.alegangames.master.util.SnackbarToast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkManager implements LifecycleObserver {

    private FragmentActivity mActivity;

    private NetworkManager(FragmentActivity activity) {
        this.mActivity = activity;
        mActivity.getLifecycle().addObserver(this);
    }

    public static void getInstance(FragmentActivity activity) {
        new NetworkManager(activity);
    }

    /**
     * Проверяем подключение к интернету.
     *
     * @param context
     * @return True если подключение есть, False если нету подключения.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Проверяет подключение к интернету,
     * и показывает сообщение в SnackBar если подключения нету.
     * <p>
     * При нажатии кнопки, обновляет активити
     *
     * @param activity
     */
    public static void onNetworkCondition(@Nullable Activity activity) {
        if (activity != null && !activity.isFinishing() && !isOnline(activity)) {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        onNetworkCondition(mActivity);
    }

}
