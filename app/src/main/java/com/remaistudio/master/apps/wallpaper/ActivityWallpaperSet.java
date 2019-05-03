package com.remaistudio.master.apps.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;
import com.remaistudio.master.R;
import com.remaistudio.master.activity.ActivityAppParent;
import com.remaistudio.master.ads.admob.AdMobBanner;
import com.remaistudio.master.apps.wallpaper.util.CropImageView;
import com.remaistudio.master.ui.ToolbarUtil;
import com.remaistudio.master.util.StorageUtil;
import com.remaistudio.master.util.ToastUtil;
import com.remaistudio.master.util.permision.PermissionManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class ActivityWallpaperSet extends ActivityAppParent implements PermissionManager.InterfacePermission {

    private static final int LAYOUT = R.layout.activity_wallpaper_set;
    private static final String TAG = ActivityWallpaperSet.class.getSimpleName();
    private CropImageView mCropImageView;
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

        mCropImageView = findViewById(R.id.CropImageView);
        FloatingActionButton fab = findViewById(R.id.setAsWallpaper);

        String urlImage = getIntent().getStringExtra("IMAGE_URL");

        Picasso.get()
                .load(StorageUtil.checkUrlPrefix(urlImage))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mCropImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        fab.setOnClickListener((v) -> PermissionManager.onAskStoragePermission(this, 0));

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onPermissionSuccessResult(int requestCode) {
        new SetWallpaperTask(this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static class SetWallpaperTask extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        private WeakReference<ActivityWallpaperSet> activityReference;

        SetWallpaperTask(ActivityWallpaperSet activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                //noinspection WrongThread
                if (activityReference.get() != null && activityReference.get().mCropImageView != null)
                    bitmap = activityReference.get().mCropImageView.getCroppedImage();
            } catch (Exception e) {
//                Crashlytics.logException(e);
                ToastUtil.show(activityReference.get(), R.string.error);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (activityReference.get() != null) {
                activityReference.get().progressBar.setVisibility(View.GONE);
                if (bitmap != null) {
                    WallpaperManager wpm = WallpaperManager.getInstance(activityReference.get()
                            .getApplicationContext()); //The method mContext() is undefined for the type SetWallpaperTask
                    try {
                        wpm.setBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Crashlytics.logException(e);
                        ToastUtil.show(activityReference.get(), R.string.error);
                    }
                    ToastUtil.show(activityReference.get(), R.string.wallpaper_set_title);
                } else {
                    ToastUtil.show(activityReference.get(), R.string.wallpaper_not_set_title);
                }
            }
        }
    }

}
