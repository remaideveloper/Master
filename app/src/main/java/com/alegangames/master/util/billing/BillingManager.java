package com.alegangames.master.util.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.alegangames.master.R;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.annimon.stream.Stream;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingManager implements PurchasesUpdatedListener, LifecycleObserver {

    public static final String TAG = "BillingManager";

    private Activity mActivity;
    private BillingClient mBillingClient;
    private PurchaseManager.InterfacePurchase mInterfacePurchase;
    private String mProductId;
    private List<String> productIds;

    private Map<String, MutableLiveData<SkuDetails>> mSkuDetailsMap = new HashMap<>();

    public BillingManager(FragmentActivity activity, String productId) {
        activity.getLifecycle().addObserver(this);
        mActivity = activity;
        mProductId = productId;
        productIds = PurchaseManager.getProductIds();
        Stream.of(productIds).forEach(item -> mSkuDetailsMap.put(item, new MutableLiveData<>()));
        mBillingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
    }

    public void registerInterfacePurchase(PurchaseManager.InterfacePurchase interfacePurchase) {
        mInterfacePurchase = interfacePurchase;
    }

    /**
     * Вызывается, когда BillingProcessor был инициализирован и готов для покупки
     */
//    @Override
//    public void onBillingInitialized() {
//        //Загрузить список покупок из Google Play
//        if (mBillingProcessor.loadOwnedPurchasesFromGoogle()) {
//            //Восстановить все покупки
////            mBillingProcessor.setPurchaseHistoryRestored();
//        }
//
//        //Сообщаем об окончании загрузке всем ожидающим
//        synchronized (mInitLock) {
//            mInitLock.notifyAll();
//        }
//    }

    /**
     * Вызывается при запросе ID продукта который был успешно куплен
     *
     * @param productId Идентификатор купленного продукта.
     * @param details   Информация о купленном продукте.
     */
//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        Log.d(TAG, "onProductPurchased: ");
//        if (mBillingProcessor != null) {
//
//
//            boolean purchased = mBillingProcessor.isPurchased(productId);
//            if (purchased) {
//                //Добавляем монеты и включаем премиум
//                PurchaseManager.onProductBought(productId, mActivity);
//                //Уничтожаем использованный продукт
//                mBillingProcessor.consumePurchase(productId);
//
//                if (mInterfacePurchase != null)
//                    mInterfacePurchase.onProductPurchased(productId);
//            }
//        }
//    }

    /**
     * Вызывается, когда история покупки была восстановлена,
     * и список всех принадлежащих PRODUCT ID были загружены из Google Play
     */
//    @Override
//    public void onPurchaseHistoryRestored() {
//        Log.d(TAG, "onPurchaseHistoryRestored: ");
//        if (mBillingProcessor != null) {
//            setPremiumPrefence(mActivity, mBillingProcessor.isPurchased(mProductId));
//            List<String> products = mBillingProcessor.listOwnedProducts();
//            for (String product : products) {
//                Log.d(TAG, "onPurchaseHistoryRestored: product " + product);
//            }
//        }
//    }

    /**
     * Вызывается, когда появляется ошибка
     *
     * @param errorCode Код ошибки.
     * @param error     Ошибка.
     */
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        Log.d(TAG, "onBillingError: ");
//        String msg;
//        switch (errorCode) {
//            case 1:
//                msg = "User cancelled";
//                break;
//            case 2:
//                msg = "Billing service unavailable.";
//                break;
//            case 3:
//                msg = "Billing API version unavailable.";
//                break;
//            case 4:
//                msg = "The requested mItem is unavailable.";
//                break;
//            case 5:
//                msg = "Developer error: invalid arguments provided to the API.";
//                break;
//            case 6:
//                msg = "Fatal billing error.";
//                break;
//            case 7:
//                msg = "Item is already owned.";
//                break;
//            case 8:
//                msg = "Failed to consume this mItem since it has not yet been purchased.";
//                break;
//            default:
//                msg = "Unknown billingManager error, error code " + errorCode;
//                break;
//        }
//
//        if (mInterfacePurchase != null)
//            mInterfacePurchase.onBillingError(errorCode);
//
//        Log.d(TAG, "onBillingError: " + msg);
//    }

    /**
     * Инициализация BillingProcessor
     */
    public void initBilling(String apiKey) {
//        if (BillingProcessor.isIabServiceAvailable(mActivity)) {
//            mBillingProcessor = new BillingProcessor(mActivity, apiKey, this);
//            return;
//        }
//        Log.d(TAG, "BillingService Not Available");

        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //здесь мы можем запросить информацию о товарах и покупках
                    querySkuDetails(); //запрос о товарах

                }
            }

            @Override public void onBillingServiceDisconnected() {
                //сюда мы попадем если что-то пойдет не так
            }
        });
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
//        List<String> skuList = PurchaseManager.getProductIds();
        skuDetailsParamsBuilder.setSkusList(productIds).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (SkuDetails skuDetails : list) {
                    mSkuDetailsMap.get(skuDetails.getSku()).postValue(skuDetails);
                }
            }
        });
    }

    /**
     * Освобождение BillingProcessor
     * Вызывать в onDestroy
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
//        if (mBillingProcessor != null)
//            mBillingProcessor.release();
//
//        if (mDetailsExecutor != null)
//            mDetailsExecutor.shutdown();

        if(mBillingClient != null)
            mBillingClient.endConnection();

    }


    /**
     * Покупка продукта
     */
    public void onPurchaseProduct() {
//        if (mBillingProcessor != null) {
//            mBillingProcessor.purchase(mActivity, mProductId);
//            return;
//        }
//        Log.d(TAG, "BillingProcessor == null");

        if (mBillingClient != null) {
//            mBillingProcessor.purchase(mActivity, productId);
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(mSkuDetailsMap.get(mProductId).getValue())
                    .build();
            mBillingClient.launchBillingFlow(mActivity, billingFlowParams);

            return;
        }
    }

    /**
     * Диалоговое окно
     * Предлагаем купить еще монет
     *
     * @param product
     */
    public void getMoreCoinsDialog(Product product) {
        new MaterialAlertDialogBuilder(mActivity)
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
        if (mBillingClient != null) {
//            mBillingProcessor.purchase(mActivity, productId);
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(mSkuDetailsMap.get(productId).getValue())
                    .build();
            mBillingClient.launchBillingFlow(mActivity, billingFlowParams);

            return;
        }
//        Log.d(TAG, "BillingProcessor == null");
    }

    /**
     * Обработка результатов покупки
     *
     * @return относятся ли переданные результаты к BillingProcessor
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
//        return mBillingClient != null && mBillingClient.handleActivityResult(requestCode, resultCode, data);
        return true;
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
//        MutableLiveData<SkuDetails> skuDetailsLiveData = new MutableLiveData<>();
//        if (mBillingProcessor == null) return skuDetailsLiveData;
//        if (mDetailsExecutor == null) mDetailsExecutor = Executors.newSingleThreadExecutor();
//        mDetailsExecutor.execute(() -> {
//            //Если BillingProcessor не инициализирован, результат всегда будет null
//            //Ждем инициализации
//            if (!mBillingProcessor.isInitialized()) {
//                try {
//                    synchronized (mInitLock) {
//                        mInitLock.wait(5000);
//                    }
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            SkuDetails details = mSkuDetailsMap.get(productId);
//            skuDetailsLiveData.postValue(details);
//        });

        return mSkuDetailsMap.get(productId);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            //сюда мы попадем когда будет осуществлена покупка
            ConsumeResponseListener listener = (billingResult1, s) -> {

            };
            Stream.of(list).forEach(item -> {

                PurchaseManager.onProductBought(item.getSku(), mActivity);

                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(item.getPurchaseToken())
                                .setDeveloperPayload(item.getDeveloperPayload())
                                .build();
                mBillingClient.consumeAsync(consumeParams, listener);
                if (mInterfacePurchase != null)
                    mInterfacePurchase.onProductPurchased(item.getSku());
            });

        } else {
            String msg;
//            switch (billingResult.) {
//                case 1:
//                    msg = "User cancelled";
//                    break;
//                case 2:
//                    msg = "Billing service unavailable.";
//                    break;
//                case 3:
//                    msg = "Billing API version unavailable.";
//                    break;
//                case 4:
//                    msg = "The requested mItem is unavailable.";
//                    break;
//                case 5:
//                    msg = "Developer error: invalid arguments provided to the API.";
//                    break;
//                case 6:
//                    msg = "Fatal billing error.";
//                    break;
//                case 7:
//                    msg = "Item is already owned.";
//                    break;
//                case 8:
//                    msg = "Failed to consume this mItem since it has not yet been purchased.";
//                    break;
//                default:
//                    msg = "Unknown billingManager error, error code " + errorCode;
//                    break;
//            }
//
            if (mInterfacePurchase != null)
                mInterfacePurchase.onBillingError(1);
//
//            Log.d(TAG, "onBillingError: " + msg);
        }
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId).getValue())
                .build();
        mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
    }
}
