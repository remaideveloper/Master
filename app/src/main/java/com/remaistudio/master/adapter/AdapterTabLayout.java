package com.remaistudio.master.adapter;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.remaistudio.master.R;
import com.remaistudio.master.fragment.FragmentAbstract;
import com.remaistudio.master.ui.CustomViewPager;
import com.remaistudio.master.util.ColorList;

import java.util.ArrayList;
import java.util.List;

public class AdapterTabLayout extends FragmentStatePagerAdapter {

    private final static String TAG = AdapterTabLayout.class.getSimpleName();
    public final static int VIEW_PAGER = R.id.view_pager;
    public final static int TAB_LAYOUT = R.id.tab_layout;
    private List<FragmentAbstract> mTabs = new ArrayList<>();
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;

    public AdapterTabLayout(@NonNull FragmentManager fragmentManager, CustomViewPager viewPager, TabLayout tabLayout, List<? extends FragmentAbstract> tabs) {
        super(fragmentManager);
        Log.d(TAG, "AdapterTabLayout");

        this.mViewPager = viewPager;
        this.mTabLayout = tabLayout;
        this.mTabs.addAll(tabs);
        setViewPager();
        setTabLayout();
    }

    public static void init(@NonNull FragmentManager fragmentManager, CustomViewPager viewPager, TabLayout tabLayout, List<? extends FragmentAbstract> tabs) {
        new AdapterTabLayout(fragmentManager, viewPager, tabLayout, tabs);
    }


    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getFragmentTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof FragmentAbstract) {
            int position = mTabs.indexOf(object);
            if (position >= 0) return position;
        }
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }



    private void setViewPager() {
        mViewPager.setAdapter(this);
        mViewPager.setOffscreenPageLimit(1);
    }

    private void setTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);

        if (getCount() <= 1) {
            mTabLayout.setVisibility(View.GONE);
        } else if (getCount() > 1 && getCount() < 4) {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else if (getCount() >= 4) {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        }
    }

    public void setAdapterList(List<FragmentAbstract> tabs) {
        Log.d(TAG, "setAdapterList");
        this.mTabs.clear();
        this.mTabs.addAll(tabs);
        setViewPager();
        setTabLayout();
        notifyDataSetChanged(); //Вызвать после измения данных в адаптере
    }

}
