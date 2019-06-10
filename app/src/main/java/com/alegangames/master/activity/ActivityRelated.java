package com.alegangames.master.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alegangames.master.R;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.architecture.viewmodel.RelatedActivityViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.screen.ScreenSize;

import java.util.ArrayList;
import java.util.List;

public class ActivityRelated extends ActivityAppParent {

    public static final String TAG = ActivityRelated.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_recycler_view;
    private static final int SPAN_COUNT_DEFAULT = 2;

    private RecyclerViewManager mRecyclerView;
    private AdapterRecyclerView mAdapter;
    private ProgressBar mProgressBarLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(LAYOUT);
        ToolbarUtil.setToolbar(this, true);

        //Находим View элементы
        mRecyclerView = findViewById(R.id.recycleView);
        mProgressBarLoading = findViewById(R.id.progressBarLoading);

        //Необходимо задать пустой адаптер
        mAdapter = new AdapterRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.LinearLayoutVertical);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(false);

        AdMobBanner adMobBanner = new AdMobBanner(this);
        adMobBanner.onCreate();

        String data = getIntent().getStringExtra(FRAGMENT_DATA);

        RelatedActivityViewModel viewModel = ViewModelProviders.of(this,
                new RelatedActivityViewModel.RelatedFactory(getApplication(), data)).get(RelatedActivityViewModel.class);

        //Подписываемся на изменения. После поиска результаты будут отображаться автоматически
        viewModel.getItemsLiveData().observe(this, this::onChanged);

    }

    //При повороте экрана меняем количество колонок в листе, сохраняя позицию
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            int position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(SPAN_COUNT_DEFAULT, this));
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.scrollToPosition(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onChanged(List<JsonItemContent> items) {
        if (items != null && items.size() != 0) {
//            mAdapter.setFirstItemList(items);
            mAdapter.setOnScrollListener(mRecyclerView);
            mAdapter.setItemList(items);
//            mAdapter.setOnLoadMoreListener(() -> mAdapter.loadNextItems(items));
            //mAdapter.loadNextItems(items);
            mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(SPAN_COUNT_DEFAULT, this));

            mProgressBarLoading.setVisibility(View.GONE);
        } else {
            mAdapter.setItemList(new ArrayList<>());
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

}

