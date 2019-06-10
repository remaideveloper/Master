package com.alegangames.master.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityFavorite;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.architecture.viewmodel.FavoriteViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;

import java.util.List;

public class FragmentFavorite extends FragmentAbstract {

    private static final String TAG = FragmentFavorite.class.getSimpleName();

    private View mRootView;
    private RecyclerViewManager mRecyclerView;
    private AdapterRecyclerView mAdapter;
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

        //Необходимо задать пустой адаптер
        mAdapter = new AdapterRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout);
        mRecyclerView.setAdapter(mAdapter);

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
                mRecyclerView.setHasFixedSize(false);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mProgressBarLoading.setVisibility(View.GONE);
            }
        }
    }
}


