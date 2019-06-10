package com.alegangames.master.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alegangames.master.R;
import com.alegangames.master.adapter.AdapterTabLayout;
import com.alegangames.master.architecture.viewmodel.FavoriteViewModel;
import com.alegangames.master.fragment.FragmentAbstract;
import com.alegangames.master.ui.ToolbarUtil;

import java.util.List;

public class ActivityFavorite extends ActivityAppParent {

    private static final String TAG = ActivityFavorite.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_view_pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ToolbarUtil.setToolbar(this, true);
        FavoriteViewModel.get(this).getFragmentListLiveData().observe(this, this::onChanged);
    }

    public void onChanged(@Nullable List<FragmentAbstract> list) {
        if (list != null && !list.isEmpty()) {
            AdapterTabLayout.init(
                    getSupportFragmentManager(),
                    findViewById(AdapterTabLayout.VIEW_PAGER),
                    findViewById(AdapterTabLayout.TAB_LAYOUT),
                    list);
            findViewById(R.id.progressBarLoading).setVisibility(View.GONE);
        } else {
            //Если лист пустой выводим сообщение и закрываем активити
            findViewById(R.id.emptyText).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBarLoading).setVisibility(View.GONE);
        }
    }

}
