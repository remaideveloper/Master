package com.alegangames.master.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NativeViewHolder extends RecyclerView.ViewHolder {

    public NativeViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void init(int position){
//        mAdManager.getNativeAd(itemView, (position+5)/(hNative+1));
//            adMobNativeAdvanceUnified.updateAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
//            if (nativeList.isEmpty()){
//                adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
//                nativeList.add(adMobNativeAdvanceUnified);
//                adMobNativeAdvanceUnified.updateAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
//            }else if (position>=4){
//                int pos = (position+5)/(hNative+1);
//                if (nativeList.size()>pos) {
//                    adMobNativeAdvanceUnified = nativeList.get(pos);
//                    adMobNativeAdvanceUnified.updateAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
//                }
//                else {
//                    adMobNativeAdvanceUnified = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
//                    nativeList.add(adMobNativeAdvanceUnified);
//                    adMobNativeAdvanceUnified.updateAdvanceView(itemView.findViewById(R.id.layoutNativeAds));
//                }
//            }
    }
}
