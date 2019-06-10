package com.alegangames.master.adapter;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.R;
import com.alegangames.master.holder.ItemContentViewHolder;
import com.alegangames.master.holder.ItemMenuViewHolder;
import com.alegangames.master.holder.ItemOfferViewHolder;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MENU_VIEW_TYPE = R.layout.card_type_menu;
    public static final int OFFER_VIEW_TYPE = R.layout.card_type_offer;
    public static final int PROMO_PAGER_VIEW_TYPE = R.layout.card_type_promo;
    public static final int CONTENT_VIEW_TYPE = R.layout.card_type_item_array;
    public static final int CONTENT_MATCH_VIEW_TYPE = R.layout.card_type_item_array_match;

    private static final String TAG = AdapterRecyclerView.class.getSimpleName();
    private List<JsonItemContent> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String query;
    private int lastPosition = -1;


    private static final int VISIBLE_THRESHOLD_ITEM = 4; //За сколько элементов до конца листа начинается загрузка следующей части
    private static final int COUNT_LOAD_ITEM = 4; //Количество загружаемых элементов при подгрузке списка
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean threshold;
    private OnLoadMoreListener onLoadMoreListener;
    private OnLoadFullListener mOnLoadFullListener;

    public AdapterRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup .getContext());
        try {
            View view = inflater.inflate(viewType, viewGroup, false);
            switch (viewType) {
                case CONTENT_VIEW_TYPE:
                case CONTENT_MATCH_VIEW_TYPE:
                    return new ItemContentViewHolder(view);
                case MENU_VIEW_TYPE:
                    return new ItemMenuViewHolder(view);
                case OFFER_VIEW_TYPE:
                    return new ItemOfferViewHolder(view);
            }
        } catch (Resources.NotFoundException ex) {
            //Костыль. При ошибке для избежания краша создается холдер, с дефолтным лэйаутом.
            //Имя итема отправляется в консоль для анализа
            ex.printStackTrace();
            if (mItemList != null && mItemList.size() != 0) {
                JsonItemContent item = mItemList.get(0);
                if (item != null && item.getName() != null) {
//                    Crashlytics.log(item.getName());
                }
            }

            View view = inflater.inflate(R.layout.card_type_item_array_match, viewGroup, false);
            return new ItemContentViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            switch (holder.getItemViewType()) {
                case CONTENT_VIEW_TYPE:
                case CONTENT_MATCH_VIEW_TYPE:
                    ((ItemContentViewHolder) holder).setHolder(getItemAtPosition(position), query);
                    break;
                case MENU_VIEW_TYPE:
                    ((ItemMenuViewHolder) holder).setHolder(getItemAtPosition(position));
                    break;
                case OFFER_VIEW_TYPE:
                    ((ItemOfferViewHolder) holder).setHolder(getItemAtPosition(position));
                    break;
            }
            setAnimation(holder.itemView, position);
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return JsonItemFactory.getViewType(mItemList.get(position).getId());
    }

    /**
     * Данный метод применяет анимацию,
     * когда границы view становиться видимыми.
     */
    public void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            try {
                Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_bottom);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled");

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }

                Log.d(TAG, "onScrolled: loading " + loading);
                Log.d(TAG, "onScrolled: threshold " + threshold);
                Log.d(TAG, "onScrolled: totalItemCount " + totalItemCount);
                Log.d(TAG, "onScrolled: lastVisibleItem + VISIBLE_THRESHOLD_ITEM " + lastVisibleItem + " + " + VISIBLE_THRESHOLD_ITEM);

                if (!loading && !threshold && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD_ITEM)) {
                    loading = true;
                    // End has been reached
                    // Do something
                    if (onLoadMoreListener != null) {
                        Log.d(TAG, "onScrolled: onLoadMore");
                        onLoadMoreListener.onLoadMore();
                    }

                }
            }
        });
    }


    public void setItemList(List<JsonItemContent> itemList) {
        this.mItemList = itemList;
    }

    /**
     * Первая инициализация списка
     * mItemList должен быть пустой
     *
     * @param list
     */
    public void setFirstItemList(List<JsonItemContent> list) {
        if (this.mItemList.isEmpty() && list != null && list.size() != 0 && list.get(0) != null) {
            try {
                this.mItemList = Collections.singletonList(list.get(0));
            } catch (Exception e) {
                this.mItemList = list;
            }
        }
    }

    public JsonItemContent getItemAtPosition(int position) {
        return mItemList.get(position);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * Предел списка
     * Установить TRUE если в адаптер загружен весь список
     * Чтобы предотвратить подгрузку новых данных
     *
     * @param threshold
     */
    public void setThreshold(boolean threshold) {
        this.threshold = threshold;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnLoadFullListener(OnLoadFullListener onLoadFullListener) {
        this.mOnLoadFullListener = onLoadFullListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnLoadFullListener {
        void onLoadFull();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Загрузить следущий список элементов
     *
     * @param list Список всех элементов
     */
    public void loadNextItems(List<JsonItemContent> list) {
        final int currentSize = getItemCount();
        if (list.size() > currentSize + COUNT_LOAD_ITEM) {
            //Заменяем текущий список на новый список с дополнительными элементами
            setItemList(list.subList(0, currentSize + COUNT_LOAD_ITEM));
            //Перерисовываем только новые элементы
//            mRecyclerView.post(() -> notifyItemRangeInserted(currentSize, currentSize + COUNT_LOAD_ITEM));
            mRecyclerView.post(this::notifyDataSetChanged);
        } else {
            //Если в полученном списке меньше объектов чем нужно получить, добавляем весь список
            setThreshold(true);
            setItemList(list);
            mRecyclerView.post(this::notifyDataSetChanged);

            if (mOnLoadFullListener != null)
                mOnLoadFullListener.onLoadFull();
        }

        mRecyclerView.post(this::notifyDataSetChanged);
        setLoading(false);
    }

    public void notifyItems() {
        mRecyclerView.post(this::notifyDataSetChanged);
    }

}

