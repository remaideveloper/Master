package com.alegangames.master.util.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.util.preference.SharedPreferenceManager;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alegangames.master.util.billing.PurchaseManager.PRODUCT_NO_ADS;

public class BillingManager implements BillingProcessor.IBillingHandler, LifecycleObserver {

    public static final String TAG = "BillingManager";

    private Activity mActivity;
    private BillingProcessor mBillingProcessor;
    private PurchaseManager.InterfacePurchase mInterfacePurchase;
    private String mProductId;

    private ExecutorService mDetailsExecutor;
    private final Object mInitLock = new Object();

    public BillingManager(FragmentActivity activity, String productId) {
        activity.getLifecycle().addObserver(this);
        mActivity = activity;
        mProductId = productId;
    }

    public void registerInterfacePurchase(PurchaseManager.InterfacePurchase interfacePurchase) {
        mInterfacePurchase = interfacePurchase;
    }

    /**
     * Вызывается, когда BillingProcessor был инициализирован и готов для покупки
     */
    @Override
    public void onBillingInitialized() {
        //Загрузить список покупок из Google Play
        if (mBillingProcessor.loadOwnedPurchasesFromGoogle()) {
            //Восстановить все покупки
//            mBillingProcessor.setPurchaseHistoryRestored();
        }

        //Сообщаем об окончании загрузке всем ожидающим
        synchronized (mInitLock) {
            mInitLock.notifyAll();
        }
    }

    /**
     * Вызывается при запросе ID продукта который был успешно куплен
     *
     * @param productId Идентификатор купленного продукта.
     * @param details   Информация о купленном продукте.
     */
    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: ");
        if (mBillingProcessor != null) {


            boolean purchased = mBillingProcessor.isPurchased(productId);
            if (purchased) {
                //Добавляем монеты и включаем премиум
                if (productId.equalsIgnoreCase(PRODUCT_NO_ADS))
                    Config.NO_ADS = true;
                else
                    PurchaseManager.onProductBought(productId, mActivity);
                //Уничтожаем использованный продукт
                mBillingProcessor.consumePurchase(productId);

                if (mInterfacePurchase != null)
                    mInterfacePurchase.onProductPurchased(productId);
            }
        }
    }

    /**
     * Вызывается, когда история покупки была восстановлена,
     * и список всех принадлежащих PRODUCT ID были загружены из Google Play
     */
    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
        if (mBillingProcessor != null) {
            setPremiumPrefence(mActivity, mBillingProcessor.isPurchased(mProductId));
            List<String> products = mBillingProcessor.listOwnedProducts();
            for (String product : products) {
                Log.d(TAG, "onPurchaseHistoryRestored: product " + product);
            }
        }
    }

    /**
     * Вызывается, когда появляется ошибка
     *
     * @param errorCode Код ошибки.
     * @param error     Ошибка.
     */
    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError: ");
        String msg;
        switch (errorCode) {
            case 1:
                msg = "User cancelled";
                break;
            case 2:
                msg = "Billing service unavailable.";
                break;
            case 3:
                msg = "Billing API version unavailable.";
                break;
            case 4:
                msg = "The requested mItem is unavailable.";
                break;
            case 5:
                msg = "Developer error: invalid arguments provided to the API.";
                break;
            case 6:
                msg = "Fatal billing error.";
                break;
            case 7:
                msg = "Item is already owned.";
                break;
            case 8:
                msg = "Failed to consume this mItem since it has not yet been purchased.";
                break;
            default:
                msg = "Unknown billingManager error, error code " + errorCode;
                break;
        }

        if (mInterfacePurchase != null)
            mInterfacePurchase.onBillingError(errorCode);

        Log.d(TAG, "onBillingError: " + msg);
    }

    /**
     * Инициализация BillingProcessor
     */
    public void initBilling(String apiKey) {
        if (BillingProcessor.isIabServiceAvailable(mActivity)) {
            mBillingProcessor = new BillingProcessor(mActivity, Config.MERCHANT_ID, apiKey, this);
            return;
        }
        Log.d(TAG, "BillingService Not Available");
    }

    /**
     * Освобождение BillingProcessor
     * Вызывать в onDestroy
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onReleaseBillingProcessor() {
        if (mBillingProcessor != null)
            mBillingProcessor.release();

        if (mDetailsExecutor != null)
            mDetailsExecutor.shutdown();

    }

    /**
     * Изменить локальные настройки переменной premium_pref
     *
     * @param context
     * @param isPremium Передать true, если Premium куплен, false если нет.
     */
    private void setPremiumPrefence(Context context, boolean isPremium) {
        SharedPreferenceManager.getInstance(context.getApplicationContext()).putBoolean("premium_pref", isPremium);
    }

    /**
     * Покупка продукта
     */
    public void onPurchaseProduct() {
        if (mBillingProcessor != null) {
            mBillingProcessor.purchase(mActivity, mProductId);
            return;
        }
        Log.d(TAG, "BillingProcessor == null");
    }

    /**
     * Диалоговое окно
     * Предлагаем купить еще монет
     *
     * @param product
     */
    public void getMoreCoinsDialog(Product product) {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.get_more_coins)
                .setMessage(mActivity.getString(R.string.get_more_coins_description, product.getAmount(), product.getBonus()))
                .setPositiveButton(R.string.get, (dialog, which) -> onPurchaseProduct(product.getId()))
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    /**
     * Покупка продукта по идентификатору
     */
    public void onPurchaseProduct(String productId) {
        if (mBillingProcessor != null) {
            mBillingProcessor.purchase(mActivity, productId);
            return;
        }
        Log.d(TAG, "BillingProcessor == null");
    }

    /**
     * Обработка результатов покупки
     *
     * @return относятся ли переданные результаты к BillingProcessor
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mBillingProcessor != null && mBillingProcessor.handleActivityResult(requestCode, resultCode, data);
    }

    /**
     * Аснхронно получает данные о продукте. Если BillingProcessor еще
     * на инициализирован, то ждет инициализации.
     *
     * @param productId требуемый продукт
     * @return LiveData после получения деталей продукта, данные
     * будут переданы в LiveData. Данные могут быть null.
     */
    public LiveData<SkuDetails> getProductDetailsAsynk(String productId) {
        MutableLiveData<SkuDetails> skuDetailsLiveData = new MutableLiveData<>();
        if (mBillingProcessor == null) return skuDetailsLiveData;
        if (mDetailsExecutor == null) mDetailsExecutor = Executors.newSingleThreadExecutor();
        mDetailsExecutor.execute(() -> {
            //Если BillingProcessor не инициализирован, результат всегда будет null
            //Ждем инициализации
            if (!mBillingProcessor.isInitialized()) {
                try {
                    synchronized (mInitLock) {
                        mInitLock.wait(5000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            SkuDetails details = mBillingProcessor.getPurchaseListingDetails(productId);
            skuDetailsLiveData.postValue(details);
        });

        return skuDetailsLiveData;
    }

}
