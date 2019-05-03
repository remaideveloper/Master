package com.remaistudio.master.util.permision;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.remaistudio.master.interfaces.InterfaceCallback;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PermissionManager {

    /**
     * Интерфейс для разрешений
     */
    public interface InterfacePermission {
        //Метод вызывется когда пользователь одобрил разрешение
        void onPermissionSuccessResult(int requestCode);
    }

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void onAskStoragePermission(Activity activity, int requestCode, InterfaceCallback interfaceListener) {

        if (onSdkMoreThanLollipopMR1()) {
            if (shouldAskStoragePermission(activity, requestCode)) {
                interfaceListener.onCallback();
            }
        } else {
            interfaceListener.onCallback();
        }
    }

    public static void onAskStoragePermission(Activity activity, int requestCode) {

        if (onSdkMoreThanLollipopMR1()) {
            if (shouldAskStoragePermission(activity, requestCode)) {
                onPermissionInterface(requestCode, ((InterfacePermission) activity));
            }
        } else {
            onPermissionInterface(requestCode, ((InterfacePermission) activity));
        }
    }

    private static void onPermissionInterface(int requestCode, InterfacePermission interfacePermission) {
        if (interfacePermission != null)
            interfacePermission.onPermissionSuccessResult(requestCode);
    }

    private static boolean onSdkMoreThanLollipopMR1() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private static boolean shouldAskStoragePermission(Activity activity, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, requestCode);
            return false;
        } else {
            return true;
        }
    }


}

