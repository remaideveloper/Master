package com.remaistudio.master.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.remaistudio.master.util.network.NetworkManager;

public abstract class FragmentAbstract extends Fragment {

    private final static String TAG = "FragmentAbstract";
    public InterfaceFragment mFragmentInterface;

    private String mTitle;

    public interface InterfaceFragment {

        void onShowBannerOfFragment(boolean b);

        void onShowInterstitialOfFragment(boolean b);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        try {
            mFragmentInterface = (InterfaceFragment) context;
        } catch (Exception e) {
            Log.e(TAG, "Activity must implement InterfaceFragment.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        if (mFragmentInterface != null) {
            mFragmentInterface = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        NetworkManager.onNetworkCondition(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Для избежания TransactionTooLargeException
//            super.onSaveInstanceState(outState);
    }

    public String getFragmentTitle() {
        return mTitle != null ? mTitle : "";
    }

    public void setFragmentTitle(String title) {
        if (title != null) {
            this.mTitle = title;
        }
    }

}