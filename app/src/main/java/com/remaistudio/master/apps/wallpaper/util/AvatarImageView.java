package com.remaistudio.master.apps.wallpaper.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class AvatarImageView extends AppCompatImageView {

    private OnImageViewSizeChanged sizeCallback = null;

    public AvatarImageView(Context context) {
        super(context);
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w == 0 || h == 0) {
            return;
        }
        else{
            if(sizeCallback != null) {
                sizeCallback.invoke(this, w, h);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();
        final double scaleX = (double) widthSize / (double) origW;
        final double scaleY = (double) heightSize / (double) origH;

// Get the drawable real width and height


// Calculate the actual dimensions
        final int actW = (int)(origW * scaleX);
        final int actH = (int)(origH * scaleY);
        //setMeasuredDimension(actW, actH);

    }

    public void setOnImageViewSizeChanged(OnImageViewSizeChanged _callback){
        this.sizeCallback = _callback;

        if (getWidth() != 0 && getHeight() != 0) {
            _callback.invoke(this, getWidth(), getHeight());
        }
    }

    public interface OnImageViewSizeChanged{
        public void invoke(ImageView v, int w, int h);
    }
}
