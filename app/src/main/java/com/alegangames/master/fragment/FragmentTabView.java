package com.alegangames.master.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.adapter.NativeAdapterRecyclerView;
import com.alegangames.master.architecture.viewmodel.ItemsViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.util.screen.ScreenSize;

import java.util.ArrayList;
import java.util.List;

import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_COLUMN;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_DATA;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_SHUFFLE;

public class FragmentTabView extends FragmentAbstract {

    public static final String CATEGORY_ALL = "All";
    private final static String TAG = FragmentTabView.class.getSimpleName();

    //Fragment Settings
    private int mSpanCount;
    private boolean mShuffle;

    private String mFragmentSettings;
    private ProgressBar mProgressBarLoading;
    private RecyclerViewManager mRecyclerView;
    private NativeAdapterRecyclerView mAdapter;
    ItemsViewModel viewModel;
    LiveData<List<JsonItemContent>> liveData;

    public FragmentTabView() {
    }

    public static FragmentTabView getInstance() {
        return new FragmentTabView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        //View
        View mRootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //Находим View элементы
        mRecyclerView = mRootView.findViewById(R.id.recycleView);
        mProgressBarLoading = mRootView.findViewById(R.id.progressBarLoading);

        ActivityAppParent activityAppParent = ((ActivityAppParent) requireActivity());

        //Необходимо задать пустой адаптер
        if (getArguments().getString(FRAGMENT_DATA).equalsIgnoreCase("main.txt"))
            mAdapter = new NativeAdapterRecyclerView(activityAppParent, mRecyclerView, false, activityAppParent.mAdMobInterstitial, activityAppParent.mAdMobVideoRewarded);
        else {
            mAdapter = new NativeAdapterRecyclerView(activityAppParent, mRecyclerView, activityAppParent.mAdMobInterstitial, activityAppParent.mAdMobVideoRewarded);
        }
        mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //Получаем аргументы переданные фрагменту
//        mFragmentSettings = getArguments().getString(FRAGMENT_SETTINGS);

        //Поучаем настройки из mFragmentSettings
//        try {
//            JSONObject jsonObject = new JSONObject(mFragmentSettings);
//            mSpanCount = jsonObject.optInt("column");
        mSpanCount = getArguments().getInt(FRAGMENT_COLUMN);
//            mShuffle = jsonObject.optBoolean("shuffle");
        mShuffle = getArguments().getBoolean(FRAGMENT_SHUFFLE);
//        } catch (JSONException e) {
//            Crashlytics.logException(e);
//        }

        String category = getFragmentTitle();
        if (category.equals(CATEGORY_ALL)) category = "";

        viewModel = ViewModelProviders.of(getParentFragment(),
                new ItemsViewModel.ItemsViewModelFactory(getActivity().getApplication(),
                        getArguments().getString(FRAGMENT_DATA))).get(ItemsViewModel.class);

        liveData = viewModel.getListJsonItemLiveDataCategory(category, mShuffle);
        liveData.observe(this, this::updateRecyclerView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.removeAllViews();
//        mRecyclerView.setAdapter(new ArrayList<>());

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    //При повороте экрана меняем количество колонок в листе, сохраняя позицию
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            int position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(mSpanCount, getContext()));
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.scrollToPosition(position);
        }
    }

    private void updateRecyclerView(List<JsonItemContent> items) {
        Log.d(TAG, "updateRecyclerView");


        if (items != null && items.size() != 0) {
            if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
//                mAdapter.setItemList(new ArrayList<>(items));
                mAdapter.setOnScrollListener(mRecyclerView);
                mAdapter.setOnLoadMoreListener(() -> mAdapter.loadNextItems(new ArrayList<>(items)));
                mAdapter.loadNextItems(items);
//                mRecyclerView.removeAllViews();
                mRecyclerView.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.GridLayout, ScreenSize.getColumnSize(mSpanCount, getContext()));
//                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.getAdapter().notifyDataSetChanged();

//                mAdapter.setOnLoadFullListener(() -> {
//                    View mViewByPosition = mRecyclerView.getLayoutManager().findViewByPosition(4);
//
//                    new ShowcaseView.Builder(getActivity())
//                            .setTarget(new ViewTarget(mViewByPosition))
//                            .setContentTitle("ShowcaseView")
//                            .setContentText("This is highlighting the Home button")
//                            .withMaterialShowcase()
////                            .hideOnTouchOutside()
////                            .singleShot(1)
//                            .build();
//                });

            }
            mProgressBarLoading.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        //liveData.observe(this, this::updateRecyclerView);
        super.onResume();
    }

    @Override
    public void onPause() {
        liveData.removeObservers(this);
        super.onPause();
    }
}