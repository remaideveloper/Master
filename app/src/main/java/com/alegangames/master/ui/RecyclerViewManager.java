package com.alegangames.master.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.adapter.NativeAdapterRecyclerView;
import com.alegangames.master.model.JsonItemFactory;

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
                GridLayoutManager glm = new GridLayoutManager(getContext(), i);
                int finalI = i;
                glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        Adapter adapter = getAdapter();
                        if (adapter instanceof NativeAdapterRecyclerView){
                            int type = getAdapter().getItemViewType(position);
                            if (type == JsonItemFactory.getViewType(JsonItemFactory.MENU)){
                                if (position == 6)
                                    return finalI;
                                else
                                    return 1;
                            } else if (type == NativeAdapterRecyclerView.CONTENT_NATIVE){
                                return finalI;
                            } else {
                                int a = ((NativeAdapterRecyclerView) adapter).getA();
                                if (a == -1)
                                    return 1;
                                else if (position == a)
                                    return finalI;
                            }
                        } else if (adapter instanceof AdapterRecyclerView) {
                            if (((AdapterRecyclerView)adapter).a == -1)
                                return 1;
                            else if (position == ((AdapterRecyclerView)adapter).a)
                                return finalI;
                        }
                        return 1;
                    }
                });
                super.setLayoutManager(glm);

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
