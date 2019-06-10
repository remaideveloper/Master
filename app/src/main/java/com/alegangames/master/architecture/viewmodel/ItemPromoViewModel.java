package com.alegangames.master.architecture.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.alegangames.master.model.JsonItemContent;

import java.util.List;

public class ItemPromoViewModel extends AndroidViewModel {
    private static final String TAG = "ItemPromoViewModel";
    private MutableLiveData<List<JsonItemContent>> mItemCategoryLiveData = new MutableLiveData();

    public ItemPromoViewModel(@NonNull Application application) {
        super(application);
    }

    public static ItemPromoViewModel get(FragmentActivity fragmentActivity) {
        return (ItemPromoViewModel) ViewModelProviders.of(fragmentActivity).get(ItemPromoViewModel.class);
    }

    public LiveData<List<JsonItemContent>> getItemLiveData() {
        if (this.mItemCategoryLiveData.getValue() == null) {
//            AsyncTask.execute(new -$$Lambda$ItemPromoViewModel$qu1rUzOni8oxzG4iNlwZf_TsKNk());
        }
        return this.mItemCategoryLiveData;
    }

//    private void getDataItem(JSONArray jSONArray) {
//        this.mItemCategoryLiveData.postValue(getItemList(jSONArray));
//    }
//
//    private List<JsonItemContent> getItemList(JSONArray jSONArray) {
//        return JsonItemFactory.getListJsonItemFromJsonArrayPromo(jSONArray);
//    }
//
//    public void notifyViewModel() {
//        this.mItemCategoryLiveData.setValue(getItemLiveData().getValue());
//    }
}
