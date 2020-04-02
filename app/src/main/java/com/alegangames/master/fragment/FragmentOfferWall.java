package com.alegangames.master.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alegangames.master.Config;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.architecture.viewmodel.AppViewModel;
import com.annimon.stream.Stream;
import com.alegangames.master.R;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.screen.ScreenSize;

import java.util.List;

public class FragmentOfferWall extends FragmentAbstract {

    private static final String TAG = FragmentOfferWall.class.getSimpleName();
    private static final int SPAN_COUNT_DEFAULT = 3;

    private View mRootView;
    private ProgressBar mProgressBarLoading;
    private RecyclerViewManager mRecyclerView;
    private AdapterRecyclerView mAdapter;
    private AppViewModel viewModel;

    public static FragmentOfferWall getInstance() {
        return new FragmentOfferWall();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //Находим View элементы
        mRecyclerView = mRootView.findViewById(R.id.recycleView);
        mProgressBarLoading = mRootView.findViewById(R.id.progressBarLoading);

        //Необходимо задать пустой адаптер
        mAdapter = new AdapterRecyclerView(getActivity(), mRecyclerView, new AdMobInterstitial(requireActivity(), Config.INTERSTITIAL_ID), new AdMobVideoRewarded(requireActivity()));
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout);
        mRecyclerView.setAdapter(mAdapter);

        viewModel = AppViewModel.get(getActivity());
        viewModel.getItemLiveData().observe(this, this::onChanged);

        return mRootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Вызывается для принудительного обновления RecyclerView
        viewModel.notifyViewModel();
    }

    public void onChanged(@Nullable List<JsonItemContent> list) {
        if (list == null || list.size() == 0) return;
        //Показываем только обьекты у которых название категории совпадает с названием фрагмента
        List<JsonItemContent> sortedList = Stream.of(list)
                .filter((item) -> StringUtil.containsIgnoreCase(item.getCategory(), getFragmentTitle()))
                .toList();
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            mAdapter.setItemList(sortedList);
            mAdapter.setOnScrollListener(mRecyclerView);
//            mAdapter.setOnLoadMoreListener(() -> mAdapter.loadNextItems(sortedList));
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(SPAN_COUNT_DEFAULT, getContext()));
            mProgressBarLoading.setVisibility(View.GONE);
        }
    }
}
