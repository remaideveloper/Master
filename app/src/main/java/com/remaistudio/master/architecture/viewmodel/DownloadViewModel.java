package com.remaistudio.master.architecture.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.remaistudio.master.download.DownloadAsyncTask;
import com.remaistudio.master.events.DownloadEvent;

public class DownloadViewModel extends AndroidViewModel {

    public static final String TAG = DownloadViewModel.class.getSimpleName();

    public static final int DOWNLOAD_CONTENT = 2;
    public static final int DOWNLOAD_IMAGE_SHARE = 4;
    public static final int DOWNLOAD_SKIN_TO_GALLERY = 8;

    public static final int UNZIP_MAP = 0;
    public static final int UNZIP_MOD = 1;
    public static final int UNZIP_SIMPLE = 2;

    private MutableLiveData<DownloadEvent> mDownloadEventLiveData = new MutableLiveData<>();
    private MutableLiveData<DownloadEvent> mUnzipLiveData = new MutableLiveData<>();

    private AsyncTask<Void, DownloadEvent, Boolean> mAsyncTask;

    public DownloadViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Возвращает LiveData для наблюдения за состоянием загрузки
     */
    public MutableLiveData<DownloadEvent> getDownloadLiveData() {
        return mDownloadEventLiveData;
    }

    /**
     * Запусает AsyncTask с загрузкой контента
     * Если AsyncTask уже запущен, отменяет его
     *
     * @param url         Ссылка на файл
     * @param savePath    Путь для сохранения файла
     * @param saveName    Название для сохранения файла
     * @param requestCode Код запроса
     */
    public void onStartDownload(String url, String savePath, String saveName, int requestCode) {
        if (isRunning())
            mAsyncTask.cancel(true);

        mAsyncTask = new DownloadAsyncTask(getApplication(), saveName, savePath, url, requestCode, mDownloadEventLiveData);
        mAsyncTask.execute();
    }

    /**
     * Проверяет условие не является ли AsyncTask null и запущен ли он
     *
     * @return True если AsyncTask запущен
     */
    public boolean isRunning() {
        return mAsyncTask != null && mAsyncTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    /**
     * Отменяет AsyncTask
     */
    public void onCancelDownload() {
        if (mAsyncTask != null)
            mAsyncTask.cancel(false);
    }


    /**
     * Вызывается при уничтожении Activity из которого был создан экземпляр ViewModel
     */
    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        super.onCleared();
        onCancelDownload();
    }
}
