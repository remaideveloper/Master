package com.alegangames.master.apps.skins;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.github.clans.fab.FloatingActionButton;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.ads.admob.AdMobVideoRewarded;
import com.alegangames.master.apps.skins.render.MinecraftSkinRenderer;
import com.alegangames.master.apps.skins.render.SkinGLSurfaceView;
import com.alegangames.master.apps.skins.render.SkinRender;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.events.DownloadEvent;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.BlockLauncherHelper;
import com.alegangames.master.util.ColorList;
import com.alegangames.master.util.MinecraftHelper;
import com.alegangames.master.util.SocialManager;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.billing.PurchaseManager;
import com.alegangames.master.util.files.FileUtil;
import com.alegangames.master.util.files.MemoryManager;
import com.alegangames.master.util.json.SerializableJSONObject;
import com.alegangames.master.util.permision.PermissionManager;
import com.alegangames.master.util.screen.ScreenSize;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.alegangames.master.activity.ActivityItem.JSON_OBJECT_KEY;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_SUCCESS;
import static com.alegangames.master.util.BlockLauncherHelper.REQUEST_CODE_SKIN;


public abstract class ActivitySkins extends ActivityAppParent implements PermissionManager.InterfacePermission, PurchaseManager.InterfacePurchase, AdMobVideoRewarded.RewardedVideoAdListener {


    public static final String TAG = ActivitySkins.class.getSimpleName();
    public static final int EDIT_SKIN = 1001;

    public SkinGLSurfaceView glSurfaceView;
    public MinecraftSkinRenderer mRenderer;
    public ProgressBar mProgressBar;
    public ProgressBar mDownloadBar;
    public ImageView mImageViewSkin;
    public JsonItemContent mItem;
    public FloatingActionButton saveButton;
    public boolean isFavorite = false;
    private AdMobVideoRewarded mAdMobVideoRewarded;

    public BitmapTarget target;

    private DownloadViewModel downloadViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterfacePermission = this;

        mAdMobVideoRewarded = new AdMobVideoRewarded(this);
        mAdMobVideoRewarded.forceLoadRewardedVideo();

        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);
        downloadViewModel.getDownloadLiveData().observe(this, this::onDownloadEvent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            glSurfaceView.onResume();
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }

        this.getSkinsFromSite(mItem.getFileLink());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            glSurfaceView.onPause();
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(JSON_OBJECT_KEY, new SerializableJSONObject(mItem.getJsonObject()));
    }

    public void onDownloadEvent(DownloadEvent event) {
        android.util.Log.d(TAG, "onDownloadEvent: ");
        if (event.type == DownloadEvent.FINISHED) {
            onDownloadComplete(event.file, event.status, event.requestCode);
        }
    }

    public void onDownloadComplete(File file, int status, int requestCode) {
        if (file != null) {
            Log.d(TAG, "onDownloadComplete: " + "File " + file.getPath() + "\n" + "Status " + status + "\n" + "RequestCode " + requestCode);
        }

        if (mDownloadBar != null) mDownloadBar.setVisibility(View.GONE);

        Log.d(TAG, "onDownloadComplete: status " + status + " equals sucess" + (status == STATUS_SUCCESS));
        if (status == STATUS_SUCCESS) {
            Log.d(TAG, "onDownloadComplete: success");
            switch (requestCode) {
//                case EDIT_SKIN:
//                    Intent intent = new Intent(this, ActivitySkinEditor.class);
//                    intent.setType("image/*");
//                    if (file != null) {
//                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
//                    }
//                    startActivity(intent);
//                    break;
                case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                    SocialManager.onShareImage(this, file, "Skin for Minecraft PE"
                            + "\nDownload it in the app "
                            + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                    break;
                case DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY:
                    SocialManager.onShowRateDialogOrToastFileSave(this);
                    break;
                case DownloadViewModel.DOWNLOAD_CONTENT:
                    if (MinecraftHelper.isSupportSkinsVersion(this)) {
                        //Minecraft PE Установлен и версия поддерживает автоматическую установку
                        saveButton.setOnClickListener(v -> MinecraftHelper.openMinecraft(ActivitySkins.this));
                        setFabMain(R.drawable.ic_fab_play, ColorList.RED, ColorList.RED_LIGHT, ColorList.RED_DARK);
                        ActivitySkins.changeOptionsFile();
                    } else {
                        //Minecraft PE Установлен и версия не поддерживает автоматическую установку
                        saveButton.setOnClickListener(v -> BlockLauncherHelper.importSkin(ActivitySkins.this, file));
                        setFabMain(R.drawable.ic_fab_play, ColorList.RED, ColorList.RED_LIGHT, ColorList.RED_DARK);
                    }
                    SocialManager.onShowRateDialogOrToastFileSave(this);
                    break;
            }
        }
    }

    public void initFAB() {
        saveButton = findViewById(R.id.fab_save);
        saveButton.setOnClickListener(v -> onAskPermissionSkin(this, DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY));
        setFabMain(R.drawable.ic_fab_save, ColorList.BLUE, ColorList.BLUE_LIGHT, ColorList.BLUE_DARK);
    }

    protected void setFabMain(@DrawableRes int id, int normal, int pressed, int ripple) {
        saveButton.setImageResource(id);
        saveButton.setColorNormal(normal);
        saveButton.setColorPressed(pressed);
        saveButton.setColorRipple(ripple);
    }

    private void onSaveToGallery() {
        Log.d(TAG, "onSaveToGallery");
        downloadViewModel.onStartDownload(
                mItem.getFileLink(),
                "/Download/Skins/",
                getSkinSaveName(),
                DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY);
        if (mDownloadBar != null) mDownloadBar.setVisibility(View.VISIBLE);
    }

    private void onSaveToMinecraft() {
        Log.d(TAG, "onSaveToMinecraft");
        if (!MinecraftHelper.isMinecraftInstalled(this)) {
            //Minecraft PE Не установлен
            ToastUtil.show(this, R.string.minecraft_not_installed);
            if (mDownloadBar != null) {
                mDownloadBar.setVisibility(View.GONE);
            }
            return;
        }

        //Delete preview custom.png
        File file = new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/custom.png");
        if (file.exists())
            file.delete();

        downloadViewModel.onStartDownload(
                mItem.getFileLink(),
                "/games/com.mojang/minecraftpe/",
                "custom.png",
                DownloadViewModel.DOWNLOAD_CONTENT);

        if (mDownloadBar != null) mDownloadBar.setVisibility(View.VISIBLE);
    }

    public void onEditSkin() {
        Log.d(TAG, "onEditSkin");
        if (mDownloadBar != null) mDownloadBar.setVisibility(View.VISIBLE);
        downloadViewModel.onStartDownload(
                mItem.getFileLink(),
                "/Download/Skins/",
                getSkinSaveName(),
                EDIT_SKIN);
    }

    public void onShareSkin() {
        Log.d(TAG, "onShareSkin");
        if (mDownloadBar != null) mDownloadBar.setVisibility(View.VISIBLE);
        downloadViewModel.onStartDownload(
                mItem.getImageLink(),
                "/Download/Skins/",
                getSkinSaveName(),
                DownloadViewModel.DOWNLOAD_IMAGE_SHARE);
    }


    private String getSkinSaveName() {
        if (mItem.getCategory() != null && !mItem.getCategory().isEmpty()) {
            return mItem.getCategory() + "_" + mItem.getName();
        } else {
            return mItem.getName();
        }
    }

    public void initGLSurfaceView() {
        //Skin 3D
        try {
            this.glSurfaceView = this.findViewById(R.id.skins);
            this.mRenderer = new MinecraftSkinRenderer(this, R.raw.nullchar, false);
            this.glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            this.glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            this.glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            this.glSurfaceView.setZOrderOnTop(false);
            this.glSurfaceView.setRenderer(this.mRenderer, this.getResources().getDisplayMetrics().density);
            this.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            this.mRenderer.mCharacter.SetRunning(true);
            this.mProgressBar = this.findViewById(R.id.progress);

            int height = (int) Math.round(ScreenSize.getHeightPX(this) * 0.52);
            int width = (int) Math.round(ScreenSize.getWidthPX(this) * 0.85);
            if (height > width) {
                width = height;
            }
            Log.d(TAG, "GLSurfaceView: Full height " + ScreenSize.getHeightPX(this));
            Log.d(TAG, "GLSurfaceView: Full width " + ScreenSize.getWidthPX(this));
            Log.d(TAG, "GLSurfaceView: height " + height);
            Log.d(TAG, "GLSurfaceView: width " + width);
            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(14, 14);
            layoutParams.setMargins(0, (int) Math.round(height * 0.15), 0, 0);
            final RelativeLayout relativeLayout = this.findViewById(R.id.lnrr);
            relativeLayout.setLayoutParams(layoutParams);

//            this.getSkinsFromSite(skinLinkFile);

            Log.d(TAG, "initGLSurfaceView: glSurfaceView");
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }

    }

    public void updateSurfaceView(){
        int height = (int) Math.round(ScreenSize.getHeightPX(this) * 0.52);
        int width = (int) Math.round(ScreenSize.getWidthPX(this) * 0.85);
        if (height > width) {
            width = height;
        }
        Log.d(TAG, "GLSurfaceView: Full height " + ScreenSize.getHeightPX(this));
        Log.d(TAG, "GLSurfaceView: Full width " + ScreenSize.getWidthPX(this));
        Log.d(TAG, "GLSurfaceView: height " + height);
        Log.d(TAG, "GLSurfaceView: width " + width);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.addRule(14, 14);
        layoutParams.setMargins(0, (int) Math.round(height * 0.15), 0, 0);
        final RelativeLayout relativeLayout = this.findViewById(R.id.lnrr);
        relativeLayout.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSkinBitmap(Bitmap bitmap) {
        Log.d(TAG, "setSkinBitmap");
        try {
            Log.d(TAG, "setSkinBitmap Try");
            if (SkinRender.isOldSkin(bitmap)) {
                Log.d(TAG, "setSkinBitmap: isOldSkin");
                bitmap = SkinRender.convertSkin(bitmap);
            }
            if (bitmap != null) {
                Log.d(TAG, "setSkinBitmap: Bitmap not Null");
                this.mRenderer.updateTexture(bitmap);
//                mImageViewSkin = findViewById(R.id.imageViewSkin);
//                mImageViewSkin.setImageBitmap(SkinRender.overlay(SkinRender.renderBodyFront(bitmap), SkinRender.renderBackFront(bitmap)));

            }
            this.mProgressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "setSkinBitmap Catch");
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    public void setSkinInv() {
        try {
            this.mRenderer.updateTexture(BitmapFactory.decodeStream(this.getResources().openRawResource(R.raw.nullchar)));
            this.mProgressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }
    }

    public void getSkinsFromSite(String url) {
        try {
            if (target == null) {
                target = new BitmapTarget(this);
            }
            Picasso.get().load(url).into(target);
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }
    }

    public void onAskPermissionSkin(Activity activity, final int requestCode) {

        //Если скин стоит монет и не куплен
        if (mItem.isPremium() && !PurchaseManager.isItemBought(mItem.getJsonObject(), this)) {
            if (PurchaseManager.getCoins(this) > mItem.getPrice()) {
                PurchaseManager.onItemsBought(mItem.getJsonObject(), this);
                PermissionManager.onAskStoragePermission(activity, requestCode);
            } else {
              onBillingError(0);
            }
        } else {
            PermissionManager.onAskStoragePermission(activity, requestCode);
        }
    }

    @Override
    public void onProductPurchased(String productId) {
        PurchaseManager.onItemsBought(mItem.getJsonObject(), this);
        onAskPermissionSkin(this, DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY);
    }

    @Override
    public void onBillingError(int errorCode) {
        Log.d(TAG, "onBillingError");
        // Предлагаем посмотреть видеорекламу
        runOnUiThread(() -> {
            if (mAdMobVideoRewarded.isLoaded() && !isFinishing()) {
                new AlertDialog.Builder(ActivitySkins.this)
                        .setTitle(R.string.free_coins)
                        .setMessage(R.string.watch_and_gain)
                        .setPositiveButton(R.string.watch, (dialog, which) -> mAdMobVideoRewarded.onShow())
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
            }
        });
    }

    @Override
    public void onPermissionSuccessResult(int requestCode) {
        switch (requestCode) {
            case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                onShareSkin();
                break;
            case DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY:
            case DownloadViewModel.DOWNLOAD_CONTENT:
                initDialogList();
                break;
        }
    }

    public void initDialogList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        };

        adapter.add(getString(R.string.save_to_gallery));
        adapter.add(getString(R.string.save_to_minecraft));


        new AlertDialog.Builder(this)
                .setAdapter(adapter, (dialog, which) -> {
                    switch (which) {
                        case 0: //Save to Gallery
                            onSaveToGallery();
                            break;
                        case 1: //Save to Minecraft
                            onSaveToMinecraft();
                            break;
                    }
                })
                .create()
                .show();
    }

    /*public void initDialogSave(int requestCode) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        };

        adapter.add(getString(R.string.free) + " (" + getString(R.string.watch_video) + ")");
        adapter.add(getString(R.string.paid) + " (" + getString(R.string.buy) + " " + getString(R.string.coins_amount_format, 10) + ")");


        new AlertDialog.Builder(this)
                .setAdapter(adapter, (dialog, which) -> {
                    switch (which) {
                        case 0: //Бесплатное скачивание
                            if (mAdMobVideoRewarded.isLoaded()) {
                                mAdMobVideoRewarded.onShow();
                                mAdMobVideoRewarded.setRewardedVideoAdListener(new AdMobVideoRewarded.RewardedVideoAdListener() {
                                    @Override
                                    public void onAdsClosed(boolean rewarded) {
                                        if(rewarded) {
                                            PermissionManager.onAskStoragePermission(ActivitySkins.this, requestCode);
                                        }
                                    }

                                    @Override
                                    public void onAdsLoaded() {
                                        mAdMobVideoRewarded.forceLoadRewardedVideo();
                                    }
                                });
                            }
                            break;
                        case 1: //Платное скачивание
                            //Если монет больше чем стоимость скина, купить скин
                            if (PurchaseManager.getCoins(this) > mItem.getPrice()) {
                                PurchaseManager.onItemsBought(mItem.getJsonObject(), this);
                                PermissionManager.onAskStoragePermission(this, requestCode);
                            } else {
                                //Иначе инициализировать покупку монет
                                billingManager.onPurchaseProduct(PurchaseManager.getDefaultProductId());
                            }
                            break;
                    }
                })
                .create()
                .show();
    }*/

    /**
     * Метод обратного вызова результата установки файла.
     * На данный момент может возвращать результать после установки скина.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult:" + " requestCode " + requestCode + " resultCode " + resultCode);

        switch (requestCode) {
            case REQUEST_CODE_SKIN:
                switch (resultCode) {
                    case RESULT_OK: //-1
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.file_saved)
                                .setMessage(R.string.skin_dialog_description_success)
                                .setPositiveButton(android.R.string.ok, (dialog, i) ->
                                        MinecraftHelper.openBlockLauncher(ActivitySkins.this))
                                .create()
                                .show();
                    default:
                        //RESULT_CANCELED 0
                        //RESULT_FIRST_USER 1
                        ToastUtil.show(this, R.string.file_error_import);
                        break;
                }
                break;
        }
    }

    //Обрабатывает загрузку изображения
    public static class BitmapTarget implements Target {

        private WeakReference<ActivitySkins> mActivityReference;

        public BitmapTarget(ActivitySkins activitySkins) {
            mActivityReference = new WeakReference<>(activitySkins);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                if (mActivityReference.get() != null) {
                    mActivityReference.get().setSkinBitmap(bitmap);
                }
            } catch (Exception ex) {
//                Crashlytics.logException(ex);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            if (mActivityReference.get() != null) {
                Log.d(TAG, "onBitmapFailed: ERROR");
                ToastUtil.show(mActivityReference.get(), R.string.error);
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (mActivityReference.get() != null) {
                mActivityReference.get().setSkinInv();
            }
        }
    }

    public static void changeOptionsFile() {
        List<String> stringList = new ArrayList<>();
        File file;
        File sdPath;

        // проверяем доступность SD
        if (!MemoryManager.externalMemoryAvailable()) return;

        try {
            sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/games/com.mojang/minecraftpe/");

            //Если директория не существует, создаем ее
            if (!sdPath.exists()) {
                sdPath.mkdirs();
            }

            //Формируем объект File, который содержит путь к файлу
            file = new File(sdPath, "options.txt");

            //Если файл не существет, создать новый файл, и записать в него строку
            if (!file.exists()) {
                Log.d(TAG, "doInBackground: File Servers not exists");
                FileUtil.createFileWithString(file, "game_skintypefull:Standard_Custom");
                return;
            }


            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(TAG, "readStringsFile: " + str);
                if (str.contains("game_skintypefull")) {
                    str = str.replace(str, "game_skintypefull:Standard_Custom");
                }
                stringList.add(str);
            }

            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            // пишем данные
            for (String s : stringList) {
                bw.write(s); //записываем в файл строку
                bw.write("\n"); // переходим на след строку
                Log.d(TAG, "writeStringsFile: " + s);
            }

            // закрываем потоки
            br.close();
            bw.flush();
            bw.close();
        } catch (IOException e) {
//            Crashlytics.logException(e);
        }
    }

    @Override
    public void onAdsClosed(boolean rewarded) {
        Log.d(TAG, "onAdsClosed: ");
        //Вознаграждаем пользователя монеткой
        if (rewarded) {
            PurchaseManager.addCoins(10, this);
            ToolbarUtil.setCoinsSubtitle(this);
            String message = getString(R.string.you_earned_coins, 10);
            ToastUtil.show(this, message);
        }

        if (mAdMobVideoRewarded != null)
            mAdMobVideoRewarded.forceLoadRewardedVideo();
    }

    @Override
    public void onAdsLoaded() {

    }


}
