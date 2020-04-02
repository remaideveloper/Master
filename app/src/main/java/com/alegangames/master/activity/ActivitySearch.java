package com.alegangames.master.activity;

import android.app.SearchManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.adapter.NativeAdapterRecyclerView;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.architecture.viewmodel.SearchActivityViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.screen.ScreenSize;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearch extends ActivityAppParent implements SearchView.OnQueryTextListener {

    public static final String TAG = ActivitySearch.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_search;
    private SearchView searchView;

    private String query = "";
    private SearchActivityViewModel viewModel;

    private RecyclerViewManager mRecyclerView;
    private NativeAdapterRecyclerView mAdapter;
    private ProgressBar mProgressBarLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(LAYOUT);
        ToolbarUtil.setActionBar(this, true);

        //Находим View элементы
        mRecyclerView = findViewById(R.id.recycleView);
        mProgressBarLoading = findViewById(R.id.progressBarLoading);
        mAdMobInterstitial = new AdMobInterstitial(this, Config.INTERSTITIAL_ID);

        //Необходимо задать пустой адаптер
        mAdapter = new NativeAdapterRecyclerView(this, mRecyclerView, mAdMobInterstitial, mAdMobVideoRewarded);
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        String data = getIntent().getStringExtra(FRAGMENT_DATA);

        viewModel = ViewModelProviders.of(this,
                new SearchActivityViewModel.SearchVMFactory(getApplication(), data)).get(SearchActivityViewModel.class);

        //Подписываемся на изменения. После поиска результаты будут отображаться автоматически
        viewModel.getItemsLiveData().observe(this, this::updateRecyclerView);

    }

    private void updateRecyclerView(List<JsonItemContent> items) {
        Log.d(TAG, "QuerySearch updateRecyclerView");

        if (items != null && items.size() != 0) {
            Log.d(TAG, "QuerySearch updateRecyclerView items.size() " + items.size());
            mAdapter.setFirstItemList(items);
            mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(2, this));
            mAdapter.setQuery(query);
            mAdapter.setOnScrollListener(mRecyclerView);
            mAdapter.setOnLoadMoreListener(() -> mAdapter.loadNextItems(items));
            mAdapter.loadNextItems(items);

            mProgressBarLoading.setVisibility(View.GONE);
        } else {
            mAdapter.setItemList(new ArrayList<>());
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSaveEnabled(true);
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setOnQueryTextListener(this);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchMenuItem.expandActionView();

        searchView.setQuery("", false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(TAG, "QuerySearch onQueryTextChange: " + query);

        if (!query.isEmpty()) {
            this.query = query;
            viewModel.search(query);
            return true;
        }

        this.query = "";
        viewModel.search(query);
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle("");
    }
}

