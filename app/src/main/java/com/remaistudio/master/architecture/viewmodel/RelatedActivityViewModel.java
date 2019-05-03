package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.util.json.JsonHelper;

import org.json.JSONArray;

import java.util.List;

public class RelatedActivityViewModel extends AndroidViewModel{

    public static final String TAG = RelatedActivityViewModel.class.getSimpleName();

    private String data;
    private MutableLiveData<List<JsonItemContent>> itemsData = new MutableLiveData<>();
    private List<JsonItemContent> itemList;

    public RelatedActivityViewModel(@NonNull Application application, String data) {
        super(application);
        this.data = data;
    }

    public static RelatedActivityViewModel get(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(RelatedActivityViewModel.class);
    }


    public MutableLiveData<List<JsonItemContent>> getItemsLiveData() {
        if(itemList == null || itemList.size() == 0) {
            loadData();
        }
        return itemsData;
    }

    /**
     * Загружает все элементы из файла
     */
    private void loadData() {

//        VolleyManager.getInstance(getApplication()).getJsonArrayRequest(
//                response -> {
//                    Log.d(TAG, "doInBackground: " + response);
//                    //Получаем список элементов
//                    //Получаем список List<Item> из JsonArray
//                    List <JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(response);
//                    //Перемешать список элементов
//                    itemList = items;
//                    itemsData.postValue(items);
//                },
//                error -> ToastUtil.showToast(getApplication(), R.string.error),
//                StorageUtil.STORAGE_APPSCREAT + "/content" + "/master/" + data);
        AsyncTask.execute(() -> {
            //Получаем JSONArray из файла

            JSONArray jsonArray = JsonHelper.getJsonArrayFromStorage(
                    getApplication(),
                    data);

            //Получаем список элементов
            List<JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(jsonArray);
            itemList = items;
            itemsData.postValue(items);
        });
    }

    /**
     * Фабрика необходимая для создания экземпляра класса
     */
    public static class RelatedFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private String itemsFile;


        public RelatedFactory(Application application, String itemsFile) {
            mApplication = application;
            this.itemsFile = itemsFile;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new RelatedActivityViewModel(mApplication, itemsFile);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

