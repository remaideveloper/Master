package com.alegangames.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.annimon.stream.Stream;
import com.alegangames.master.R;
import com.alegangames.master.fragment.FragmentAbstract;
import com.alegangames.master.fragment.FragmentFavorite;
import com.alegangames.master.model.JsonItem;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.util.FavoriteManager;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.ToastUtil;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    public static final String TAG = FavoriteViewModel.class.getSimpleName();

    private MutableLiveData<List<FragmentAbstract>> mFragmentLiveData = new MutableLiveData<>();
    private MutableLiveData<List<JsonItemContent>> mJsonItemLiveData = new MutableLiveData<>();

    private JsonItemContent mItem;
    private MutableLiveData<Boolean> mFavoriteLiveData;
    private boolean isFavorite;

    public FavoriteViewModel(@NonNull Application application, JsonItemContent item) {
        super(application);
        mItem = item;
    }

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
    }

    public static FavoriteViewModel get(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(FavoriteViewModel.class);
    }

    /**
     * Инициализирует загрузку итемов из предпочтений
     * и возвращает LiveData c фрагментами
     */
    public LiveData<List<FragmentAbstract>> getFragmentListLiveData() {
        if (mFragmentLiveData.getValue() == null) {
            loadData();
        }
        return mFragmentLiveData;
    }

    public LiveData<List<JsonItemContent>> getJsonItemListLiveData(String category) {
        //Фильтрует итемы по категории и устанавливает полученный лист в LiveData
        if (mJsonItemLiveData.getValue() == null) {
            AsyncTask.execute(() -> {
                List<JsonItemContent> categoryList = Stream
                        .of(JsonItemFactory.getListJsonItemFromJsonArray(FavoriteManager.getFavoriteOnPreference(getApplication())))
                        .filter((item) -> StringUtil.containsIgnoreCase(item.getId(), category))
                        .toList();
                mJsonItemLiveData.postValue(categoryList);
            });
        }

        return mJsonItemLiveData;
    }

    public LiveData<Boolean> getFavoriteLiveData() {
        if (mFavoriteLiveData == null) {
            mFavoriteLiveData = new MutableLiveData<>();
            isFavorite = FavoriteManager.isFavoriteContent(getApplication(), mItem);
            AsyncTask.execute(() -> mFavoriteLiveData.postValue(isFavorite));
        }

        return mFavoriteLiveData;
    }

    public void setFavoriteLiveData() {
        if (mFavoriteLiveData == null) mFavoriteLiveData = new MutableLiveData<>();
        AsyncTask.execute(() -> {
            if (isFavorite) {
                FavoriteManager.deleteFavoriteToPreference(getApplication(), mItem);
            } else {
                FavoriteManager.addFavoriteToPreference(getApplication(), mItem);
            }

            isFavorite = !isFavorite;

            int message = isFavorite ? R.string.added_to_favorite : R.string.removed_from_favorite;
            ToastUtil.show(getApplication(), message);

            mFavoriteLiveData.postValue(isFavorite);
        });
    }


    /**
     * Инициализация данных. Загружает все данные из предпочтений.
     * Создает фрагмент для каждой категории и постит лист полученных фрагментов
     * в LiveData - mFragmentLiveData
     */
    private void loadData() {
        AsyncTask.execute(() -> {
            List<FragmentAbstract> mFragmentAbstractList = Stream
                    .of(JsonItemFactory.getListJsonItemFromJsonArray(FavoriteManager.getFavoriteOnPreference(getApplication())))
                    .map(JsonItem::getId) //получаем категории
                    .distinct() //убираем дубликаты
                    .map(category -> {
                        FragmentAbstract fragment = new FragmentFavorite();
                        fragment.setFragmentTitle(category);
                        return fragment;
                    })
                    .toList();
            mFragmentLiveData.postValue(mFragmentAbstractList);
        });
    }

    public static class FavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private JsonItemContent mItem;


        public FavoriteViewModelFactory(Application application, JsonItemContent item) {
            mApplication = application;
            mItem = item;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new FavoriteViewModel(mApplication, mItem);
        }
    }
}
