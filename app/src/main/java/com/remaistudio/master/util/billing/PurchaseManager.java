package com.remaistudio.master.util.billing;

import android.content.Context;

import com.remaistudio.master.Config;
import com.remaistudio.master.R;
import com.remaistudio.master.util.preference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PurchaseManager {

    public static final String COINS_PREF = "coins_pref";
    public static final String ITEMS_PREF = "items_pref";

    public static final String PRODUCT_TEST = "android.test.purchased";
    public static final String PRODUCT_100_COINS = "100_coins";
    public static final String PRODUCT_500_COINS = "500_coins";
    public static final String PRODUCT_2000_COINS = "2000_coins";
    public static final String PRODUCT_6000_COINS = "6000_coins";
    public static final String PRODUCT_20000_COINS = "20000_coins";
    public static final String PRODUCT_50000_COINS = "50000_coins";

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
        return PRODUCT_100_COINS;
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
        productMap.put(PRODUCT_100_COINS, new Product(PRODUCT_100_COINS, context.getString(R.string.coins_amount_format, 100),
                context.getString(R.string.bonus_format, 0), R.drawable.money_icon_1, 100, 0));

        productMap.put(PRODUCT_500_COINS, new Product(PRODUCT_500_COINS, context.getString(R.string.coins_amount_format, 500),
                context.getString(R.string.bonus_format, 100), R.drawable.money_icon_2, 500, 100));

        productMap.put(PRODUCT_2000_COINS, new Product(PRODUCT_2000_COINS, context.getString(R.string.coins_amount_format, 2000),
                context.getString(R.string.bonus_format, 300), R.drawable.money_icon_3, 2000, 300));

        productMap.put(PRODUCT_6000_COINS, new Product(PRODUCT_6000_COINS, context.getString(R.string.coins_amount_format, 6000),
                context.getString(R.string.bonus_format, 500), R.drawable.money_icon_4, 6000, 500));

        productMap.put(PRODUCT_20000_COINS, new Product(PRODUCT_20000_COINS, context.getString(R.string.coins_amount_format, 20000),
                context.getString(R.string.bonus_format, 700), R.drawable.money_icon_5, 20000, 700));

        productMap.put(PRODUCT_50000_COINS, new Product(PRODUCT_50000_COINS, context.getString(R.string.coins_amount_format, 50000),
                context.getString(R.string.bonus_format, 1000), R.drawable.money_icon_6, 50000, 1000));
    }
}
