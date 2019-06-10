package com.alegangames.master.util.screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSize {

    private static final int TABLET_WIDTH = 600;

    public static int getHeightPX(final Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return defaultDisplay.getHeight();
    }

    public static int getWidthPX(final Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return defaultDisplay.getWidth();
    }

    public static int getWidthDP(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = context.getResources().getDisplayMetrics().density;
        float dpWidth = (outMetrics.widthPixels / density);
        return (int) (dpWidth);
    }

    public static int getHeightDP(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        float density = context.getResources().getDisplayMetrics().density;
        float dpHeight = (displayMetrics.heightPixels / density);
        return (int) (dpHeight);
    }

    public static boolean isTablet(Context context) {
        return ScreenSize.getWidthDP(context) >= TABLET_WIDTH;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static int getColumnSize(int spanCount, Context context) {
        if (spanCount == 0) {
            //Если количество столбцов не указано
            return 1;
        } else if (isTablet(context) && isLandscape(context)) {
            //Если планшет и ориентация альбомная
            return spanCount + 2;
        } else if (isTablet(context) || isLandscape(context)) {
            //Если планшет или ориентация альбомная
            return spanCount + 1;
        } else {
            //Если телефон и ориентация портретная
            return spanCount;
        }
    }
}
