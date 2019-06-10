package com.alegangames.master.architecture.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TestViewModel extends AndroidViewModel {
    public TestViewModel(@NonNull Application application) {
        super(application);
    }
//    private MutableLiveData<String> liveData = new MutableLiveData();
//
//    private void loadData() {
//    }
//
//    protected void onCleared() {
//    }
//
//    public TestViewModel(@NonNull Application application) {
//        super(application);
//    }
//
//    public LiveData<String> getData() {
//        if (this.liveData == null) {
//            this.liveData = new MutableLiveData();
//            loadData();
//        }
//        return this.liveData;
//    }
//
//    public void setLiveDataValue(String str) {
//        this.liveData.setValue(str);
//    }
//
//    public void postLiveDataValue(String str) {
//        this.liveData.postValue(str);
//    }
//
//    public LiveData<Integer> getIntFromString() {
////        return Transformations.map(this.liveData, -$$Lambda$0xZni40zPvQA8pwdYzSuKmUCwss.INSTANCE);
//    }
}
