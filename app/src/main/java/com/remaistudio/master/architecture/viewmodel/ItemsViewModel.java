package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.annimon.stream.Stream;
import com.remaistudio.master.R;
import com.remaistudio.master.fragment.FragmentAbstract;
import com.remaistudio.master.fragment.FragmentTabView;
import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.util.MinecraftHelper;
import com.remaistudio.master.util.SkinUtil;
import com.remaistudio.master.util.StorageUtil;
import com.remaistudio.master.util.StringUtil;
import com.remaistudio.master.util.json.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.remaistudio.master.activity.ActivityAppParent.FRAGMENT_BANNER;
import static com.remaistudio.master.activity.ActivityAppParent.FRAGMENT_COLUMN;
import static com.remaistudio.master.activity.ActivityAppParent.FRAGMENT_DATA;
import static com.remaistudio.master.activity.ActivityAppParent.FRAGMENT_INTERSTITIAL;
import static com.remaistudio.master.activity.ActivityAppParent.FRAGMENT_SHUFFLE;

public class ItemsViewModel extends AndroidViewModel {

    public static final String TAG = ItemsViewModel.class.getSimpleName();

    //Полный список элементов
    private List<JsonItemContent> mListJsonItem = new ArrayList<>();

    //Текущие элементы
    private MutableLiveData<List<JsonItemContent>> mListJsonItemLiveData = new MutableLiveData<>();
    //Хранит список версий. Значения обновляются один раз при первой инициализации
    private MutableLiveData<List<String>> mListVersionLiveData = new MutableLiveData<>();
    //Хранит категории. Слушает изменения mListJsonItemLiveData и пересоздает список
    private MutableLiveData<List<FragmentAbstract>> mListFragmentLiveData = new MutableLiveData<>();
    //LiveData для каждой категории. Новый элемент создается при первом вызове категории и сохраняется в Map
    private Map<String, MutableLiveData<List<JsonItemContent>>> mListCategoryLiveData = new HashMap<>();

    //Имя файла с итемами
    private String mItemFile;
    private String mFragmentTitle = "";
    private boolean mFragmentBanner;
    private boolean mFragmentInterstitial;
    private boolean mFragmentShuffle;
    private int mFragmentColumn;

    Observer observerFragment = new Observer<List<JsonItemContent>>() {
        @Override
        public void onChanged(@Nullable List<JsonItemContent> jsonItemContents) {
            if (jsonItemContents!=null && jsonItemContents.size()!=0) {
                createAndPostTabs(jsonItemContents);
                //mListJsonItemLiveData.removeObserver(this);
            }
        }
    };


//    private String mFragmentSettings = "";




    public ItemsViewModel(@NonNull Application application, String itemFile) {
        super(application);
        mItemFile = itemFile;
        Log.d(TAG, "ItemsViewModel: Constructor");
    }

    public void setSettings(String fragmentTitle, boolean banner, boolean interstitial, boolean shuffle, int column) {
        Log.d(TAG, "setSettings");
        this.mFragmentTitle = fragmentTitle;
        this.mFragmentBanner = banner;
        this.mFragmentInterstitial = interstitial;
        this.mFragmentShuffle = shuffle;
        this.mFragmentColumn = column;
//        this.mFragmentSettings = fragmentSettings;
    }

    /**
     * Получает все категории из листа итемов и возвращает их в виде LiveData с фрагментами
     */
    public LiveData<List<FragmentAbstract>> getListFragmentLiveData() {
        Log.d(TAG, "getListFragmentLiveData");
        checkListJsonItem();
        mListJsonItemLiveData.observeForever(observerFragment);

        return mListFragmentLiveData;
    }

    private void checkListJsonItem() {
        if (mListJsonItemLiveData.getValue() == null) {
            new JsonItemAsyncTask(getApplication(), mListJsonItem, mListJsonItemLiveData, mListVersionLiveData, mItemFile)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Возвращает все версии для данного типа итемов
     *
     * @return LiveData со списком версий
     */
    public LiveData<List<String>> getVersionListLiveData() {
        Log.d(TAG, "getVersionListLiveData");
        return mListVersionLiveData;
    }

    /**
     * Сортирует элементы по версии. Изменения автоматически отображаются у всех подписчиков
     *
     * @param version
     */
    public void onSortListItemLiveDataByVersion(String version) {
        AsyncTask.execute(() -> {
            if (mListJsonItem != null) {
                List<JsonItemContent> versionedList = Stream.of(mListJsonItem)
                        .filter(item -> item.getVersion().contains(version))
                        .toList();
                mListJsonItemLiveData.postValue(versionedList);
            }
        });
    }

    /**
     * Возвращает LiveData с итемами переданной категории
     */
    public LiveData<List<JsonItemContent>> getListJsonItemLiveDataCategory(String category, boolean shuffle) {
        Log.d(TAG, "getListJsonItemLiveDataCategory: " + category);
        checkListJsonItem();

        //Получаем данные для этой категории
        MutableLiveData<List<JsonItemContent>> categoryItem = mListCategoryLiveData.get(category);

        //Если категория еще не была загружена, то создаем ее
        if (categoryItem == null) {
            MutableLiveData<List<JsonItemContent>> categoryData = new MutableLiveData<>();
            //Подписываем созданную категорию на обновления от mListJsonItemLiveData
//            mListJsonItemLiveData.observeForever(items -> new FilterCategoryAsyncTask(categoryData, items, category, shuffle)
//                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            mListJsonItemLiveData.observeForever(new Observer<List<JsonItemContent>>(){

                @Override
                public void onChanged(@Nullable List<JsonItemContent> jsonItemContents) {
                    if (jsonItemContents!=null && jsonItemContents.size()!=0) {
                        new FilterCategoryAsyncTask(categoryData, jsonItemContents, category, shuffle)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //mListJsonItemLiveData.removeObserver(this);
                    }
                }
            });
            categoryItem = categoryData;
            mListCategoryLiveData.put(category, categoryItem);
        }
        return categoryItem;
    }


    /**
     * Загружает List<JsonItemContent> из файла и постит их в LiveData
     */
    private static class JsonItemAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> mContextWeakReference;
        private WeakReference<List<JsonItemContent>> mListJsonItemWeakReference;
        private WeakReference<MutableLiveData<List<JsonItemContent>>> mMutableLiveDataListJsonItemWeakReference;
        private WeakReference<MutableLiveData<List<String>>> mMutableLiveDataListVersionWeakReference;
        private String mItemFile;

        JsonItemAsyncTask(Context context, List<JsonItemContent> listJsonItem, MutableLiveData<List<JsonItemContent>> liveDataListJsonItem, MutableLiveData<List<String>> liveDataListVersion, String itemFile) {
            mContextWeakReference = new WeakReference<>(context);
            mListJsonItemWeakReference = new WeakReference<>(listJsonItem);
            mMutableLiveDataListJsonItemWeakReference = new WeakReference<>(liveDataListJsonItem);
            mMutableLiveDataListVersionWeakReference = new WeakReference<>(liveDataListVersion);
            mItemFile = itemFile;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Log.d(TAG, "doInBackground: mItemFile Url " + StorageUtil.STORAGE + "/content" + "/master/" + mItemFile);

                //Получаем JSONArray из файла
                JSONArray jsonArray = JsonHelper.getJsonArrayFromStorage(mContextWeakReference.get(), mItemFile);
//                VolleyManager.getInstance(mContextWeakReference.get()).getJsonArrayRequest(
//                        response -> {
//                            Log.d(TAG, "doInBackground: " + response);
//                            //Получаем список элементов
//                            List<JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(response);
//
//                            mListJsonItemWeakReference.get().addAll(items);
//                            mMutableLiveDataListJsonItemWeakReference.get().postValue(items);
//
//                            setLiveDataVersionList(items);
//                        },
//                        error -> ToastUtil.showToast(mContextWeakReference.get(), R.string.error),
//                        StorageUtil.STORAGE_APPSCREAT + "/content" + "/master/" + mItemFile);

                //Получаем список элементов
                List<JsonItemContent> items = JsonItemFactory.getListJsonItemFromJsonArray(jsonArray);

                mListJsonItemWeakReference.get().addAll(items);
                mMutableLiveDataListJsonItemWeakReference.get().postValue(items);

                setLiveDataVersionList(items);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Создает список версий и передает их в versions LiveData
         */
        private void setLiveDataVersionList(List<JsonItemContent> items) {
            Set<String> versionsSet = new HashSet<>();
            for (JsonItemContent i : items) {
                String[] versions = getArrayOfStringVersion(i.getVersion());
                Collections.addAll(versionsSet, versions);
            }
            List<String> versionsList = new ArrayList<>();

            if (!versionsSet.isEmpty()) {
                versionsList.addAll(versionsSet);
                Collections.sort(versionsList);
                Collections.reverse(versionsList);
                versionsList.add(0, mContextWeakReference.get().getString(R.string.all_version));
            }

            mMutableLiveDataListVersionWeakReference.get().postValue(versionsList);
        }
    }


    /**
     * Создает список с фрагментами по категориям и передает полученное значение в mListFragmentLiveData
     *
     * @param items
     */
    private void createAndPostTabs(List<JsonItemContent> items) {
        Log.d(TAG, "createAndPostTabs");
        Set<String> categoriesSet = new HashSet<>();
        for (JsonItemContent i : items) {
            String[] categories = i.getCategory().replaceAll("\\s+", "").split(",");
            for (String s : categories) {
                if (!s.isEmpty()) categoriesSet.add(s.toLowerCase());
            }
        }

        if (categoriesSet.isEmpty()) categoriesSet.add("");

        List<FragmentAbstract> tabsList = new ArrayList<>();

//        if (categoriesSet.size() > 1 && isContentFragment(mFragmentTitle)) {
//            FragmentAbstract tab = createTab(CATEGORY_ALL);
//            tabsList.add(tab);
//        }

        for (String category : categoriesSet) {
            FragmentAbstract tab = createTab(category);
            tabsList.add(tab);
        }

        mListFragmentLiveData.postValue(tabsList);
    }

    /**
     * Создает FragmentTabView для переданной категории
     */
    private FragmentAbstract createTab(String category) {
        Log.d(TAG, "createTab");
        FragmentTabView fragment = FragmentTabView.getInstance();
        Bundle bundle = new Bundle();
//        bundle.putString(FRAGMENT_SETTINGS, mFragmentSettings);
        bundle.putString(FRAGMENT_DATA, mItemFile);
        bundle.putBoolean(FRAGMENT_BANNER, mFragmentBanner);
        bundle.putBoolean(FRAGMENT_INTERSTITIAL, mFragmentInterstitial);
        bundle.putBoolean(FRAGMENT_SHUFFLE, mFragmentShuffle);
        bundle.putInt(FRAGMENT_COLUMN, mFragmentColumn);
        fragment.setArguments(bundle);
        fragment.setFragmentTitle(category);

        return fragment;
    }


    /**
     * Получает скины для переданной категории и помещает их в лист
     *
     * @param item  категория скинов
     * @param items лист для размещения скинов
     */
    private static void getSkinsFromCategory(JsonItemContent item, List<JsonItemContent> items) {
        for (int i = 1; i <= item.getCount(); i++) {
            int price = SkinUtil.getSkinPrice(item.getCategory(), i);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", JsonItemFactory.SKIN_CUSTOM);
                jsonObject.put("name", "skin" + i + ".png");
                jsonObject.put("image_link", item.getImageLink() + "skin" + i + ".png");
                jsonObject.put("file_link", item.getFileLink() + "skin" + i + ".png");
                jsonObject.put("category", item.getCategory());
                jsonObject.put("count", item.getCount());
                jsonObject.put("price", price);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            items.add(new JsonItemContent(jsonObject));
        }
    }

    /**
     * Получает обои для переданной категории и помещает их в лист
     *
     * @param item  категория обоев
     * @param items лист для размещения обоев
     */
    private static void getWallpaperFromCategory(JsonItemContent item, List<JsonItemContent> items) {
        for (int i = 1; i <= item.getCount(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", JsonItemFactory.WALLPAPERS);
                jsonObject.put("name", "wallpaper" + i + ".png");
                jsonObject.put("image_link", item.getImageLink() + "wallpaper" + i + ".png");
                jsonObject.put("file_link", item.getImageLink() + "wallpaper" + i + ".png");
                jsonObject.put("category", item.getCategory());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.add(new JsonItemContent(jsonObject));
        }
    }

    /**
     * @param s Строку со списоком версий перечисленных через запятую
     * @return Массив строк "Версий" в виде 1.0.X или 0.16.X
     */
    private static String[] getArrayOfStringVersion(String s) {
        String[] stringsArray = new String[0];
        if (!s.isEmpty()) {
            stringsArray = s.replaceAll("\\s+", "").split(",");
            for (int j = 0; j < stringsArray.length; j++) {
                stringsArray[j] = MinecraftHelper.setVersionToDouble(stringsArray[j]) + ".X";
            }
        }
        return stringsArray;
    }

    /**
     * Фабрика для создания ViewModel.
     */
    public static class ItemsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private Application mApplication;
        private String itemsFile;

        public ItemsViewModelFactory(Application application, String itemsFile) {
            mApplication = application;
            this.itemsFile = itemsFile;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ItemsViewModel(mApplication, itemsFile);
        }

    }

    private static class FilterCategoryAsyncTask extends AsyncTask<Void, Void, List<JsonItemContent>> {

        private WeakReference<MutableLiveData<List<JsonItemContent>>> mMutableLiveDataCategoryWeakReference;
        private WeakReference<List<JsonItemContent>> mListJsonItemWeakReference;
        private String mCategory;
        private boolean mShuffle;

        FilterCategoryAsyncTask(MutableLiveData<List<JsonItemContent>> mutableLiveDataCategory, List<JsonItemContent> listJsonItem, String category, boolean shuffle) {
            mMutableLiveDataCategoryWeakReference = new WeakReference<>(mutableLiveDataCategory);
            mListJsonItemWeakReference = new WeakReference<>(listJsonItem);
            mCategory = category;
            mShuffle = shuffle;
        }

        @Override
        protected List<JsonItemContent> doInBackground(Void... voids) {

            List<JsonItemContent> completeList = new ArrayList<>();

            if (mListJsonItemWeakReference.get() != null && !mListJsonItemWeakReference.get().isEmpty()) {

                List<JsonItemContent> list = new ArrayList<>(mListJsonItemWeakReference.get());

                //Если это список с Скинами
                if (list.get(0).getId().contains(JsonItemFactory.SKINS_CATEGORY)) {
                    List<JsonItemContent> skinsList = new ArrayList<>();
                    //Если категория соответствует переданной, получает скины для этой категории и добавляет в лист
                    Stream.of(list)
                            .filter(item -> item.getCategory().equals(mCategory))
                            .forEach(item -> getSkinsFromCategory(item, skinsList));
//            Log.d(TAG, "onChanged: skin list size " + skinsList.size() + " first item " + skinsList.get(0).getListJsonItemLiveDataCategory());
                    list = skinsList;
                }

                //Если это список с Обоями
                if (list.get(0).getId().contains(JsonItemFactory.WALLPAPERS_CATEGORY)) {
                    List<JsonItemContent> wallPaperList = new ArrayList<>();
                    //Получает обои для категории и добавляет их в лист
                    Stream.of(list).forEach(item -> getWallpaperFromCategory(item, wallPaperList));
                    list = wallPaperList;
                }

                if (mShuffle) Collections.shuffle(list);

                //Фильтрует элементы по категории, сортирует их по премиуму и создает лист
                completeList = Stream.of(list)
                        .filter(item -> StringUtil.containsIgnoreCase(item.getCategory(), mCategory))
                        .sorted((i1, i2) -> Boolean.compare(i2.isPremium(), i1.isPremium()))
                        .toList();

            }

            return completeList;
        }

        @Override
        protected void onPostExecute(List<JsonItemContent> list) {
            if (list != null && mMutableLiveDataCategoryWeakReference.get() != null) {
                mMutableLiveDataCategoryWeakReference.get().setValue(list);
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: " + mFragmentTitle + " object " + this.toString());
        mListJsonItemLiveData.removeObserver(observerFragment);
    }


}
