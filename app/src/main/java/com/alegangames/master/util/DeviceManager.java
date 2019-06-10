package com.alegangames.master.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();

    /**
     * Проверить, существует ли приложение с названием пакета packageName на данном устройстве
     *
     * @param context
     * @param packageName Название пакета приложения
     * @return True если существует, False если не существует
     */
    public static boolean checkAppInDevice(Context context, String packageName) {
        boolean b = false;
        try {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                b = true;
            } catch (PackageManager.NameNotFoundException e) {
                b = false;
            }
            Log.d(TAG, "checkAppInDevice: " + packageName + " " + b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * Узнать запущен ли определенный процесс
     *
     * @param context Context
     * @param name    Название процесса
     * @return True/False Запущен/Незапущен процесс
     */
    public static boolean isNamedProcessRunning(Context context, String name) {
        Log.d(TAG, "isNamedProcessRunning");
        if (name != null) {
            Log.d(TAG, "isNamedProcessRunning: name " + name);

            try {
                ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                if (manager == null) {
                    return false;
                }
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
                if (runningProcesses == null || runningProcesses.isEmpty()) {
                    return false;
                }
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningProcesses) {
                    Log.d(TAG, "isNamedProcessRunning: runningAppProcessInfo.processName " + runningAppProcessInfo.processName);
                    if (name.equals(runningAppProcessInfo.processName)) {
                        return true;
                    }
                }
            } catch (Exception e) {
//                Crashlytics.logException(e);
                e.printStackTrace();
            }

        }
        return false;
    }


}
