package com.remaistudio.master.util;


import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;

public class ButtonColorManager {

    public static void setBackgroundButton(Button button, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable background = button.getBackground();
            background.mutate();
            background.setColorFilter(color, PorterDuff.Mode.SRC);
            button.setBackground(background);
        } else {
            button.setBackgroundColor(color);
        }
    }

    public static void setTextColorButton(Button button, int color){
        button.setTextColor(color);
    }
}
