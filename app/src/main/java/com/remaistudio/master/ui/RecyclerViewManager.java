package com.remaistudio.master.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewManager extends RecyclerView {

    public enum LayoutManagerEnum {
        GridLayout,
        LinearLayoutHorizontal,
        LinearLayoutVertical
    }

    public RecyclerViewManager(Context context) {
        super(context);
    }

    public RecyclerViewManager(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RecyclerViewManager(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setLayoutManager(LayoutManagerEnum layoutManagerEnum, int i) {
        switch (layoutManagerEnum) {
            case GridLayout:
                if (i <= 0) {
                    i = 1;
                }
                super.setLayoutManager(new GridLayoutManager(getContext(), i));
                return;
            case LinearLayoutVertical:
                super.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
                return;
            case LinearLayoutHorizontal:
                super.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
                return;
            default:
                return;
        }
    }

    public void setLayoutManager(LayoutManagerEnum layoutManagerEnum) {
        setLayoutManager(layoutManagerEnum, 0);
    }
}
