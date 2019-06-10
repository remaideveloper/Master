package com.alegangames.master.architecture.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alegangames.master.model.JsonItemContent;

import java.lang.ref.WeakReference;
import java.util.List;

public class RelatedItemViewModel extends AndroidViewModel {

    public static final String TAG = RelatedItemViewModel.class.getSimpleName();

    private JsonItemContent mItem;

    private MutableLiveData<List<JsonItemContent>> mRelatedItemListLiveData = new MutableLiveData<>();

    public RelatedItemViewModel(@NonNull Application application, JsonItemContent item) {
        super(application);
        this.mItem = item;
    }

    public LiveData<List<JsonItemContent>> getRelatedItemsLiveData() {
        if (mRelatedItemListLiveData.getValue() == null) {
            new RelatedItemAsyncTask(getApplication(), mItem, mRelatedItemListLiveData).execute();
        }
        return mRelatedItemListLiveData;
    }

    public void notifyViewModel() {
        mRelatedItemListLiveData.setValue(getRelatedItemsLiveData().getValue());
    }

    /**
     * Фабрика необходимая для создания экземпляра класса
     */
    public static class RelatedItemViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private JsonItemContent mItem;

        public RelatedItemViewModelFactory(Application application, JsonItemContent item) {
            mApplication = application;
            mItem = item;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new RelatedItemViewModel(mApplication, mItem);
        }
    }

    private static class RelatedItemAsyncTask extends AsyncTask<Void, Void, List<JsonItemContent>> {

        private WeakReference<Context> mContextWeakReference;
        private WeakReference<JsonItemContent> mJsonItemContentWeakReference;
        private WeakReference<MutableLiveData<List<JsonItemContent>>> mRelatedItemListLiveDataWeakReference;

        public RelatedItemAsyncTask(Context context, JsonItemContent jsonItemContent, MutableLiveData<List<JsonItemContent>> relatedItemListLiveData) {
            mContextWeakReference = new WeakReference<>(context);
            mJsonItemContentWeakReference = new WeakReference<>(jsonItemContent);
            mRelatedItemListLiveDataWeakReference = new WeakReference<>(relatedItemListLiveData);
        }

        @Override
        protected List<JsonItemContent> doInBackground(Void... voids) {
//            VolleyManager.getInstance(mContextWeakReference.get()).getJsonArrayRequest(
//                    response -> {
//                        Log.d(TAG, "doInBackground: " + response);
//                        //Получаем список элементов
//                        //Получаем список List<Item> из JsonArray
//                        List <JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(response);
//                        //Перемешать список элементов
//                        Collections.shuffle(items);
//                        //Фильтруем список
//                        items = Stream.of(items)
//                                .filter((i) -> !mJsonItemContentWeakReference.get().getName().equals(i.getName())) //Не включать элемент с таким же именем
//                                .limit(ActivityItem.RELATED_COUNT_DEFAULT) //Ограничиваем длинну списка
//                                .toList();
//                        this.onPostExecute(items);
//                    },
//                    error -> ToastUtil.showToast(mContextWeakReference.get(), R.string.error),
//                    StorageUtil.STORAGE_APPSCREAT + "/content" + "/master/" + ContentHelper.getJsonNameItem(mJsonItemContentWeakReference.get()));
            return null;
        }

        @Override
        protected void onPostExecute(List<JsonItemContent> list) {
            if (list != null && !list.isEmpty() && mRelatedItemListLiveDataWeakReference.get() != null) {
                mRelatedItemListLiveDataWeakReference.get().setValue(list);
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: " + mItem.getName());

    }
}