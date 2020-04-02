package com.alegangames.master.holder;


import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.Config;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.architecture.viewmodel.FavoriteViewModel;
import com.alegangames.master.events.DownloadEvent;
import com.alegangames.master.interfaces.InterfaceDownload;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.util.BlockLauncherHelper;
import com.alegangames.master.util.ContentHelper;
import com.alegangames.master.util.MinecraftHelper;
import com.alegangames.master.util.ServerHelper;
import com.alegangames.master.util.SocialManager;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.UrlHelper;
import com.alegangames.master.util.billing.PurchaseManager;
import com.alegangames.master.util.permision.PermissionManager;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.alegangames.master.download.DownloadAsyncTask.STATUS_CANCELED;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_MEMORY_ERROR;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_NETWORK_ERROR;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_SUCCESS;

public class ItemContentViewHolder extends RecyclerView.ViewHolder implements PermissionManager.InterfacePermission, InterfaceDownload {

    public static final String TAG = "ViewHolders";

    private String mQuery;
    private TextView mTitle;
    private ImageView mImageView;
    private TextView mTextViewPremium;
    private LinearLayout container;
    private LinearLayout layoutShow;
    private JsonItemContent mItem;
    private DownloadViewModel mDownloadViewModel;
    private FavoriteViewModel mFavoriteViewModel;
    private FloatingActionButton floatingActionButton;
    private ContentLoadingProgressBar progressBar;
    private AdMobVideoRewarded mAdMobVideoRewarded;
    private TextView textCountAd;
    private int countShowAd = 0;
    private boolean isShow = false;

    private LiveData<Boolean> favoriteLiveData;
    private LiveData<DownloadEvent> downloadLiveData;
    private MutableLiveData<Integer> priceLiveData;

    public ItemContentViewHolder(View view) {
        super(view);
        mTitle = view.findViewById(R.id.textViewTitle);
        mImageView = view.findViewById(R.id.imageViewItem);
        mTextViewPremium = view.findViewById(R.id.textViewPremium);
        container = view.findViewById(R.id.container);
        layoutShow = view.findViewById(R.id.layoutShow);
    }


    public void setHolder(final JsonItemContent item, String query) {
        this.mQuery = query;

        //Title
        if (item.getName() != null && !item.getName().isEmpty() && mTitle != null) {
            if (mQuery != null && !mQuery.isEmpty()) {
                mTitle.setText(StringUtil.highLightSearchText(mQuery, item.getName()));
            } else {
                mTitle.setText(item.getName());
            }
        }

        View.OnClickListener onClickListener = v -> {
            ((ActivityAppParent) itemView.getContext()).onOpenItem(item);
            //FirebaseAnalyticsHelper.sendEvent(itemView.getContext(), "item_content_view_click", "content", item.getName());
        };

        //ImageView
        if (mImageView != null) {
//            if (item.getId().equals(JsonItemFactory.SKIN_CUSTOM))
                mImageView.setOnClickListener(onClickListener);
            Picasso
                    .get()
                    .load(StorageUtil.checkUrlPrefix(StringUtil.getFirstStringFromArray(item.getImageLink())))
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.empty_image)
                    .into(mImageView);
        }

        //Premium
        if (mTextViewPremium != null) {
            if (item.getPrice() > 25) {
                String text = itemView.getContext().getString(R.string.coins_amount_format, item.getPrice());
                mTextViewPremium.setText(text);
                mTextViewPremium.setVisibility(View.GONE);
//                mTitle.setTextColor(itemView.getContext().getResources().getColor(R.color.carbon_white));
//                mTextViewPremium.setTextColor(itemView.getContext().getResources().getColor(R.color.carbon_white));
                itemView.findViewById(R.id.privateImage).setVisibility(View.VISIBLE);
            } else {
                mTextViewPremium.setVisibility(View.GONE);
                itemView.findViewById(R.id.privateImage).setVisibility(View.GONE);
//                mTitle.setTextColor(Color.parseColor("#d6bf96"));
//                mTextViewPremium.setTextColor(Color.parseColor("#d6bf96"));
            }
        }
    }

    private void setContentDescription(TextView mTextViewDescription) {

        switch (mItem.getId()) {
            case JsonItemFactory.SEEDS:
                mTextViewDescription.setText(new StringBuilder()
                        .append(mTextViewDescription.getContext().getString(R.string.description))
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
                }
                break;
        }
    }

    public void show(JsonItemContent itemContent, FavoriteViewModel favoriteViewModel, DownloadViewModel downloadViewModel, AdMobVideoRewarded adMobVideoRewarded) {
        if (isShow)
            return;
        isShow = true;
        mAdMobVideoRewarded = adMobVideoRewarded;
        mFavoriteViewModel = favoriteViewModel;
        mDownloadViewModel = downloadViewModel;
        mItem = itemContent;

        if (layoutShow != null)
            layoutShow.setVisibility(View.VISIBLE);
        if (container == null)
            return;
        container.addView(LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_show_card, null));
        itemView.findViewById(R.id.textViewDescription).setVisibility(View.VISIBLE);
        TextView textDescription = itemView.findViewById(R.id.textViewDescription);
        setContentDescription(textDescription);
//        textDescription.setText(itemContent.getDescription());
        floatingActionButton = itemView.findViewById(R.id.btn);
        progressBar = itemView.findViewById(R.id.progressBar);

        floatingActionButton.setOnClickListener(v -> {
            PermissionManager.onAskStoragePermission(((FragmentActivity) itemView.getContext()), DownloadViewModel.DOWNLOAD_CONTENT, this);
        });

        FloatingActionButton btnFavorite = itemView.findViewById(R.id.btnFavorite);
        FloatingActionButton btnShare = itemView.findViewById(R.id.btnShare);

        favoriteLiveData = mFavoriteViewModel.getFavoriteLiveData(itemContent);
        favoriteLiveData.observe(((FragmentActivity) itemView.getContext()), aBoolean->{
            if (aBoolean != null) {
                Drawable icon = ContextCompat.getDrawable(itemView.getContext(), aBoolean ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false);
                if (btnFavorite!=null)
                    btnFavorite.setImageDrawable(icon);
            }
        });


        btnFavorite.setOnClickListener(v->{
            if (mFavoriteViewModel!=null)
                mFavoriteViewModel.setFavoriteLiveData(itemContent);
        });

        btnShare.setOnClickListener(v -> {
            PermissionManager.onAskStoragePermission(((FragmentActivity) itemView.getContext()), DownloadViewModel.DOWNLOAD_IMAGE_SHARE, this);
        });

        textCountAd = itemView.findViewById(R.id.textCountAd);

        if (itemContent.isPremium() && !Config.NO_ADS) {
//            textDescription.setTextColor(itemView.getContext().getResources().getColor(R.color.carbon_white));
            floatingActionButton.setImageResource(R.drawable.ic_play);
//            floatingActionButton.setColorFilter(itemView.getContext().getResources().getColor(R.color.carbon_white));
//            btnFavorite.setColorFilter(itemView.getContext().getResources().getColor(R.color.carbon_white));
//            btnShare.setColorFilter(itemView.getContext().getResources().getColor(R.color.carbon_white));


            countShowAd = PurchaseManager.getBoughtItem(itemView.getContext(), mItem.mJSONObject);

            if (countShowAd == mItem.getPrice()){
                textCountAd.setVisibility(View.GONE);
                itemView.findViewById(R.id.imageView).setVisibility(View.GONE);
            } else {
                priceLiveData = new MutableLiveData<>();
                textCountAd.setText((countShowAd + "/" + mItem.getPrice()));
                priceLiveData.observe(((FragmentActivity) itemView.getContext()), count -> {
                    countShowAd = count;
                    textCountAd.setText((countShowAd + "/" + mItem.getPrice()));
                    if (count == mItem.getPrice()) {
                        floatingActionButton.setImageResource(R.drawable.ic_fab_save);
                        floatingActionButton.setOnClickListener(v1 -> {
                            PermissionManager.onAskStoragePermission(((FragmentActivity) itemView.getContext()), DownloadViewModel.DOWNLOAD_CONTENT, ItemContentViewHolder.this);
                        });
                        textCountAd.setVisibility(View.GONE);
                        itemView.findViewById(R.id.imageView).setVisibility(View.GONE);
                    }
                });

                floatingActionButton.setOnClickListener(v -> {
                    mAdMobVideoRewarded.getRewardedVideoAd().setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdClosed() {
                            Log.d(TAG, "onRewardedVideoAdClosed");
                            mAdMobVideoRewarded.forceLoadRewardedVideo();
//                        onReward();
                        }

                        @Override
                        public void onRewarded(RewardItem rewardItem) {
                            Log.d(TAG, "onRewarded: " + "currency: " + rewardItem.getType() + " amount: " + rewardItem.getAmount());
//                        setRewarded(true);

                            if (mItem != null) {
                                PurchaseManager.addAdItem(itemView.getContext(), mItem);
                                priceLiveData.postValue(++countShowAd);
                            }
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int errorCode) {
                            Log.d(TAG, "onRewardedVideoAdFailedToLoad code " + errorCode);
                        }

                        @Override
                        public void onRewardedVideoAdLoaded() {
                            Log.d(TAG, "onRewardedVideoAdLoaded");
                        }

                        @Override
                        public void onRewardedVideoAdOpened() {
                            Log.d(TAG, "onRewardedVideoAdOpened");
                        }

                        @Override
                        public void onRewardedVideoStarted() {
                            Log.d(TAG, "onRewardedVideoStarted");
                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {
                            Log.d(TAG, "onRewardedVideoAdLeftApplication");
                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                            Log.d(TAG, "onRewardedVideoCompleted");
                        }
                    });
                    mAdMobVideoRewarded.onShow();

                });

            }
        } else {
            textCountAd.setVisibility(View.GONE);
            itemView.findViewById(R.id.imageView).setVisibility(View.GONE);
        }

//            itemView.findViewById(R.id.textViewDescription).setVisibility(View.GONE);
//            ((TextView) itemView.findViewById(R.id.textViewDescription)).setText("");


    }

    public void hide() {
        if (!isShow)
            return;
        isShow = false;
        if (mItem != null)
            mItem = null;
        if (layoutShow != null)
            layoutShow.setVisibility(View.GONE);
        if (favoriteLiveData!=null) {
            favoriteLiveData.removeObservers(((FragmentActivity) itemView.getContext()));
            favoriteLiveData = null;
        }
        if (downloadLiveData!=null) {
            downloadLiveData.removeObservers(((FragmentActivity) itemView.getContext()));
            downloadLiveData = null;
        }

        if (priceLiveData!=null){
            priceLiveData.removeObservers(((FragmentActivity) itemView.getContext()));
            priceLiveData = null;
        }

        textCountAd = null;

        mDownloadViewModel = null;
        mFavoriteViewModel = null;
        if (container == null)
            return;
        container.removeAllViewsInLayout();
    }

    @Override
    public void onPermissionSuccessResult(int requestCode) {

        if(mItem == null || mDownloadViewModel == null || mFavoriteViewModel == null)
            return;

        switch (requestCode) {
            case DownloadViewModel.DOWNLOAD_CONTENT:

                //Если Minecraft не установлен показать диалог и остановить выполнение
                if (!MinecraftHelper.isMinecraftInstalled(itemView.getContext())) {
                    ToastUtil.show(itemView.getContext(), R.string.minecraft_not_installed);
                    return;
                }

                //Если Minecraft запущен показать диалог и остановить выполнение
                if (MinecraftHelper.isMinecraftRunning(itemView.getContext())) {
                    ToastUtil.show(itemView.getContext(), R.string.close_minecraft);
                    return;
                }

                switch (mItem.getId()) {
                    case JsonItemFactory.SERVERS:
                        Log.d(TAG, "onPermissionSuccessResult");
                        if (MinecraftHelper.isSupportServersVersion(itemView.getContext())) {
                            Log.d(TAG, "onPermissionSuccessResult is Support Servers Version");
                            ServerHelper.openServer(itemView.getContext(),
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
                    default:
                        downloadLiveData = mDownloadViewModel.onStartDownload1(
                                StorageUtil.checkUrlPrefix(mItem.getFileLink()),
                                ContentHelper.getFileSavePathItem(mItem),
                                UrlHelper.getFileNameUrl(mItem.getFileLink()),
                                DownloadViewModel.DOWNLOAD_CONTENT);
                        downloadLiveData.observe(((FragmentActivity) itemView.getContext()), this::onDownloadEvent);
                        break;
                }
                break;
            case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                downloadLiveData = mDownloadViewModel.onStartDownload1(
                        StorageUtil.checkUrlPrefix(StringUtil.getFirstStringFromArray(mItem.getImageLink())),
                        "/Download/",
                        UrlHelper.getFileNameUrl(mItem.getImageLink()),
                        DownloadViewModel.DOWNLOAD_IMAGE_SHARE);
                downloadLiveData.observe(((FragmentActivity) itemView.getContext()), this::onDownloadEvent);
                break;
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
            case DownloadEvent.CANCELED:
                floatingActionButton.setOnClickListener(v -> {
                    PermissionManager.onAskStoragePermission(((FragmentActivity) itemView.getContext()), DownloadViewModel.DOWNLOAD_CONTENT, this);
                });
        }
    }

    @Override
    public void onDownloadUpdate(DownloadEvent event) {
        Log.d(TAG, "onDownloadUpdate");

        Log.d(TAG, "Event requestCode "+event.requestCode);
        Log.d(TAG, "DOWNLOAD_IMAGE_SHARE requestCode "+DownloadViewModel.DOWNLOAD_IMAGE_SHARE);

        if (event.requestCode != DownloadViewModel.DOWNLOAD_IMAGE_SHARE) {

            if (event.progress == 0) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(event.progress);
            }

            if (progressBar.getVisibility() == View.GONE) {
                progressBar.setVisibility(View.VISIBLE);
            }

            floatingActionButton.setImageResource(R.drawable.ic_close);
            floatingActionButton.setOnClickListener(v -> {
                mDownloadViewModel.onCancelDownload();
                floatingActionButton.setImageResource(R.drawable.ic_fab_save);
                floatingActionButton.setOnClickListener(v1->{
                    PermissionManager.onAskStoragePermission(((FragmentActivity) itemView.getContext()), DownloadViewModel.DOWNLOAD_CONTENT, ItemContentViewHolder.this);
                });
            });
        }
    }

    @Override
    public void onDownloadComplete(DownloadEvent event) {

        progressBar.setVisibility(View.GONE);

        if (event.status == STATUS_SUCCESS) {

            //Если файл равен null (Правило не касается Серверов)
            if (event.file == null && !JsonItemFactory.SERVERS.equals(mItem.getId())) {
                ToastUtil.show(itemView.getContext(), R.string.error);
                return;
            }

            switch (event.requestCode) {
                case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                    SocialManager.onShareImage(itemView.getContext(), event.file, mItem.getName()
                            + "\nDownload it in the app "
                            + "\nhttps://play.google.com/store/apps/details?id=" + itemView.getContext().getPackageName());
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
                                BlockLauncherHelper.importMod(itemView.getContext(), event.file);
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
                            if (MinecraftHelper.isSupportServersVersion(itemView.getContext())) {
                                setButtonMainOpenMinecraft();
                            } else {
                                setButtonMainOpenBlockLauncher();
                            }
                            break;
                    }
//                    mAdMobInterstitial.onShowAd();
                    SocialManager.onShowRateDialogOrToastFileSave(((FragmentActivity) itemView.getContext()));
                    break;
            }
        } else if (event.status > STATUS_CANCELED) {
            switch (event.status) {
                case STATUS_MEMORY_ERROR:
                    ToastUtil.show(itemView.getContext(), R.string.file_storage_error);
                    break;
                case STATUS_NETWORK_ERROR:
                    ToastUtil.show(itemView.getContext(), R.string.file_network_error);
                    break;
                default:
                    Log.d(TAG, "onDownloadComplete STATUS_CANCELED: ERROR");
                    ToastUtil.show(itemView.getContext(), R.string.error);
            }
        }

    }

    public void setButtonMainOpenMinecraft() {
        setButtonMainEnable();
        floatingActionButton.setOnClickListener(v -> MinecraftHelper.openMinecraft(itemView.getContext()));
    }

    public void setButtonMainOpenMinecraft(String param, File file) {
        setButtonMainEnable();
        floatingActionButton.setOnClickListener(v -> {
            try {
                MinecraftHelper.openContent(itemView.getContext(), param, Uri.parse(file.toString()));
            } catch (ActivityNotFoundException e) {
                ToastUtil.show(itemView.getContext(), R.string.error);
                e.printStackTrace();
            }
        });
    }

    private void setButtonMainEnable() {
        //mButtonDownloadFree.setVisibility(View.GONE);
        floatingActionButton.setImageResource(R.drawable.ic_play);
    }

    public void setButtonMainOpenBlockLauncher() {
        Log.d(TAG, "setButtonMainOpenBlockLauncher");
        setButtonMainEnable();
        floatingActionButton.setOnClickListener(v -> MinecraftHelper.openBlockLauncher(itemView.getContext()));
    }
}