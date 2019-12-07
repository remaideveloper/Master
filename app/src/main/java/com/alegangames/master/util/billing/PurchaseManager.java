package com.alegangames.master.util.billing;

import android.content.Context;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.util.preference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PurchaseManager {

    public static final String COINS_PREF = "coins_pref";
    public static final String ITEMS_PREF = "items_pref";

//    public static final String PRODUCT_TEST = "android.test.purchased";
//    public static final String PRODUCT_NO_ADS = "no_ads";

    private static final int DEFAULT_COINS = 0;

    public interface InterfacePurchase {
        void onProductPurchased(String productId);

        void onBillingError(int errorCode);
    }


    private static Map<String, Product> productMap;

    public static boolean isItemBought(JSONObject item, Context context) {
        String boughtItems = SharedPreferenceManager.getInstance(context).getString(ITEMS_PREF, "");
        //Если есть админ режим то любой контент считается купленным
        return boughtItems.contains(item.toString()) || Config.ADMIN_MODE;
    }

    public static int getCoins(Context context) {
        return SharedPreferenceManager.getInstance(context).getInt(COINS_PREF, DEFAULT_COINS);
    }

    public static int addCoins(int addCoins, Context context) {
        int coins = SharedPreferenceManager.getInstance(context).getInt(COINS_PREF, DEFAULT_COINS);
        coins = coins + addCoins;
        SharedPreferenceManager.getInstance(context).putInt(COINS_PREF, coins);
        return coins;
    }

    public static void addAdItem(Context context, JsonItemContent mItem){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context).getString(ITEMS_PREF, "[]"));
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }
        if (jsonArray != null) {
            JSONObject item = null;
            int index = -1;
            List<JSONObject> list = new ArrayList<>();

            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject!=null){
                    list.add(jsonObject);
                    if (index == -1 && jsonObject.optJSONObject("item").toString().equals(mItem.mJSONObject.toString())){
                        item = jsonObject;
                        index = i;
                    }
                }
            }

            try {

                if (item == null) {
                    item = new JSONObject();
                    item.put("item", mItem.mJSONObject);
                    item.put("count", 1);
                    list.add(item);
                } else {
                    int count = item.optInt("count") + 1;
                    item.remove("count");
                    item.put("count", count);
                    list.set(index, item);
                }
            } catch (JSONException e){}

            jsonArray = new JSONArray(list);

            SharedPreferenceManager.getInstance(context).putString(ITEMS_PREF, jsonArray.toString());

        }
    }

    public static int getBoughtItem(Context context, JSONObject mItem){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context).getString(ITEMS_PREF, "[]"));
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }

        if (jsonArray!=null){
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject!=null &&  jsonObject.optJSONObject("item")!=null && jsonObject.optJSONObject("item").toString().equals(mItem.toString()))
                    return jsonObject.optInt("count");
            }
        }
        return 0;
    }

    /**
     * Покупка элемента. Добавляет итем в список приобретенных и списывает монеты
     *
     * @param item    итем для покупки
     * @param context контекст для доступа к ресурсам
     * @return успешность покупки
     */
    public static boolean onItemsBought(JSONObject item, Context context) {
        int coins = SharedPreferenceManager.getInstance(context).getInt(COINS_PREF, DEFAULT_COINS);
        int itemPrice = item.optInt("price");

        if (coins < itemPrice) return false;

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SharedPreferenceManager.getInstance(context).getString(ITEMS_PREF, "[]"));
        } catch (JSONException e) {
//            Crashlytics.logException(e);
        }

        if (jsonArray != null) {
            jsonArray.put(item);

            SharedPreferenceManager.getInstance(context).putString(ITEMS_PREF, jsonArray.toString());
            SharedPreferenceManager.getInstance(context).putInt(COINS_PREF, coins - itemPrice);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Возвращает массив купленных премиум итемов
     */
    public static JSONArray getBoughtItems(Context context) {
        String itemsString = SharedPreferenceManager.getInstance(context).getString(ITEMS_PREF, "[]");
        JSONArray itemsArray = null;
        try {
            itemsArray = new JSONArray(itemsString);
        } catch (JSONException ex) {
//            Crashlytics.logException(ex);
        }

        return itemsArray;
    }

    /**
     * Добавляет монеты из продукта. После вызова метода, продукт нужно уничтожить в
     * BillingManager
     */
    public static void onProductBought(String productId, Context context) {
        Product product = getProduct(productId, context);
        addCoins(product.getAmount(), context);
    }

    /**
     * Продукт вызываемый по умолчанию при попытке загрузить премиум контент
     */
    public static String getDefaultProductId() {
        return "";
    }

    /**
     * Возвращает список доступных для покупки продуктов
     *
     * @param context контекст для доступа к ресурсам
     * @return лист с продуктами
     */
    public static List<Product> getProducts(Context context) {
        if (productMap == null) {
            createProducts(context);
        }

        return new ArrayList<>(productMap.values());
    }

    /**
     * Возвращает продукт по id
     *
     * @param productId id требуемого продукта
     * @param context   контекст для доступа к ресурсам
     * @return продукт привязанный к переданному id
     */
    public static Product getProduct(String productId, Context context) {
        if (productMap == null) {
            createProducts(context);
        }
        return productMap.get(productId);
    }

    /**
     * Создает Map с продуктами
     *
     * @param context контекст для доступа к ресурсам
     */
    private static void createProducts(Context context) {
        productMap = new LinkedHashMap<>();
    }

}
