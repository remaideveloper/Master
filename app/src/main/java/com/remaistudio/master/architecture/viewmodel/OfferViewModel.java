package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.remaistudio.master.fragment.FragmentAbstract;
import com.remaistudio.master.fragment.FragmentOfferWall;
import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.util.DeviceManager;
import com.remaistudio.master.util.UrlHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class OfferViewModel extends AndroidViewModel {

    private static final String TAG = OfferViewModel.class.getSimpleName();

    //Observer связан со своим Lifecycle и автоматически отписывается в случае, когда его Lifecycle уничтожен
    private MutableLiveData<List<FragmentAbstract>> mFragmentMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<JsonItemContent>> mItemMutableLiveData = new MutableLiveData<>();

    public static OfferViewModel get(FragmentActivity activity){
        return ViewModelProviders.of(activity).get(OfferViewModel.class);
    }

    public OfferViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Возвращает LiveData с фрагментами. Если фрагментов нет, то запускает их инициализацию
     */
    public LiveData<List<FragmentAbstract>> getFragmentLiveData() {
        if (mFragmentMutableLiveData.getValue() == null) {
//            requestData(this::getDataFragment);
        }
        return mFragmentMutableLiveData;
    }

    /**
     * Возвращает LiveData с офферами. Если итемов нет, то запускает их инициализацию
     */
    public LiveData<List<JsonItemContent>> getItemLiveData() {
        if (mItemMutableLiveData.getValue() == null) {
//            requestData(this::getDataItem);
        }
        return mItemMutableLiveData;
    }

    /**
     * Создает фрагменты, категорий содержащихся в переденном JSONArray
     * Постит фрагменты и итемы в mFragmentMutableLiveData и mItemMutableLiveData
     */
    private void getDataFragment(JSONArray jsonArray) {
        AsyncTask.execute(() -> {
            List<JsonItemContent> itemList = getOfferList(jsonArray);
            List<FragmentAbstract> fragmentList = new ArrayList<>();
            Stream.of(itemList)
                    .map(JsonItemContent::getCategory) //получаем категории
                    .distinct() //убираем дубликаты
                    .forEach(category -> { // для каждой категории создаем фрагмент
                        FragmentOfferWall fragment = new FragmentOfferWall();
                        fragment.setFragmentTitle(category);
                        fragmentList.add(fragment);
                    });

            mFragmentMutableLiveData.postValue(fragmentList);
            mItemMutableLiveData.postValue(itemList);
        });
    }

    /**
     * Создает объекты офферов, содержащихся в переданном JSONArray
     * Постит в mItemMutableLiveData
     */
    private void getDataItem(JSONArray jsonArray) {
        AsyncTask.execute(() -> {
            List<JsonItemContent> itemList = getOfferList(jsonArray);
            mItemMutableLiveData.postValue(itemList);
        });
    }

    /**
     * Создает объекты офферов, содержащихся в переданном JSONArray
     */
    private List<JsonItemContent> getOfferList(JSONArray jsonArray) {
        return Stream.of(JsonItemFactory.getListJsonItemFromJsonArray(jsonArray))
                .filter(item -> !DeviceManager.checkAppInDevice(getApplication(), UrlHelper.getPackageNameFromUrl(item.getFileLink())))
                .collect(Collectors.toList());
    }

//    /**
//     * Получает JsonArray с итемами от сервера, передает
//     * полученный массив в переданный слушатель
//     */
//    private void requestData(Response.Listener<JSONArray> jsonArrayListener) {
//        VolleyManager.getInstance(getApplication()).getJsonArrayRequest(jsonArrayListener, Config.APPS_URL);
//    }

    /**
     * Заного устанавливает данные в mItemMutableLiveData
     */
    public void notifyViewModel() {
        mItemMutableLiveData.setValue(getItemLiveData().getValue());
    }

}
