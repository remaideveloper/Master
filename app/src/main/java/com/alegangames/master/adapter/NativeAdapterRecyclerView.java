package com.alegangames.master.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.ads.admob.AdMobNativeAdvanceUnified;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.model.JsonItemContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NativeAdapterRecyclerView  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public boolean onAd = true;

    public static final int CONTENT_NATIVE = R.layout.layout_native_ads;

    private List<Integer> adIndex =  new ArrayList<>();

    private List<JsonItemContent> mItemList = new ArrayList<>();
    private AdapterRecyclerView adapterRecyclerView;
    private RecyclerView mRecyclerView;
    private int lastPosition = -1;

    private static final int VISIBLE_THRESHOLD_ITEM = 4; //За сколько элементов до конца листа начинается загрузка следующей части
    private static final int COUNT_LOAD_ITEM = 4; //Количествyо загружаемых элементов при подгрузке списка
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean threshold;
    private AdapterRecyclerView.OnLoadMoreListener onLoadMoreListener;
    private AdapterRecyclerView.OnLoadFullListener mOnLoadFullListener;

    private List<AdMobNativeAdvanceUnified> nativeList = new ArrayList<>();

    private int maxCountNative = 3;
    private int hNative = 8;
    private int countShowNative = 0;

    public NativeAdapterRecyclerView(FragmentActivity activity, RecyclerView recyclerView, AdMobInterstitial adMobInterstitial, AdMobVideoRewarded adMobVideoRewarded) {
        this.mRecyclerView = recyclerView;
        adapterRecyclerView = new AdapterRecyclerView(activity, recyclerView, adMobInterstitial, adMobVideoRewarded);
//        adIndex.add(4);
//        adIndex.add(17);
//        adIndex.add(30);
    }

    public NativeAdapterRecyclerView(FragmentActivity activity, RecyclerView recyclerView, boolean onAd, AdMobInterstitial adMobInterstitial, AdMobVideoRewarded adMobVideoRewarded) {
        this.mRecyclerView = recyclerView;
        adapterRecyclerView = new AdapterRecyclerView(activity, recyclerView, adMobInterstitial, adMobVideoRewarded);
        this.onAd = onAd;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == CONTENT_NATIVE) {
            ++countShowNative;
            return new NativeViewHolder(inflater.inflate(R.layout.layout_native_ads, parent, false));
        }
        else
            return adapterRecyclerView.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NativeViewHolder) {
            ((NativeViewHolder) holder).init(position);
        }
        else
            adapterRecyclerView.onBindViewHolder(this, holder,getRealPosition(position), position);
    }

    public int getA(){
        return adapterRecyclerView.a;
    }

    @Override
    public int getItemCount() {
        int additionalContent = 0;
//        if (onAd && mItemList.size() > 0 && maxCountNative > 0 && mItemList.size() > maxCountNative) {
        if (onAd) {
//            additionalContent = mItemList.size() / ((countShowNative*countShowNative*hNative)+4);
//            int size = mItemList.size();
//            if (size>adIndex.get(2))
//                additionalContent = 3;
//            else {
//                if (size>adIndex.get(1))
//                    additionalContent = 2;
//                else
//                    additionalContent = 1;
//            }

            int size = mItemList.size();
            if (size>3){
                additionalContent +=1;
                if (size>4)
                    additionalContent += (int) Math.floor(size/(hNative+4));
            }
        }
        return mItemList.size() + additionalContent;
    }

    private int getRealPosition(int position) {
        Log.d("TEST_POSITION", String.valueOf(position));
        if (!onAd || Config.NO_ADS) {
            return position;
        } else {
//            return position - position / ((countShowNative*countShowNative*hNative)+4);
//            if (position>adIndex.get(2))
//                return position - 3;
//            else if (position>adIndex.get(1))
//                return position - 2;
//            else if (position>adIndex.get(0))
//                return position - 1;
            if (position>=4)
                return position - (int) Math.floor((position+4)/(hNative+1));
            return position;

        }
//        return position;
    }

    @Override
    public int getItemViewType(int position) {
//        if (onAd && position>0 && countShowNative<maxCountNative && (position%((countShowNative*countShowNative*hNative)+4)==0))
//            return CONTENT_NATIVE;
//        if (onAd && position>0 && countShowNative<maxCountNative && adIndex.contains(position))
        if (!Config.NO_ADS && onAd && position!=0 && (position % ((int)Math.floor(position/hNative)*(hNative+1)+4) == 0))
            return CONTENT_NATIVE;
        else
            return adapterRecyclerView.getItemViewType(getRealPosition(position));
    }


    /**
     * Данный метод применяет анимацию,
     * когда границы view становиться видимыми.
     */
    private void setAnimation(View viewToAnimate, int position) {
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
        adapterRecyclerView.setOnScrollListener(recyclerView);
    }


    public void setItemList(List<JsonItemContent> itemList) {
        adapterRecyclerView.setItemList(itemList);
        this.mItemList = itemList;
    }

    /**
     * Первая инициализация списка
     * mItemList должен быть пустой
     *
     * @param list
     */
    public void setFirstItemList(List<JsonItemContent> list) {
        adapterRecyclerView.setFirstItemList(list);
        if (this.mItemList.isEmpty() && list != null && list.size() != 0 && list.get(0) != null) {
            try {
                this.mItemList = Collections.singletonList(list.get(0));
            } catch (Exception e) {
                this.mItemList = list;
            }
        }
    }


    public void setLoading(boolean loading) {
        this.loading = loading;
        adapterRecyclerView.setLoading(loading);
    }

    /**
     * Предел списка
     * Установить TRUE если в адаптер загружен весь список
     * Чтобы предотвратить подгрузку новых данных
     *
     * @param threshold
     */
    private void setThreshold(boolean threshold) {
        this.threshold = threshold;
        adapterRecyclerView.setThreshold(threshold);
    }

    public void setOnLoadMoreListener(AdapterRecyclerView.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        adapterRecyclerView.setOnLoadMoreListener(onLoadMoreListener);
    }

    public void setOnLoadFullListener(AdapterRecyclerView.OnLoadFullListener onLoadFullListener) {
        adapterRecyclerView.setOnLoadFullListener(onLoadFullListener);
    }


    public interface OnLoadFullListener {
        void onLoadFull();
    }

    public void setQuery(String query) {
        adapterRecyclerView.setQuery(query);
    }

    /**
     * Загрузить следущий список элементов
     *
     * @param list Список всех элементов
     */
    public void loadNextItems(List<JsonItemContent> list) {
//        adapterRecyclerView.loadNextItems(list);
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
        adapterRecyclerView.notifyItems();
    }

    public class NativeViewHolder extends RecyclerView.ViewHolder{

        AdMobNativeAdvanceUnified adMobNativeAdvanceUnified;

        public NativeViewHolder(@NonNull View itemView) {
            super(itemView);
//            adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
//            adMobNativeAdvanceUnified.addNativeAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
        }

        public void init(int position){
            if (nativeList.isEmpty()){
                adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
                nativeList.add(adMobNativeAdvanceUnified);
                adMobNativeAdvanceUnified.addNativeAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
            }else if (position>=4){
                int pos = (position+5)/(hNative+1);
                if (nativeList.size()>pos) {
                    adMobNativeAdvanceUnified = nativeList.get(pos);
                    adMobNativeAdvanceUnified.updateAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
                }
                else {
                    adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
                    nativeList.add(adMobNativeAdvanceUnified);
                    adMobNativeAdvanceUnified.addNativeAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
                }
            }
        }
    }
}
