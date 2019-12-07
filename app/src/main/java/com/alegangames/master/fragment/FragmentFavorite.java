package com.alegangames.master.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.activity.ActivityFavorite;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.adapter.NativeAdapterRecyclerView;
import com.alegangames.master.architecture.viewmodel.FavoriteViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.util.screen.ScreenSize;

import java.util.List;

public class FragmentFavorite extends FragmentAbstract {

    private static final String TAG = FragmentFavorite.class.getSimpleName();

    private View mRootView;
    private RecyclerViewManager mRecyclerView;
    private NativeAdapterRecyclerView mAdapter;
    private ProgressBar mProgressBarLoading;

    private FavoriteViewModel mFavoriteViewModel;

    public static FragmentFavorite getInstance() {
        return new FragmentFavorite();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ((ActivityFavorite) getActivity()).getSupportActionBar().setTitle(R.string.activity_favorite);
        mRecyclerView = mRootView.findViewById(R.id.recycleView);
        mProgressBarLoading = mRootView.findViewById(R.id.progressBarLoading);

        ActivityAppParent activityAppParent = ((ActivityAppParent) requireActivity());

        //Необходимо задать пустой адаптер
        mAdapter = new NativeAdapterRecyclerView(activityAppParent, mRecyclerView, activityAppParent.mAdMobInterstitial, activityAppParent.mAdMobVideoRewarded);
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(2, getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mFavoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        //Запрашиваем итемы для данной категории и подписываемся на обновления
        mFavoriteViewModel.getJsonItemListLiveData(getFragmentTitle()).observe(this, this::setData);

        return mRootView;
    }

    private void setData(List<JsonItemContent> items) {
        if (items != null) {
            if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
                mAdapter.setFirstItemList(items);
                mAdapter.setOnScrollListener(mRecyclerView);
                mAdapter.setOnLoadMoreListener(() -> mAdapter.loadNextItems(items));
                mRecyclerView.removeAllViews();
                mRecyclerView.setNestedScrollingEnabled(false);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setItemViewCacheSize(10);
                mRecyclerView.setDrawingCacheEnabled(true);
                mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mProgressBarLoading.setVisibility(View.GONE);
            }
        }
    }
}


