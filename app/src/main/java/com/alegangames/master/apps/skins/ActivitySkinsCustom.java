package com.alegangames.master.apps.skins;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.clans.fab.FloatingActionButton;
import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.FavoriteManager;
import com.alegangames.master.util.SkinUtil;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.StringUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.json.SerializableJSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import static com.alegangames.master.activity.ActivityItem.JSON_OBJECT_KEY;

public class ActivitySkinsCustom extends ActivitySkins {

    public static final String TAG = "ActivitySkinsCustom";
    private static final int LAYOUT = R.layout.activity_skins;
    private Menu menu;
    private TextView mTextViewSkin;
    public AdMobBanner mAdMobBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        mAdMobBanner = new AdMobBanner(this);
        mAdMobBanner.onCreate();

        SerializableJSONObject serializableJSONObject;

        if (savedInstanceState != null && savedInstanceState.getSerializable(JSON_OBJECT_KEY) != null) {
            serializableJSONObject = (SerializableJSONObject) savedInstanceState.getSerializable(JSON_OBJECT_KEY);
        } else {
            serializableJSONObject = (SerializableJSONObject) getIntent().getSerializableExtra(JSON_OBJECT_KEY);
        }

        if (serializableJSONObject != null) {

            //Пересоздаем итем
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", JsonItemFactory.SKIN_CUSTOM);
                jsonObject.put("name", serializableJSONObject.getJSONObject().optString("name"));
                jsonObject.put("image_link", StorageUtil.checkUrlPrefix(serializableJSONObject.getJSONObject().optString("image_link")));
                jsonObject.put("file_link", StorageUtil.checkUrlPrefix(serializableJSONObject.getJSONObject().optString("file_link")));
                jsonObject.put("category", serializableJSONObject.getJSONObject().optString("category"));
                jsonObject.put("count", serializableJSONObject.getJSONObject().optInt("count"));
                jsonObject.put("price", serializableJSONObject.getJSONObject().optInt("price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mItem = new JsonItemContent(jsonObject);
        }


        mDownloadBar = findViewById(R.id.progressBar);
        mTextViewSkin = findViewById(R.id.textSkin);


        initFAB();
        ToolbarUtil.setToolbar(this, true);
        ToolbarUtil.setCoinsSubtitle(this);

        initGLSurfaceView();
        getSkinsFromSite(mItem.getFileLink());

        setTextSkin(StringUtil.getIntFromString(mItem.getFileLink()), mItem.getCount());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_menu, menu);

        setFavoriteItem(menu.getItem(0));

        return true;
    }

    /**
     * Асинхронно проверяет добален ли элемен в избранные и меняет иконку
     *
     * @param item лайк итем
     */
    private void setFavoriteItem(MenuItem item) {
        AsyncTask.execute(() -> {
            isFavorite = FavoriteManager.getFavoriteOnPreference(this).toString().contains(mItem.getJsonObject().toString());
            Drawable icon = ContextCompat.getDrawable(ActivitySkinsCustom.this, isFavorite ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false);
            runOnUiThread(() -> item.setIcon(icon));
        });
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_skins, true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_favorite:
                onClickMenuItemFavorite();
                return true;
            case R.id.share:
                onAskPermissionSkin(this, DownloadViewModel.DOWNLOAD_IMAGE_SHARE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickMenuItemFavorite() {
        Log.d(TAG, "onClickMenuItemFavorite: isFavorite " + isFavorite);
        if (isFavorite) {
            //Удалить из избранного
            ToastUtil.show(this, R.string.removed_from_favorite);
            FavoriteManager.deleteFavoriteToPreference(this, mItem);
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_false));
            isFavorite = false;
        } else {
            //Добавить в избранное
            ToastUtil.show(this, R.string.added_to_favorite);
            FavoriteManager.addFavoriteToPreference(this, mItem);
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_true));
            isFavorite = true;
        }
    }

    @Override
    public void initFAB() {
        super.initFAB();
        FloatingActionButton backButton = findViewById(R.id.fab_back);
        backButton.setOnClickListener(v ->
                loadNextLinkFile(false));

        FloatingActionButton nextButton = findViewById(R.id.fab_next);
        nextButton.setOnClickListener(v ->
                loadNextLinkFile(true));
    }

    /**
     * Загружает следующий/предыдущий скин из категории
     *
     * @param increment Если значение True загружается следующий скин
     *                  Если значение False загружается предыдущий скин
     */
    private void loadNextLinkFile(boolean increment) {

        //Example "https://storage.googleapis.com/json-data-base.appspot.com/apps/modsforminecraft/skins/boy/file/"
        String path = mItem.getFileLink().substring(0, mItem.getFileLink().lastIndexOf('/') + 1);
        //Example "skin100.png"
        String name = mItem.getFileLink().substring(mItem.getFileLink().lastIndexOf('/') + 1);
        //Example ".png"
        String ext = name.substring(name.lastIndexOf('.'));
        //Example "100"
        int number = Integer.parseInt(name.replaceAll("\\D+", ""));
        //Example "skin"
        String prext = name.substring(0, name.lastIndexOf(String.valueOf(number)));

        //То же самое для превью
        String previewPath = mItem.getImageLink().substring(0, mItem.getImageLink().lastIndexOf('/') + 1);
        String preview = mItem.getImageLink().substring(mItem.getImageLink().lastIndexOf('/') + 1);
        String previewExt = preview.substring(preview.lastIndexOf('.'));
        String previewPrext = preview.substring(0, preview.lastIndexOf(String.valueOf(number)));

        if (increment) {
            number++;
            //Если скин skin501.png сделать его skin1.png
            if (number > mItem.getCount()) {
                number = 1;
            }
        } else {
            number--;
            //Если скин skin0.png сделать его skin500.png
            if (number < 1) {
                number = mItem.getCount();
            }
        }

        //Example "https://storage.googleapis.com/json-data-base.appspot.com/apps/modsforminecraft/skins/boy/file/skin101.png"
        String skinLinkFile = path + prext + String.valueOf(number) + ext;
        String previewLinkFile = previewPath + previewPrext + String.valueOf(number) + previewExt;

        // почему json не хранит категорию?
//        String[] splitLink = skinLinkFile.split("/");
//        String category = splitLink[splitLink.length - 3];
//        int price = SkinUtil.getSkinPrice(category, number);
        int price = SkinUtil.getSkinPrice(mItem.getCategory(), number);

        //Пересоздаем итем
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", JsonItemFactory.SKIN_CUSTOM);
            jsonObject.put("name", prext + String.valueOf(number) + ext);
            jsonObject.put("image_link", previewLinkFile);
            jsonObject.put("file_link", skinLinkFile);
            jsonObject.put("category", mItem.getCategory());
            jsonObject.put("count", mItem.getCount());
            jsonObject.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mItem = new JsonItemContent(jsonObject);
        setTextSkin(number, mItem.getCount());
        getSkinsFromSite(skinLinkFile);

        saveButton.setOnClickListener(v -> onAskPermissionSkin(this, DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY));
        saveButton.setImageResource(R.drawable.ic_fab_save);

        invalidateOptionsMenu();

    }

    private void setTextSkin(int numberCurrentSkin, int numberAmountSkin) {
        try {
            mTextViewSkin.setText(String.format(AppUtil.getCurrentLocale(this), "%d / %d", numberCurrentSkin, numberAmountSkin));
        } catch (Exception e) {
//            Crashlytics.logException(e);
        }
    }

}
