package com.remaistudio.master.util;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.remaistudio.master.Config;
import com.remaistudio.master.R;

public class MinecraftHelper {

    public static final String MINECRAFT_PACKAGE_NAME = "com.mojang.minecraftpe";
    public static final String BLOCKLAUNCHER_PACKAGE_NAME = "net.zhuoweizhang.mcpelauncher";
    public static final String BLOCKLAUNCHER_PRO_PACKAGE_NAME = "net.zhuoweizhang.mcpelauncher.pro";

    public static final String IMPORT = "import";
    public static final String LOAD = "load";
    public static final String IMPORTLOAD = "importload";
    public static final String IMPORTADDON = "importaddon";

    public static final Double NOT_SUPPORT_SERVER_FILE = 1.0;

    public static void openMinecraft(Context context) {
        if (MinecraftHelper.isMinecraftInstalled(context)) {
            AppUtil.onOpenApp(context, MINECRAFT_PACKAGE_NAME);
        }

    }

    /**
     * Открыть BlockLauncher, если он установлен
     * Иначе показать сообщение о том что его нет на устройстве
     *
     * @param context Context
     */
    public static void openBlockLauncher(Context context) {
        if (isBlockLauncherInstalled(context)) {
            if (DeviceManager.checkAppInDevice(context, BLOCKLAUNCHER_PRO_PACKAGE_NAME)) {
                AppUtil.onOpenApp(context, BLOCKLAUNCHER_PRO_PACKAGE_NAME);
            } else if (DeviceManager.checkAppInDevice(context, BLOCKLAUNCHER_PACKAGE_NAME)) {
                AppUtil.onOpenApp(context, BLOCKLAUNCHER_PACKAGE_NAME);
            }
        }
    }

    /**
     * Поддерживает ли данная версия Minecraft PE автоматическую установку скина
     *
     * @return True/False Поддерживает/Неподдерживает
     */
    public static boolean isSupportSkinsVersion(Context context) {

        if (MinecraftHelper.getMinecraftVersionName(context).isEmpty())
            return false;

        String mNotSupportVersions[] = {
                "1.1.5", "1.1.4",
                "1.1.3", "1.1.2", "1.1.1",
                "1.1.0", "1.0.9", "1.0.8",
                "1.0.7", "1.0.6", "1.0.5"};
        for (String nVersion : mNotSupportVersions) {
            if (MinecraftHelper.getMinecraftVersionName(context).contains(nVersion)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Поддерживает ли данная версия Minecraft PE автоматическую установку серверов
     *
     * @return True/False Поддерживает/Неподдерживает
     */
    public static boolean isSupportServersVersion(Context context) {
        return MinecraftHelper.getMinecraftVersionNameToDouble(context) > MinecraftHelper.NOT_SUPPORT_SERVER_FILE;
    }

    /**
     * @param context Context
     * @return Возвращает True если MCPE установлен
     */
    public static boolean isMinecraftInstalled(Context context) {
        return DeviceManager.checkAppInDevice(context, MINECRAFT_PACKAGE_NAME) || Config.MINECRAFT_INSTALLED;

    }

    /**
     * @param c Context
     * @return True/False Запущен/Незапущен процесс приложения Minecraft PE
     */
    public static boolean isMinecraftRunning(Context c) {
        return DeviceManager.isNamedProcessRunning(c, MinecraftHelper.MINECRAFT_PACKAGE_NAME);
    }

    /**
     * @param context Context
     * @return Возвращает True если BlockLauncher установлен
     */
    public static boolean isBlockLauncherInstalled(Context context) {
        if (DeviceManager.checkAppInDevice(context, BLOCKLAUNCHER_PACKAGE_NAME)
                || DeviceManager.checkAppInDevice(context, BLOCKLAUNCHER_PRO_PACKAGE_NAME)) {
            return true;
        } else {
            ToastUtil.show(context, R.string.blocklauncher_not_installed);
            return false;
        }
    }

    /**
     * @param context
     * @return Название версии приложения Minecraft Pocket Edition на данном устройстве
     */
    public static String getMinecraftVersionName(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(MINECRAFT_PACKAGE_NAME, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pInfo != null ? pInfo.versionName : "";
    }

    /**
     * @param context
     * @return Название версии приложения Minecraft Pocket Edition на данном устройстве
     * ввиде значения Double, если приложение не установлено вернуть значение 0.0
     */
    public static Double getMinecraftVersionNameToDouble(Context context) {
        String version = getMinecraftVersionName(context);
        if (version.isEmpty()) return 0.0;
        return Double.parseDouble(setVersionToDouble(version));
    }

    /**
     * version.indexOf(String,int)
     * Возвращает индекс в данной строке первого вхождения указанной подстроки,
     * начиная с указанного индекса. Если не встречается, возвращается -1.
     *
     * @param version Полное название версии
     * @return Название версии с одной точкой, в виде 1.0 или 0.16
     */
    public static String setVersionToDouble(String version) {
        if (version.isEmpty()) {
            return version;
        }
        int end = version.indexOf(".", 2);
        if (end < 0) {
            return "1.0";
        }
        return version.substring(0, end);
    }

    /**
     * //import
     *
     * //load
     *
     * //importload
     * @param context
     * @param uri
     */
    public static void openContent(Context context, String param, Uri uri) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("minecraft://?=" + param + "=" + uri)));
    }


}
