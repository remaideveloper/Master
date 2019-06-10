package com.alegangames.master.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (getCurrentItem() == 0) {
            int childCount = getChildCount();
        }
        try {
            return super.onTouchEvent(motionEvent);
        } catch (Throwable motionEvent2) {
            motionEvent2.printStackTrace();
            return false;
        }
    }

//    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
//        if (getCurrentItem() == 0) {
//            int childCount = getChildCount();
//        }
//        try {
//            return super.onInterceptTouchEvent(motionEvent);
//        } catch (Throwable motionEvent2) {
//            motionEvent2.printStackTrace();
//
//        }
//    }
}
