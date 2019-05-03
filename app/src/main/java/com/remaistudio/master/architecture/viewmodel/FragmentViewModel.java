package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.model.JsonItemFragment;
import com.remaistudio.master.util.json.JsonHelper;

import java.util.Map;

public class FragmentViewModel extends AndroidViewModel {
    public static final String TAG = "FragmentViewModel";
    private MutableLiveData<Map<String, JsonItemFragment>> mJsonItemLiveData = new MutableLiveData();

    public FragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public static FragmentViewModel get(FragmentActivity fragmentActivity) {
        return (FragmentViewModel) ViewModelProviders.of(fragmentActivity).get(FragmentViewModel.class);
    }

    public LiveData<Map<String, JsonItemFragment>> getFragmentMapLiveData() {
        if (this.mJsonItemLiveData.getValue() == null) {
            loadData();
        }
        return this.mJsonItemLiveData;
    }

    private void loadData() {
        AsyncTask.execute(() -> mJsonItemLiveData.postValue(JsonItemFactory.getListJsonItemFragmentFromJsonArray(JsonHelper.getJsonArrayFromStorage(getApplication(), "data-master.json"))));
    }
}

