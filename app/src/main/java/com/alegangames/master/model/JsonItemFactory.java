package com.alegangames.master.model;

import androidx.annotation.Nullable;

import com.alegangames.master.activity.ActivityItem;
import com.alegangames.master.apps.skins.ActivitySkinsCustom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.alegangames.master.adapter.AdapterRecyclerView.CONTENT_MATCH_VIEW_TYPE;
import static com.alegangames.master.adapter.AdapterRecyclerView.CONTENT_VIEW_TYPE;
import static com.alegangames.master.adapter.AdapterRecyclerView.MENU_VIEW_TYPE;
import static com.alegangames.master.adapter.AdapterRecyclerView.OFFER_VIEW_TYPE;

public class JsonItemFactory {

    public static final String TAG = JsonItemFactory.class.getSimpleName();

    public static final String PACKS = "Packs";
    public static final String MAPS = "Maps";
    public static final String BUILDING = "Building";
    public static final String SEEDS = "Seeds";
    public static final String MODS = "Mods";
    public static final String ADDONS = "Addons";
    public static final String SERVERS = "Servers";
    public static final String TEXTURES = "Textures";
    public static final String SKIN_CUSTOM = "Skins";
    public static final String SKIN_STEALER = "Stealer";
    public static final String SKINS_CATEGORY = "Skins Category";
    public static final String WALLPAPERS_CATEGORY = "Wallpapers Category";
    public static final String WALLPAPERS = "Wallpapers";
    public static final String MENU = "Menu";
    public static final String OFFER = "Offer";
    public static final String PROMO_PAGER = "Promo_Pager";
    public static final String FRAGMENT = "Fragment";

    public static Class getClass(String type) {
        switch (type) {
            case PACKS:
            case MAPS:
            case SEEDS:
            case BUILDING:
            case MODS:
            case ADDONS:
            case SERVERS:
            case TEXTURES:
                return ActivityItem.class;
            case SKIN_CUSTOM:
                return ActivitySkinsCustom.class;
        }
        return null;
    }

    public static int getViewType(String type) {
        switch (type) {
            case PACKS:
            case MAPS:
            case SEEDS:
            case BUILDING:
            case MODS:
            case ADDONS:
            case SERVERS:
            case TEXTURES:
                return CONTENT_VIEW_TYPE;
            case SKIN_CUSTOM:
                return CONTENT_MATCH_VIEW_TYPE;
            case OFFER:
                return OFFER_VIEW_TYPE;
            case MENU:
                return MENU_VIEW_TYPE;
        }
        return 0;
    }

    /**
     * @param title Тип элемента
     * @return Наследуется ли данный объект от ItemContent
     */
    public static boolean isContentFragment(String title) {
        switch (title) {
            case PACKS:
            case MAPS:
            case BUILDING:
            case SEEDS:
            case MODS:
            case ADDONS:
            case SERVERS:
            case TEXTURES:
                return true;
            default:
                return false;
        }
    }

    /**
     * Получаем List<JsonItemContent> из JSONArray
     *
     * @param jsonArray Список Json
     * @return Список объектов
     */
    public static List<JsonItemContent> getListJsonItemFromJsonArray(@Nullable JSONArray jsonArray) {

        List<JsonItemContent> itemList = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.isNull(i)) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        itemList.add(new JsonItemContent(jsonObject));
                    }
                }
            }
        }
        return itemList;
    }


    public static Map<String, JsonItemFragment> getListJsonItemFragmentFromJsonArray(@Nullable JSONArray jsonArray){

        Map<String, JsonItemFragment> map = new LinkedHashMap<>();

        if (jsonArray!=null){
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.isNull(i)) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        map.put(jsonObject.optString("title"), new JsonItemFragment(jsonObject));
                    }
                }
            }
        }

        return map;
    }
}
