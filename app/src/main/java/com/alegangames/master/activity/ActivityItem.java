package com.alegangames.master.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.adapter.AdapterRecyclerView;
import com.alegangames.master.adapter.ImageViewPagerAdapter;
import com.alegangames.master.ads.admob.AdMobInterstitial;
import com.alegangames.master.ads.admob.AdMobNativeAdvanceUnified;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.apps.builder.dialog.BuildingInstaller;
import com.alegangames.master.architecture.viewmodel.AppViewModel;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.architecture.viewmodel.FavoriteViewModel;
import com.alegangames.master.architecture.viewmodel.PurchaseViewModel;
import com.alegangames.master.events.DownloadEvent;
import com.alegangames.master.fragment.FragmentBuy;
import com.alegangames.master.interfaces.InterfaceDownload;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.ui.RecyclerViewManager;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.BlockLauncherHelper;
import com.alegangames.master.util.ButtonColorManager;
import com.alegangames.master.util.ColorList;
import com.alegangames.master.util.ContentHelper;
import com.alegangames.master.util.MinecraftHelper;
import com.alegangames.master.util.ServerHelper;
import com.alegangames.master.util.SocialManager;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.UrlHelper;
import com.alegangames.master.util.billing.BillingManager;
import com.alegangames.master.util.billing.PurchaseManager;
import com.alegangames.master.util.json.SerializableJSONObject;
import com.alegangames.master.util.permision.PermissionManager;
import com.google.android.material.button.MaterialButton;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alegangames.master.Config.API_KEY;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_CANCELED;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_MEMORY_ERROR;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_NETWORK_ERROR;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_SUCCESS;
import static com.alegangames.master.util.BlockLauncherHelper.REQUEST_CODE_MOD;
import static com.alegangames.master.util.BlockLauncherHelper.REQUEST_CODE_TEXTURE;

public class ActivityItem extends ActivityAppParent implements BuildingInstaller.BuildingListener,
        PermissionManager.InterfacePermission, InterfaceDownload, PurchaseManager.InterfacePurchase, AdMobVideoRewarded.Listener{

    public static final String TAG = ActivityItem.class.getSimpleName();

    public static final String JSON_OBJECT_KEY = "JSON_OBJECT_KEY";

    public static final int SPAN_COUNT_DEFAULT = 2;
    public static final int RELATED_COUNT_DEFAULT = 8;

    private static final int LAYOUT = R.layout.activity_item;

    private Menu mMenu;
    private JsonItemContent mItem;

    //private Button mButtonDownloadFree;
    private Button mButtonDownload;

    private TextView mTextViewProgressPercent;
    private TextView mTextViewProgressSize;
    private TextView mTextViewDescription;
    private MaterialButton mTextViewDescriptionMore;

    private AdMobNativeAdvanceUnified mAdMobNativeAdvance;
//    private AdMobVideoRewarded mAdMobVideoRewarded;
    private AdMobInterstitial mAdMobInterstitial;


    private LinearLayout mLayoutNative;
    private ConstraintLayout mConstraintLayoutProgress;
    private ContentLoadingProgressBar mLoadingProgressBar;
    private ImageButton mButtonCancel;
    private ImageViewPagerAdapter mImageViewPagerAdapter;
    private PageIndicatorView mPageIndicatorView;
    private ViewPager mViewPager;
    private BuildingInstaller mBuildingInstaller;
    private AdapterRecyclerView mAdapterOffers;
    private RecyclerViewManager mRecyclerViewOffers;

    private DownloadViewModel mDownloadViewModel;
    private FavoriteViewModel mFavoriteViewModel;
    private PurchaseViewModel mPurchaseViewModel;
    private AppViewModel mAppViewModel;
    private int countAd = 0;
    public BillingManager mBillingManager;

    private boolean exitPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        Serializable extraObject;
        if (getIntent() != null && (extraObject = getIntent().getSerializableExtra(JSON_OBJECT_KEY)) != null) {
            SerializableJSONObject serializableJSONObject = (SerializableJSONObject) extraObject;
            mItem = new JsonItemContent(serializableJSONObject.getJSONObject());
        } else {
            ToastUtil.show(this, R.string.error);
            finish();
            return;
        }

        mBillingManager = new BillingManager(this, PurchaseManager.PRODUCT_100_MONEY);
        mBillingManager.registerInterfacePurchase(this);
        mBillingManager.initBilling(API_KEY);

        countAd = PurchaseManager.getBoughtItem(this, mItem.mJSONObject);

        mInterfacePermission = this;

        mAdMobInterstitial = new AdMobInterstitial(this, Config.INTERSTITIAL_ID);

        mConstraintLayoutProgress = findViewById(R.id.constraintLayoutLoading);
        mLoadingProgressBar = findViewById(R.id.progressBarLoading);
        mTextViewProgressPercent = findViewById(R.id.textViewProgress);
        mTextViewProgressSize = findViewById(R.id.textViewDownloading);
        mButtonCancel = findViewById(R.id.imageButtonCancel);
//        mRecycleViewRelated = findViewById(R.id.recycleViewRelated);
        mRecyclerViewOffers = findViewById(R.id.recycleViewOffers);
        mPageIndicatorView = findViewById(R.id.pageIndicatorView);
        mLayoutNative = findViewById(R.id.layoutNativeAds);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewDescriptionMore = findViewById(R.id.textViewDescriptionMore);
//        mTextViewRelatedMore = findViewById(R.id.textViewRelatedMore);
//        mTextViewOfferMore = findViewById(R.id.textViewOfferMore);
        //mButtonDownloadFree = findViewById(R.id.buttonViewFree);
        mButtonDownload = findViewById(R.id.buttonDownload);
        mViewPager = findViewById(R.id.viewPager);

        mAdapterOffers = new AdapterRecyclerView(this, mRecyclerViewOffers, null, null);
        mRecyclerViewOffers.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.LinearLayoutHorizontal);
        mRecyclerViewOffers.setAdapter(mAdapterOffers);

        mAdMobVideoRewarded = new AdMobVideoRewarded(this);
        mAdMobVideoRewarded.setListener(this);
        mAdMobVideoRewarded.getRewardedVideoAd().setRewardedVideoAdListener(mAdMobVideoRewarded.getDefaultVideoRewardAdListener());
        mAdMobVideoRewarded.forceLoadRewardedVideo();

        //mButtonDownloadFree.setEnabled(false);

        //Необходимо задать пустой адаптер


        setViewModel();
        ToolbarUtil.setToolbar(this, true);
        ToolbarUtil.setCoinsSubtitle(this);
        setContentDescription();
        setTitle(mItem.getName());
        setImageView();
        setNativeAds();
        setButtonCancel();


    }

    @Override
    protected void onResume() {

        if (mBuildingInstaller != null)
            mBuildingInstaller.setBuildingListener(this);

        super.onResume();
    }

    @Override
    protected void onPause() {

        if (mBuildingInstaller != null)
            mBuildingInstaller.setBuildingListener(null);
        super.onPause();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Убираем группу меню для скинов
        menu.setGroupVisible(R.id.group_skins, false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        this.mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_menu, menu);

        mFavoriteViewModel.getFavoriteLiveData(mItem).observe(this, this::setFavorite);
        return true;
    }

    private void setFavorite(Boolean bool) {
        if (bool != null) {
            Drawable icon = ContextCompat.getDrawable(ActivityItem.this, bool ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false);
            mMenu.getItem(0).setIcon(icon);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_favorite:
                mFavoriteViewModel.setFavoriteLiveData(mItem);
                return true;
            case R.id.share:
                PermissionManager.onAskStoragePermission(this, DownloadViewModel.DOWNLOAD_IMAGE_SHARE);
                return true;
            case R.id.shop:
                startActivity(new Intent(this, ActivityShop.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mAdMobNativeAdvance != null && mLayoutNative != null) {
            mAdMobNativeAdvance.addNativeAdvanceView(mLayoutNative);
        }

        mAppViewModel.notifyViewModel();

    }

    /**
     * Пользователь одобрил разрешение или оно уже есть
     */
    @Override
    public void onPermissionSuccessResult(int requestCode) {
        Log.d(TAG, "onPermissionSuccessResult code" + requestCode);

        switch (requestCode) {
            case DownloadViewModel.DOWNLOAD_CONTENT:

                //Если Minecraft не установлен показать диалог и остановить выполнение
                if (!MinecraftHelper.isMinecraftInstalled(this)) {
                    ToastUtil.show(this, R.string.minecraft_not_installed);
                    return;
                }

                //Если Minecraft запущен показать диалог и остановить выполнение
                if (MinecraftHelper.isMinecraftRunning(this)) {
                    ToastUtil.show(this, R.string.close_minecraft);
                    return;
                }

                mViewPager.setVisibility(View.GONE);

                switch (mItem.getId()) {
                    case JsonItemFactory.SERVERS:
                        Log.d(TAG, "onPermissionSuccessResult");
                        if (MinecraftHelper.isSupportServersVersion(this)) {
                            Log.d(TAG, "onPermissionSuccessResult is Support Servers Version");
                            ServerHelper.openServer(this,
                                    mItem.getName(),
                                    mItem.getJsonObject().optString("address"),
                                    mItem.getJsonObject().optString("port"));
                        } else {
                            Log.d(TAG, "onPermissionSuccessResult is Not Support Servers Version");
                            ServerHelper.addServer(
                                    mItem.getName(),
                                    mItem.getJsonObject().optString("address"),
                                    mItem.getJsonObject().optString("port"),
                                    this);
                        }
                        break;
                    case JsonItemFactory.BUILDING:
                        mDownloadViewModel.onStartDownload(
                                StorageUtil.checkUrlPrefix(mItem.getFileLink()),
                                "/Download/",
                                "plan.schematic",
                                DownloadViewModel.DOWNLOAD_CONTENT);
                        break;
                    default:
                        mDownloadViewModel.onStartDownload(
                                StorageUtil.checkUrlPrefix(mItem.getFileLink()),
                                ContentHelper.getFileSavePathItem(mItem),
                                UrlHelper.getFileNameUrl(mItem.getFileLink()),
                                DownloadViewModel.DOWNLOAD_CONTENT);
                        break;
                }
                break;
            case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                mDownloadViewModel.onStartDownload(
                        StorageUtil.checkUrlPrefix(StringUtil.getFirstStringFromArray(mItem.getImageLink())),
                        "/Download/",
                        UrlHelper.getFileNameUrl(mItem.getImageLink()),
                        DownloadViewModel.DOWNLOAD_IMAGE_SHARE);
                break;
        }
    }

    /**
     * Метод обратного вызова результата установки файла.
     * На данный момент может возвращать результать после установки текстур или мода.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode) {
            case REQUEST_CODE_MOD:
            case REQUEST_CODE_TEXTURE:
                switch (resultCode) {
                    case RESULT_OK:
                        ToastUtil.show(this, R.string.file_success_import);
                        setButtonMainOpenBlockLauncher();
                        break;
                    default:
                        //RESULT_CANCELED
                        //RESULT_FIRST_USER
                        ToastUtil.show(this, R.string.file_error_import);
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (mBuildingInstaller != null) {
            mBuildingInstaller.cancel();
        }

        //TODO Проверить данное условие
        if (mDownloadViewModel.isRunning() && !exitPressed) {
            ToastUtil.show(this, R.string.press_again_to_cancell);
            exitPressed = true;
            new Handler().postDelayed(() -> exitPressed = false, 2000);
        } else {
            mDownloadViewModel.onCancelDownload();
            super.onBackPressed();
        }
    }

    /**
     * Получает события от DownloadViewModel.mDownloadEventLiveData
     *
     * @param event
     */
    public void onDownloadEvent(DownloadEvent event) {
        switch (event.type) {
            case DownloadEvent.PROGRESS:
                onDownloadUpdate(event);
                break;
            case DownloadEvent.FINISHED:
                onDownloadComplete(event);
                break;
        }
    }


    @Override
    public void onDownloadUpdate(DownloadEvent event) {
        Log.d(TAG, "onDownloadUpdate");

        Log.d(TAG, "Event requestCode "+event.requestCode);
        Log.d(TAG, "DOWNLOAD_IMAGE_SHARE requestCode "+DownloadViewModel.DOWNLOAD_IMAGE_SHARE);

        if (event.requestCode != DownloadViewModel.DOWNLOAD_IMAGE_SHARE) {

            if (event.progress == 0) {
                mTextViewProgressSize.setText(R.string.loading);
                mLoadingProgressBar.setIndeterminate(true);
                mTextViewProgressPercent.setText("");
            } else {
                mTextViewProgressSize.setText(getString(R.string.downloading_progress_format, event.loadedString, event.fullString));
                mLoadingProgressBar.setIndeterminate(false);
                mLoadingProgressBar.setProgress(event.progress);
                mTextViewProgressPercent.setText(event.percent);
            }

            if (mConstraintLayoutProgress.getVisibility() == View.GONE) {
                mConstraintLayoutProgress.setVisibility(View.VISIBLE);
            }

            if (mButtonDownload.isEnabled()) {
                mButtonDownload.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDownloadComplete(DownloadEvent event) {
        //Сделать mConstraintLayoutProgress неактивным
        mConstraintLayoutProgress.setVisibility(View.GONE);
        //Сделать mButtonDownloadFree активным
        //mButtonDownloadFree.setVisibility(View.GONE);
        mButtonDownload.setVisibility(View.VISIBLE);

        if (event.status == STATUS_SUCCESS) {

            //Если файл равен null (Правило не касается Серверов)
            if (event.file == null && !JsonItemFactory.SERVERS.equals(mItem.getId())) {
                ToastUtil.show(this, R.string.error);
                return;
            }

            switch (event.requestCode) {
                case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                    SocialManager.onShareImage(this, event.file, mItem.getName()
                            + "\nDownload it in the app "
                            + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                    break;
                case DownloadViewModel.DOWNLOAD_CONTENT:
                    switch (mItem.getId()) {
                        case JsonItemFactory.PACKS:
                            setButtonMainOpenMinecraft(MinecraftHelper.IMPORT, event.file);
//                            setButtonMainOpenMinecraftWithFile(event.file);
                            break;
                        case JsonItemFactory.MAPS:
                        case JsonItemFactory.SEEDS:
                            if (event.file.getName().endsWith(".mcworld")) {
                                setButtonMainOpenMinecraft(MinecraftHelper.IMPORT, event.file);
                            } else {
                                setButtonMainOpenMinecraft();
                            }
                            break;
                        case JsonItemFactory.MODS:
                        case JsonItemFactory.ADDONS:
                            if (event.file.getName().endsWith(".mcpack")) {
                                setButtonMainOpenMinecraft(MinecraftHelper.IMPORT, event.file);
//                                setButtonMainOpenMinecraftWithFile(event.file);
                            } else if (event.file.getName().endsWith(".mcaddon")) {
                                setButtonMainOpenMinecraft(MinecraftHelper.IMPORT, event.file);
//                                setButtonMainOpenMinecraftWithFile(event.file);
                            } else {
                                BlockLauncherHelper.importMod(this, event.file);
                            }
                            break;
                        case JsonItemFactory.TEXTURES:
                            if (event.file.getName().endsWith(".mcpack")) {
                                setButtonMainOpenMinecraft(MinecraftHelper.IMPORT, event.file);
//                                setButtonMainOpenMinecraftWithFile(event.file);
                            } else {
                                setButtonMainOpenMinecraft();
                                if (event.file.exists()) {
                                    event.file.delete();
                                }
                            }
                            break;
                        case JsonItemFactory.SERVERS:
                            if (MinecraftHelper.isSupportServersVersion(this)) {
                                setButtonMainOpenMinecraft();
                            } else {
                                setButtonMainOpenBlockLauncher();
                            }
                            break;
                        case JsonItemFactory.BUILDING:
                            //Файл plan.schematic загружен, осталось установить его в карту
                            mBuildingInstaller = new BuildingInstaller(this);
                            mBuildingInstaller.initList(this);
                            break;
                    }
                    mAdMobInterstitial.onShowAd();
                    SocialManager.onShowRateDialogOrToastFileSave(this);
                    break;
            }
        } else if (event.status > STATUS_CANCELED) {
            switch (event.status) {
                case STATUS_MEMORY_ERROR:
                    ToastUtil.show(this, R.string.file_storage_error);
                    break;
                case STATUS_NETWORK_ERROR:
                    ToastUtil.show(this, R.string.file_network_error);
                    break;
                default:
                    Log.d(TAG, "onDownloadComplete STATUS_CANCELED: ERROR");
                    ToastUtil.show(this, R.string.error);
            }
        }

    }

    private void setViewModel() {
        mFavoriteViewModel = ViewModelProviders.of(this, new FavoriteViewModel.FavoriteViewModelFactory(getApplication())).get(FavoriteViewModel.class);

        mPurchaseViewModel = ViewModelProviders.of(this, new PurchaseViewModel.PurchaseViewModelFactory(getApplication(), mItem)).get(PurchaseViewModel.class);
        mPurchaseViewModel.getPremiumLiveData().observe(this, this::setButtonDownload);

        mDownloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);
        mDownloadViewModel.getDownloadLiveData().observe(this, this::onDownloadEvent);


        mAppViewModel = AppViewModel.get(this);
        mAppViewModel.getItemLiveData().observe(this, this::setRecyclerViewAppItems);

    }

    private void setRecyclerViewAppItems(@Nullable List<JsonItemContent> items) {

        if (items != null && !items.isEmpty()) {
            List<JsonItemContent> itemList = new ArrayList<>(items);
            mAdapterOffers.setItemList(itemList);
            mRecyclerViewOffers.removeAllViews();
            mRecyclerViewOffers.setHasFixedSize(false);
            mRecyclerViewOffers.setNestedScrollingEnabled(false);
            mRecyclerViewOffers.setLayoutManager(RecyclerViewManager.LayoutManagerEnum.LinearLayoutHorizontal);
            mRecyclerViewOffers.setAdapter(mAdapterOffers);
            mRecyclerViewOffers.getAdapter().notifyDataSetChanged();
            for (int i = 0; i < mRecyclerViewOffers.getChildCount(); i++) {
                mRecyclerViewOffers.getChildAt(i).setScaleX(0.1F);
            }
        } else {
            mRecyclerViewOffers.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBuildingResult(int result) {
        switch (mItem.getId()) {
            case JsonItemFactory.BUILDING:
                //Пользователь загрузил plan.schematic (onDownloadComplete) и выбрал карту
                //Файл plan.schematic успешно установлен в карту
                mConstraintLayoutProgress.setVisibility(View.GONE);
                //mButtonDownloadFree.setVisibility(View.GONE);
                mButtonDownload.setVisibility(View.VISIBLE);

                if (result == BuildingInstaller.SUCCESS) {
                    setButtonMainOpenMinecraft();
                } else if (result == BuildingInstaller.ERROR) {
                    Log.d(TAG, "onBuildingResult: ERROR");
                    ToastUtil.show(this, R.string.error);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBuildingProgress(int progress) {
        if (mConstraintLayoutProgress.getVisibility() == View.GONE) {
            mConstraintLayoutProgress.setVisibility(View.VISIBLE);
            mButtonCancel.setOnClickListener((v) -> ToastUtil.show(this, R.string.press_again_to_cancell));

            mTextViewProgressSize.setText(R.string.installing);
            //mButtonDownloadFree.setVisibility(View.GONE);
            mButtonDownload.setVisibility(View.GONE);

            mButtonCancel.setOnClickListener((v) -> {
                if (mBuildingInstaller != null) mBuildingInstaller.cancel();
//                onBuildingResult(BuildingInstaller.CANCELED);
            });
        }
        mLoadingProgressBar.setProgress(progress);
        mTextViewProgressPercent.setText(progress + "%");
    }

    /**
     * Вызывается из BillingManager при успешной покупке продукта
     *
     * @param productId id купленного продукта.
     */
    @Override
    public void onProductPurchased(String productId) {
        //Если id продукта равен PRODUCT_100_COINS
        //Инициализировать покупку предмета
        //Скачивание предмета
        //Предложить купить больше монет
        ToolbarUtil.setCoinsSubtitle(this);
        switch (productId) {
            case PurchaseManager.PRODUCT_100_MONEY:
                mPurchaseViewModel.buyItem();
                onClickButtonMain();
                mBillingManager.getMoreCoinsDialog(PurchaseManager.getProduct(PurchaseManager.PRODUCT_500_MONEY, this));
                break;
        }
    }

    /**
     * Вызывается из BillingManager при неудачной покупке продукта
     *
     * @param errorCode код ошибки.
     */
    @Override
    public void onBillingError(int errorCode) {
        Log.d(TAG, "onBillingError");
        // Предлагаем посмотреть видеорекламу
//        runOnUiThread(() -> {
//            if (!isFinishing()) {
//                new AlertDialog.Builder(ActivityItem.this)
//                        .setTitle(R.string.free_coins)
//                        .setMessage(R.string.watch_and_gain)
//                        .setPositiveButton(R.string.watch, (dialog, which) -> mAdMobVideoRewarded.onShow())
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .create()
//                        .show();
//            }
//        });
    }


    private void setButtonCancel() {
        mButtonCancel.setOnClickListener((v) -> mDownloadViewModel.onCancelDownload());
    }

    private void setContentDescription() {

        mTextViewDescriptionMore.setOnClickListener(v -> {
            mTextViewDescription.setVisibility(View.VISIBLE);
            mTextViewDescriptionMore.setVisibility(View.GONE);
        });

        switch (mItem.getId()) {
            case JsonItemFactory.SERVERS:
                mTextViewDescription.setText(new StringBuilder()
                        .append(getString(R.string.server_address))
                        .append(" ")
                        .append(mItem.getJsonObject().optString("address"))
                        .append("\n\n")
                        .append(getString(R.string.server_port))
                        .append(" ")
                        .append(mItem.getJsonObject().optString("port"))
                        .append("\n\n")
                        .append(getString(R.string.version))
                        .append(" ")
                        .append("[")
                        .append(mItem.getVersion())
                        .append("]")
                        .append("\n\n")
                        .append(StringUtil.replaseJsonString(mItem.getDescription())));
                break;
            case JsonItemFactory.SEEDS:
                mTextViewDescription.setText(new StringBuilder()
                        .append(getString(R.string.description))
                        .append("\n\n")
                        .append("[")
                        .append(mItem.getVersion())
                        .append("]")
                        .append("\n\n")
                        .append(mItem.getJsonObject().optString("seed_id"))
                        .append("\n\n")
                        .append(StringUtil.replaseJsonString(mItem.getDescription())));
                break;
            default:
                String description = "";

                if (!mItem.getDescription().isEmpty()) {
                    description += StringUtil.replaseJsonString(mItem.getDescription());
                }

                if (!mItem.getVersion().isEmpty()) {
                    description += "\n\n" + "[" + mItem.getVersion() + "]";
                }

                if (!description.isEmpty()) {
//                    String descriptionTitle = getString(R.string.description).toUpperCase();
//                    description = descriptionTitle + description;
                    mTextViewDescription.setText(description);
                } else {
                    mTextViewDescription.setVisibility(View.GONE);
                    mTextViewDescriptionMore.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * Инициализация изображения Item через ImageView
     */
    public void setImageView() {
        String[] images = StringUtil.splitStringComma(mItem.getImageLink());
        mImageViewPagerAdapter = new ImageViewPagerAdapter(Arrays.asList(images));
        mImageViewPagerAdapter.setViewPager(mViewPager);

        mPageIndicatorView.setViewPager(mViewPager);
        mPageIndicatorView.setRadius(getResources().getDimension(R.dimen.page_indicator_radius));
        mPageIndicatorView.setAnimationType(AnimationType.SLIDE);
    }

    /**
     * Инициализация главной кнопки Button. Для избежания зависания главного потока
     * вызывается из ViewModel
     *
     * @param isPremium true если итем премиум и не куплен
     */
    public void setButtonDownload(boolean isPremium) {
//        ButtonColorManager.setBackgroundButton(mButtonDownload, ColorList.BLUE);
//        ButtonColorManager.setTextColorButton(mButtonDownload, ColorList.WHITE);

        if (isPremium) {
            String textPaid = getString(R.string.unlock) + " " + getString(R.string.coins_amount_format, mItem.getPrice());
            //mButtonDownloadFree.setText(textFree);
            mButtonDownload.setText(textPaid);
            mButtonDownload.setOnClickListener(view -> onClickBuy());
        } else {
            //Если контент не премиальный или уже куплен
            //Если контент это сервер и поддерживается его открытие через ссылку
            if (mItem.getId().equals(JsonItemFactory.SERVERS) && MinecraftHelper.isSupportServersVersion(this)) {
                mButtonDownload.setText(R.string.open);
            } else {
                mButtonDownload.setText(R.string.download);
            }
            //mButtonDownloadFree.setVisibility(View.GONE);
            mButtonDownload.setOnClickListener(v -> onClickButtonMain());
        }

    }

    private void onClickButtonMain() {
        PermissionManager.onAskStoragePermission(this, DownloadViewModel.DOWNLOAD_CONTENT);
    }

    /**
     * Если монет достаточно для покупки то покупаем итем и начинаем загрузку,
     * если нет, то инициализируем покупку
     */
    private void onClickBuy() {
        if (mPurchaseViewModel.requestBuy()) {
            mPurchaseViewModel.buyItem();
            ToolbarUtil.setCoinsSubtitle(this);
            onClickButtonMain();
        } else {
            getSupportFragmentManager().beginTransaction().add(new FragmentBuy(), "").commit();
//            mBillingManager.onPurchaseProduct(PurchaseManager.PRODUCT_100_MONEY);
//            runOnUiThread(() -> {
//                if (!isFinishing()) {
//                    new MaterialAlertDialogBuilder(ActivityItem.this)
//                            .setTitle(R.string.free_coins)
//                            .setMessage(R.string.watch_and_gain)
//                            .setPositiveButton(R.string.watch, (dialog, which) -> mAdMobVideoRewarded.onShow())
//                            .setNegativeButton(android.R.string.cancel, null)
//                            .create()
//                            .show();
//                }
//            });
        }

    }

    /**
     * Инициализцая NativeAdvanceView
     */
    public void setNativeAds() {
        if (Config.ADMIN_MODE) {
            return;
        }
        mAdMobNativeAdvance = new AdMobNativeAdvanceUnified(Config.NATIVE_ADVANCE_ID);
        mAdMobNativeAdvance.addNativeAdvanceView(mLayoutNative);
    }

    /**
     * Делает кнопку mButtonDownloadFree активной
     * Изменяет цвет и текст
     */
    private void setButtonMainEnable() {
        //mButtonDownloadFree.setVisibility(View.GONE);
        mButtonDownload.setVisibility(View.VISIBLE);
        mButtonDownload.setText(R.string.open);
        mButtonDownload.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        ButtonColorManager.setBackgroundButton(mButtonDownload, ColorList.RED_LIGHT);
    }

    /**
     * Делает кнопку mButtonDownloadFree активной
     * Изменяет цвет и текст
     * Изменяет слушатель (Открыть Minecraft PE)
     * Изменяет фокус на нативную рекламу
     */
    public void setButtonMainOpenMinecraft() {
        setButtonMainEnable();
        mButtonDownload.setOnClickListener(v -> MinecraftHelper.openMinecraft(this));
    }

    /**
     * Делает кнопку mButtonDownloadFree активной
     * Изменяет цвет и текст
     * Изменяет слушатель (Открыть Minecraft PE)
     * Изменяет фокус на нативную рекламу
     */
    public void setButtonMainOpenMinecraft(String param, File file) {
        setButtonMainEnable();
        mButtonDownload.setOnClickListener(v -> {
            try {
                MinecraftHelper.openContent(this, param, Uri.parse(file.toString()));
            } catch (ActivityNotFoundException e) {
                ToastUtil.show(this, R.string.error);
                e.printStackTrace();
            }
        });
    }


    /**
     * Делает кнопку mButtonDownloadFree активной
     * Изменяет цвет и текст
     * Изменяет слушатель (Открыть BlockLauncher)
     * Изменяет фокус на нативную рекламу
     */
    public void setButtonMainOpenBlockLauncher() {
        Log.d(TAG, "setButtonMainOpenBlockLauncher");
        setButtonMainEnable();
        mButtonDownload.setOnClickListener(v -> MinecraftHelper.openBlockLauncher(this));
    }

    /**
     * Делает кнопку mButtonDownloadFree активной
     * Изменяет цвет и текст
     * Изменяет слушатель (Открыть Minecraft PE с файлом)
     * Изменяет фокус на нативную рекламу
     *
     * @param file файл который необходимо установить
     */
    private void setButtonMainOpenMinecraftWithFile(final File file) {
        setButtonMainEnable();
        mButtonDownload.setOnClickListener(v -> AppUtil.onOpenFileWithApp(this, file, MinecraftHelper.MINECRAFT_PACKAGE_NAME));
    }

    @Override
    public void onRewarded(boolean b) {
//        if (b) {
//            PurchaseManager.addAdItem(this, mItem);
//            if (++countAd == mItem.getPrice())
//                mPurchaseViewModel.updatePremium();
//        }

        if (b) {
            PurchaseManager.addCoins(25, this);
            ToolbarUtil.setCoinsSubtitle(this);
            String message = getString(R.string.you_earned_coins, 25);
            ToastUtil.show(this, message);
        }

        if (mAdMobVideoRewarded != null)
            mAdMobVideoRewarded.forceLoadRewardedVideo();

    }


//    @Override
//    public void onAdsClosed(boolean rewarded) {
//        Log.d(TAG, "onAdsClosed: ");
//        //Вознаграждаем пользователя монеткой
//        if (rewarded) {
//            PurchaseManager.addCoins(50, this);
//            ToolbarUtil.setCoinsSubtitle(this);
//            String message = getString(R.string.you_earned_coins, 10);
//            ToastUtil.show(this, message);
//        }
//
//        if (mAdMobVideoRewarded != null)
//            mAdMobVideoRewarded.forceLoadRewardedVideo();
//
//    }

}
