package com.alegangames.master.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.alegangames.master.BuildConfig;
import com.alegangames.master.R;

import java.io.File;

public class BlockLauncherHelper {

    public static final int REQUEST_CODE_MOD = 101;
    public static final int REQUEST_CODE_TEXTURE = 102;
    public static final int REQUEST_CODE_SKIN = 103;

    public static void importSkin(final Context context, final File file) {

        if (!MinecraftHelper.isBlockLauncherInstalled(context)) return;

        new AlertDialog.Builder(context)
                .setTitle(R.string.file_saved)
                .setMessage(R.string.skin_dialog_description)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    if (file != null && file.exists()) {
                        try {
                            Intent localIntent = new Intent("net.zhuoweizhang.mcpelauncher.action.SET_SKIN");
                            localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri data;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            } else {
                                data = Uri.fromFile(file);
                            }
                            localIntent.setDataAndType(data, "image/png");
                            PackageManager pm = context.getPackageManager();
                            if (localIntent.resolveActivity(pm) != null) {
                                ((Activity) context).startActivityForResult(localIntent, REQUEST_CODE_SKIN);
                            }
                        } catch (Exception e) {
//                            Crashlytics.logException(e);
                        }
                    }
                })
                .create()
                .show();
    }

    public static void importMod(final Context context, final File file) {

        if (!MinecraftHelper.isBlockLauncherInstalled(context)) return;

        new AlertDialog.Builder(context)
                .setTitle(R.string.file_saved)
                .setMessage(R.string.mod_dialog_description)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    if (file.exists()) {
                        try {
                            Intent localIntent = new Intent("net.zhuoweizhang.mcpelauncher.action.IMPORT_SCRIPT");
                            localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri data;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            } else {
                                data = Uri.fromFile(file);
                            }
                            localIntent.setDataAndType(data, "*/*");
                            PackageManager pm = context.getPackageManager();
                            if (localIntent.resolveActivity(pm) != null) {
                                ((Activity) context).startActivityForResult(localIntent, REQUEST_CODE_MOD);
                            }
                        } catch (Exception e) {
//                            Crashlytics.logException(e);
                        }
                    }

                })
                .create()
                .show();
    }

    public static void importTexture(final Context context, final File file) {

        if (!MinecraftHelper.isBlockLauncherInstalled(context)) return;

        new AlertDialog.Builder(context)
                .setTitle(R.string.file_saved)
                .setMessage(R.string.texture_dialog_blocklauncher_description)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    if (file.exists()) {
                        try {
                            Intent localIntent = new Intent("net.zhuoweizhang.mcpelauncher.action.SET_TEXTUREPACK");
                            localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri data;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            } else {
                                data = Uri.fromFile(file);
                            }
                            localIntent.setDataAndType(data, "*/*");
                            PackageManager pm = context.getPackageManager();
                            if (localIntent.resolveActivity(pm) != null) {
                                ((Activity) context).startActivityForResult(localIntent, REQUEST_CODE_TEXTURE);
                            }
                        } catch (Exception e) {
//                            Crashlytics.logException(e);
                        }
                    }

                })
                .create()
                .show();
    }
}
