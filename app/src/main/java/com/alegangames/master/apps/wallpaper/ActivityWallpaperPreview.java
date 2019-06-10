package com.alegangames.master.apps.wallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.github.clans.fab.FloatingActionButton;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.ads.admob.AdMobBanner;
import com.alegangames.master.architecture.viewmodel.DownloadViewModel;
import com.alegangames.master.events.DownloadEvent;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.ui.ToolbarUtil;
import com.alegangames.master.util.SocialManager;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.ToastUtil;
import com.alegangames.master.util.json.SerializableJSONObject;
import com.alegangames.master.util.permision.PermissionManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.alegangames.master.activity.ActivityItem.JSON_OBJECT_KEY;
import static com.alegangames.master.download.DownloadAsyncTask.STATUS_SUCCESS;

public class ActivityWallpaperPreview extends ActivityAppParent implements PermissionManager.InterfacePermission {

    private static final int LAYOUT = R.layout.activity_wallpaper_preview;
    private static final String TAG = ActivityWallpaperPreview.class.getSimpleName();

    FloatingActionButton set_as_wallpaper, share, save;
    private JsonItemContent mItem;
    private DownloadViewModel downloadViewModel;
    private ProgressBar progressBar;
    public AdMobBanner mAdMobBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        ToolbarUtil.setToolbar(this, true);

        mInterfacePermission = this;

        mAdMobBanner = new AdMobBanner(this);
        mAdMobBanner.onCreate();

        SerializableJSONObject serializableJSONObject = (SerializableJSONObject) getIntent().getSerializableExtra(JSON_OBJECT_KEY);
        mItem = new JsonItemContent(serializableJSONObject.getJSONObject());

        ImageView imageView = findViewById(R.id.imageViewItem);

        Picasso.get()
                .load(StorageUtil.checkUrlPrefix(mItem.getImageLink()))
                .placeholder(R.drawable.empty_image)
                .into(imageView);

        set_as_wallpaper = findViewById(R.id.fab_set_as_wallpaper);
        share = findViewById(R.id.fab_share);
        save = findViewById(R.id.fab_save);


        set_as_wallpaper.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ActivityWallpaperSet.class);
            intent.putExtra("IMAGE_URL", mItem.getImageLink());
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        });

        share.setOnClickListener(view -> PermissionManager.onAskStoragePermission(this, DownloadViewModel.DOWNLOAD_IMAGE_SHARE));
        save.setOnClickListener(view -> PermissionManager.onAskStoragePermission(this, DownloadViewModel.DOWNLOAD_CONTENT));

        progressBar = findViewById(R.id.progressBar);

        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);
        downloadViewModel.getDownloadLiveData().observe(this, this::onDownloadEvent);

    }

    @Override
    public void onPermissionSuccessResult(int requestCode) {
        progressBar.setVisibility(View.VISIBLE);

        downloadViewModel.onStartDownload(
                StorageUtil.checkUrlPrefix(mItem.getImageLink()),
                "/Download/Wallpapers/",
                mItem.getName(),
                requestCode);

    }


    public void onDownloadEvent(DownloadEvent event) {
        if (event.type == DownloadEvent.FINISHED) {
            onDownloadComplete(event.file, event.status, event.requestCode);
        }
    }

    public void onDownloadComplete(File file, int status, int requestCode) {

        if (status == STATUS_SUCCESS) {
            switch (requestCode) {
                case DownloadViewModel.DOWNLOAD_IMAGE_SHARE:
                    if (file != null)
                        SocialManager.onShareImage(this, file, null);
                    break;
                case DownloadViewModel.DOWNLOAD_CONTENT:
                    ToastUtil.show(this, R.string.file_saved);
                    break;
            }
        }

        progressBar.setVisibility(View.GONE);
    }

}
