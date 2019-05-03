package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.util.json.JsonHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class ItemCategoryViewModel extends AndroidViewModel {
    private static final String TAG = "ItemCategoryViewModel";
    private MutableLiveData<List<JsonItemContent>> mItemCategoryLiveData = new MutableLiveData();

    private static class ItemCategoryAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContextWeakReference;
        private String mJsonFileName;
        private MutableLiveData<List<JsonItemContent>> mMutableLiveData;

        ItemCategoryAsyncTask(Context context, MutableLiveData<List<JsonItemContent>> mutableLiveData, String str) {
            this.mContextWeakReference = new WeakReference(context);
            this.mMutableLiveData = mutableLiveData;
            this.mJsonFileName = str;
        }

        protected Void doInBackground(Void... voidArr) {
            if (this.mContextWeakReference.get() != null) {
                this.mMutableLiveData.postValue(JsonItemFactory.getListJsonItemFromJsonArray(JsonHelper.getJsonArrayFromStorage((Context) this.mContextWeakReference.get(), this.mJsonFileName)));
            }
            return null;
        }
    }

    public ItemCategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public static ItemCategoryViewModel get(FragmentActivity fragmentActivity) {
        return (ItemCategoryViewModel) ViewModelProviders.of(fragmentActivity).get(ItemCategoryViewModel.class);
    }

    public LiveData<List<JsonItemContent>> getJsonItemFragmentLiveData(String str) {
        if (this.mItemCategoryLiveData.getValue() == null) {
            new ItemCategoryAsyncTask(getApplication(), this.mItemCategoryLiveData, str).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        return this.mItemCategoryLiveData;
    }

    public void notifyViewModel(String str) {
        this.mItemCategoryLiveData.setValue(getJsonItemFragmentLiveData(str).getValue());
    }
}
