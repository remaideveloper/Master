package com.remaistudio.master.apps.skins;

import android.app.SearchManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.remaistudio.master.Config;
import com.remaistudio.master.R;
import com.remaistudio.master.architecture.viewmodel.DownloadViewModel;
import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.model.JsonItemFactory;
import com.remaistudio.master.ui.ToolbarUtil;
import com.remaistudio.master.util.ColorList;
import com.remaistudio.master.util.ToastUtil;
import com.remaistudio.master.util.json.SerializableJSONObject;
import com.remaistudio.master.util.network.MojangApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static com.remaistudio.master.activity.ActivityItem.JSON_OBJECT_KEY;

public class ActivitySkinsStealer extends ActivitySkins implements SearchView.OnQueryTextListener {

    private static final String TAG = ActivitySkinsStealer.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_skins_stealer;
    private SearchView searchView;
    private String mSearchText;

    private Handler delayHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        if (getIntent().getSerializableExtra(JSON_OBJECT_KEY) != null) {
            Log.d(TAG, "onCreate: Intent != null");
            SerializableJSONObject serializableJSONObject = (SerializableJSONObject) getIntent().getSerializableExtra(JSON_OBJECT_KEY);
            mItem = new JsonItemContent(serializableJSONObject.getJSONObject());

            Log.d(TAG, "onCreate: mItem.getFileLink() " + mItem.getFileLink());

            //Получаем название скина, если содержит расширение, то обрезаем его
            int index = mItem.getName().lastIndexOf(".");
            String name = index == -1 ? mItem.getName() : mItem.getName().substring(0, index);
            android.util.Log.d(TAG, "onCreate: name " + mItem.getName());

            mSearchText = name;
        } else {
            Log.d(TAG, "onCreate: Intent == null");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", JsonItemFactory.SKIN_STEALER);
                jsonObject.put("name", "herobrine.png");
                jsonObject.put("file_link", "http://textures.minecraft.net/texture/bdd3cc8ae29a4dfe565ddcd7a663388d8ead7f5bc8dba5a493148b245984d");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSearchText = "herobrine";
            mItem = new JsonItemContent(jsonObject);
        }


        initFAB();
        initGLSurfaceView();
        getSkinsFromSite(mItem.getFileLink());
        ToolbarUtil.setActionBar(this, !Config.SINGLE_APP);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSaveEnabled(true);
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setOnQueryTextListener(this);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchMenuItem.expandActionView();

        searchView.setQuery(mSearchText, false);

        return true;
    }

    @Override
    protected void onStop() {
        delayHandler.removeCallbacksAndMessages(null);
        super.onStop();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText!=null && !newText.isEmpty()) {
            //Ввод обновился, удаляем запланированную задачу
            delayHandler.removeCallbacksAndMessages(null);
            //Планируем загрузку с отсрочкой, для избежания многократных загрузок при вводе
            delayHandler.postDelayed(() -> {
                setSkinOnSearch(newText);
                sendSearchEvent(newText);
            }, 1000);
        }
        return true;
    }

    /**
     * Асинхронно получает ссылку на скин по имени пользователя
     * и запускает загрузку изображения. Обновление интерфейса выполняется
     * в главном потоке
     *
     * @param query имя пользователя для поиска
     */
    private void setSkinOnSearch(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);

        AsyncTask.execute(() -> {
            //Получаем ссылку на скин для нового апи асинхронно
            String link = MojangApiHelper.getLinkForUser(query);

            if (link == null) {
                //Выводим сообщение об ошибке, если ответ null
                Log.d(TAG, "setSkinOnSearch: ERROR");
                ToastUtil.show(this, R.string.error);
                return;
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", JsonItemFactory.SKIN_STEALER);
                jsonObject.put("name", query + ".png");
                jsonObject.put("file_link", link);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                mItem = new JsonItemContent(jsonObject);

                saveButton.setOnClickListener(v -> onAskPermissionSkin(this, DownloadViewModel.DOWNLOAD_SKIN_TO_GALLERY));
                setFabMain(R.drawable.ic_fab_save, ColorList.BLUE, ColorList.BLUE_LIGHT, ColorList.BLUE_DARK);

                getSkinsFromSite(mItem.getFileLink());
            });
        });
    }

    private void sendSearchEvent(String query) {
        Bundle params = new Bundle();
        params.putString("query", query);
        FirebaseAnalytics.getInstance(this).logEvent("stealer_search", params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSurfaceView();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
