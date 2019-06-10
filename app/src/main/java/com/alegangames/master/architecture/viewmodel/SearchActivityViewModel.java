package com.alegangames.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.annimon.stream.Stream;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.util.json.JsonHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SearchActivityViewModel extends AndroidViewModel{

    public static final String TAG = SearchActivityViewModel.class.getSimpleName();

    private String data;
    private MutableLiveData<List<JsonItemContent>> itemsData = new MutableLiveData<>();
    private List<JsonItemContent> itemList = new ArrayList<>();

    public SearchActivityViewModel(@NonNull Application application, String data) {
        super(application);
        this.data = data;
    }

    public MutableLiveData<List<JsonItemContent>> getItemsLiveData() {
        if(itemList == null || itemList.size() == 0) {
            loadData();
        }
        return itemsData;
    }

    /**
     * Фильтрует элементы по имени и передает результат в LiveData
     * @param query запрос для поиска
     */
    public void search(String query) {
        Log.d(TAG, "QuerySearch search: " + query);
        AsyncTask.execute(() -> {
            List<JsonItemContent> filtered = Stream.of(itemList)
                    .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                    .toList();

            Log.d(TAG, "QuerySearch search: AsyncTask " + query);

            itemsData.postValue(filtered);
        });
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
//                    List<JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(response);
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
    public static class SearchVMFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private String itemsFile;


        public SearchVMFactory(Application application, String itemsFile) {
            mApplication = application;
            this.itemsFile = itemsFile;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SearchActivityViewModel(mApplication, itemsFile);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
