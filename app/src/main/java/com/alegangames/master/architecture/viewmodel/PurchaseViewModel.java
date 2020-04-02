package com.alegangames.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.util.billing.PurchaseManager;

public class PurchaseViewModel extends AndroidViewModel {

    private JsonItemContent mItem;
    private MutableLiveData<Boolean> mPurchaseLiveData;

    public PurchaseViewModel(@NonNull Application application, JsonItemContent item) {
        super(application);
        mItem = item;
    }

    /**
     * Возвращает LiveData с информацией о премиум
     */
    public LiveData<Boolean> getPremiumLiveData() {
        if (mPurchaseLiveData == null) {
            mPurchaseLiveData = new MutableLiveData<>();
            updatePremium();
        }
        return mPurchaseLiveData;
    }

    /**
     * Получает информацию о том, является ли итем премиальным
     * и если да, то куплен ли он. Постит результат в mPremiumLiveData
     */
    public void updatePremium() {
        AsyncTask.execute(() -> {
            boolean premium = mItem.isPremium() && !PurchaseManager.isItemBought(mItem, getApplication());
            if (mPurchaseLiveData != null) mPurchaseLiveData.postValue(premium);
        });
    }

    /**
     * Вызывает покупку итема.
     */
    public void buyItem() {
        PurchaseManager.onItemsBought(mItem, getApplication());
        updatePremium();
    }

    /**
     * Проверяет, есть ли у пользователя достаточно монет
     */
    public boolean requestBuy() {
        int itemPrice = mItem.getPrice();
        int coins = PurchaseManager.getCoins(getApplication());
        return coins >= itemPrice;
    }

    public static class PurchaseViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private JsonItemContent item;


        public PurchaseViewModelFactory(Application application, JsonItemContent item) {
            mApplication = application;
            this.item = item;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new PurchaseViewModel(mApplication, item);
        }
    }

}

