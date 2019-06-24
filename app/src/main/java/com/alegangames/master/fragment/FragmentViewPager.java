package com.alegangames.master.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.alegangames.master.fragment.sideSheet.DemoSideSheetDialogFragment;
import com.annimon.stream.Stream;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityMain;
import com.alegangames.master.activity.ActivitySearch;
import com.alegangames.master.adapter.AdapterTabLayout;
import com.alegangames.master.architecture.viewmodel.ItemsViewModel;
import com.alegangames.master.model.JsonItemFactory;

import java.util.ArrayList;
import java.util.List;

import static com.alegangames.master.Config.SINGLE_APP;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_BANNER;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_COLUMN;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_DATA;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_INTERSTITIAL;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_SHUFFLE;
import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_TITLE;
import static com.alegangames.master.activity.ActivityAppParent.VERSION;

public class FragmentViewPager extends FragmentAbstract {

    private static final String TAG = FragmentViewPager.class.getSimpleName();

    public View mRootView;
    private AdapterTabLayout mAdapterTabLayout;

    private List<FragmentAbstract> mTabs = new ArrayList<>();
    private List<String> mVersionList = new ArrayList<>();

    //Fragment Arguments
    private String mFragmentData;
    private String mFragmentSettings;
    private String mFragmentTitle;

    //Fragment Settings
    private String mVersion = "";
    private boolean shouldShowBanner;
    private boolean shouldShowInterstitial;

    //View
    private ProgressBar mProgressBarLoading;

    private ItemsViewModel viewModel;
    LiveData<List<FragmentAbstract>> liveDataFragment;
    LiveData<List<String>> listLiveData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //Контролирует сохраняется ли экземпляр фрагмента во время повторного создания Activity (например, из изменения конфигурации).
        //Это можно использовать только с фрагментами, не входящими в задний стек.
        //Если установлено, жизненный цикл фрагмента будет немного отличаться, когда воссоздается действие:
        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mRootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        mTabs.clear();
        mVersionList.clear();

        mProgressBarLoading = mRootView.findViewById(R.id.progressBarLoading);

        //Чистит стэк если фрагмент уже был инициализирован
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            int stackSize = fragments.size();

            for (int i = 0; i < stackSize; i++) {
                Log.d(TAG, "Pop back stack");
                getChildFragmentManager().beginTransaction().remove(fragments.get(i)).commitAllowingStateLoss();
            }
        }

        mAdapterTabLayout = new AdapterTabLayout(
                getChildFragmentManager(),
                mRootView.findViewById(AdapterTabLayout.VIEW_PAGER),
                getActivity().findViewById(AdapterTabLayout.TAB_LAYOUT),
                mTabs);

        //Получаем обязательные аргументы переданные фрагменту
        mFragmentData = getArguments().getString(FRAGMENT_DATA);
//        mFragmentSettings = getArguments().getString(FRAGMENT_SETTINGS);
        mFragmentTitle = getArguments().getString(FRAGMENT_TITLE);
        mVersion = getArguments().getString(VERSION);

        Log.d(TAG, "onCreateView: mFragmentTitle " + mFragmentTitle);

        if (mVersion == null) mVersion = "";

        //Получаем настройки из mFragmentSettings и вызываем рекламу в главном потоке
//        try {
//            JSONObject jsonObject = new JSONObject(mFragmentSettings);
//            shouldShowBanner = jsonObject.optBoolean("banner");
//            shouldShowInterstitial = jsonObject.optBoolean("interstitital");
//        } catch (JSONException e) {
//            Crashlytics.logException(e);
//        }

        if (mFragmentInterface != null) {
            shouldShowBanner = getArguments().getBoolean(FRAGMENT_BANNER);
            shouldShowInterstitial = getArguments().getBoolean(FRAGMENT_INTERSTITIAL);
            mFragmentInterface.onShowBannerOfFragment(shouldShowBanner);
            mFragmentInterface.onShowInterstitialOfFragment(getArguments().getBoolean(FRAGMENT_INTERSTITIAL));
        }

        //Создаем ViewModel
        viewModel = ViewModelProviders.of(this, new ItemsViewModel.ItemsViewModelFactory(getActivity().getApplication(),
                mFragmentData)).get(ItemsViewModel.class);
        viewModel.setSettings(mFragmentTitle,
                getArguments().getBoolean(FRAGMENT_BANNER),
                getArguments().getBoolean(FRAGMENT_INTERSTITIAL),
                getArguments().getBoolean(FRAGMENT_SHUFFLE),
                getArguments().getInt(FRAGMENT_COLUMN));

        //Подписываемся на категории
        liveDataFragment = viewModel.getListFragmentLiveData();

        //Получить версии
        listLiveData = viewModel.getVersionListLiveData();
        return mRootView;
    }

    public void initV(List<String> list){
        if (list != null && !list.isEmpty()) {
            mVersionList.clear();
            mVersionList.addAll(list);
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG,"PAUSE");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        listLiveData.removeObservers(this);
        liveDataFragment.removeObservers(this);
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        mTabs.clear();
        mAdapterTabLayout.setAdapterList(mTabs);
    }

    @Override
    public void onResume() {
        if (!liveDataFragment.hasObservers()) {
            liveDataFragment.observe(this, this::onTabsReady);
        }
        if (!listLiveData.hasObservers()) {
            listLiveData.observe(this, this::initV);
        }
        super.onResume();
        //listLiveData.observe(this, this::initV);
        //liveDataFragment.observe(this, this::onTabsReady);
        Log.d(TAG, "onResume");

        //Если приложение не отдельное, проиграть анимацию и создать HomeButton
        if (SINGLE_APP) return;

        new Handler().postDelayed(() -> {
            try {
                if (getActivity() != null) {
                    ((ActivityMain) getActivity()).mNavigationViewUtil.onUpdateDrawerToggle();
                    ((ActivityMain) getActivity()).getSupportActionBar().setTitle(mFragmentTitle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 300);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onTabsReady(List<FragmentAbstract> tabs) {
        mTabs = Stream.of(tabs).filter(fragment -> fragment != null).toList();
        mAdapterTabLayout.setAdapterList(mTabs);
        mProgressBarLoading.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (mFragmentTitle != null) {

            if (JsonItemFactory.isContentFragment(mFragmentTitle)) {
                menu.setGroupVisible(R.id.group_search, true);

                if (mVersionList != null && mVersionList.size() > 1) {
                    menu.setGroupVisible(R.id.group_version, true);
                }

            }
        }
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.version:
                initDialogList(getContext());
                return true;
            case R.id.search:
                Intent intent = new Intent(getContext(), ActivitySearch.class);
                intent.putExtra(FRAGMENT_DATA, mFragmentData);
                intent.putExtra(FRAGMENT_TITLE, mFragmentTitle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //обновляем баннер, т.к его размер может измениться
        mFragmentInterface.onShowBannerOfFragment(shouldShowBanner);
    }

    /**
     * Показать AlertDialog со списком версий
     *
     * @param context
     */
    public void initDialogList(@Nullable Context context) {
        if (context == null) return;

        Log.d(TAG, "initDialogList");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        };

        if (mVersionList != null && !mVersionList.isEmpty()) {
            adapter.addAll(mVersionList);
        }

//        DemoSideSheetDialogFragment dialog = new DemoSideSheetDialogFragment();
//        dialog.setCancelable(true);
//        dialog.show(getActivity().getSupportFragmentManager(), DemoSideSheetDialogFragment.class.getName());

        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.sort_by_version)
                .setAdapter(adapter, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            mVersion = "";
                            break;
                        default:
                            mVersion = mVersionList.get(which).substring(0, mVersionList.get(which).length() - 1);
                            break;
                    }
                    viewModel.onSortListItemLiveDataByVersion(mVersion);
                })
                .create()
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}