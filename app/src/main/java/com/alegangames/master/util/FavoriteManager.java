package com.alegangames.master.util;


import android.content.Context;

import com.alegangames.master.R;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.util.preference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    /**
     * @return JSONArray с избранным контентом
     */
    public static JSONArray getFavoriteOnPreference(Context context) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context.getApplicationContext()).getString(context.getString(R.string.favorite_pref), "[]"));
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }
        return jsonArray;
    }

    /**
     * Добаляет контент в избранное
     *
     * @param item элемент для добавления
     */
    public static void addFavoriteToPreference(Context context, JsonItemContent item) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context.getApplicationContext()).getString(context.getApplicationContext().getString(R.string.favorite_pref), "[]"));
            jsonArray.put(item.getJsonObject());
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }
        if (jsonArray != null) {
            SharedPreferenceManager.getInstance(context.getApplicationContext()).putString(context.getApplicationContext().getString(R.string.favorite_pref), jsonArray.toString());
        }
    }


    /**
     * Удаляет контент из избранное
     *
     * @param item Контент который необходимо удалить
     */
    public static void deleteFavoriteToPreference(Context context, JsonItemContent item) {
        List<JsonItemContent> list = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            //Берем JSONArray избранных элементов
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context.getApplicationContext()).getString(context.getApplicationContext().getString(R.string.favorite_pref), "[]"));
            //Добавляем все элементы в List, кроме того что нужно удлать
            for (JsonItemContent itemContent : JsonItemFactory.getListJsonItemFromJsonArray(jsonArray)) {
                if (!StorageUtil.checkUrlPrefix(itemContent.getFileLink()).equals(StorageUtil.checkUrlPrefix(item.getFileLink()))) {
                    list.add(itemContent);
                }
            }
            //Очищаем JSONArray избранных элементов
            jsonArray = new JSONArray();
            //Добавляем в JSONArray избранные элементы из List
            for (JsonItemContent jsonItemContent : list) {
                jsonArray.put(jsonItemContent.getJsonObject());
            }
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }
        if (jsonArray != null) {
            SharedPreferenceManager.getInstance(context.getApplicationContext()).putString(context.getApplicationContext().getString(R.string.favorite_pref), jsonArray.toString());
        }
    }

    /**
     * @param context         Контекст
     * @param jsonItemContent Контент который необходимо проверить
     * @return True если jsonItemContent является избранным
     */
    public static boolean isFavoriteContent(Context context, JsonItemContent jsonItemContent) {
        List<JsonItemContent> list = JsonItemFactory.getListJsonItemFromJsonArray(FavoriteManager.getFavoriteOnPreference(context));
        for (JsonItemContent itemContent : list) {
            if (itemContent.getName().equals(jsonItemContent.getName())) {
                return true;
            }

        }
        return false;
    }

}
