package com.alegangames.master.util.notif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Менеджер уведомлений
 */
public class NotificationAppManager {

    private static final String TAG = NotificationAppManager.class.getSimpleName();

    private Context mContext;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent mPendingIntent;
    private int mNotificationID;


    public NotificationAppManager(Context context, int notificationID) {
        mContext = context;
        mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationID = notificationID;
    }

    /**
     * Создать уведомление
     */
    public NotificationAppManager onCreateNotification(String notificationChannelID) {
        createChannel(notificationChannelID);
        mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, notificationChannelID);
        mBuilder.setContentIntent(mPendingIntent);
        return this;
    }

    /**
     * Изменить заголовок уведомления
     *
     * @param title       Заголовок
     * @param description Описание
     */
    public NotificationAppManager setTextNotification(String title, String description) {
        mBuilder.setContentTitle(title)
                .setContentText(description);
        return this;
    }

    /**
     * Изменить иконки уведомления
     *
     * @param smallIconId Маленькая иконка для StatusBar
     * @param largeIconId Большая иконка для уведомления
     */
    public NotificationAppManager setIconNotification(@DrawableRes int smallIconId, @DrawableRes int largeIconId) {
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(mContext.getResources(), largeIconId);
        mBuilder.setLargeIcon(largeIconBitmap)
                .setSmallIcon(smallIconId);
        return this;
    }

    /**
     * Устанавливает PendingIntent
     *
     * @param pendingIntent для открытия Activity
     * @return
     */
    public NotificationAppManager setPendingIntent(PendingIntent pendingIntent) {
        mPendingIntent = pendingIntent;
        mBuilder.setContentIntent(mPendingIntent);
        return this;
    }

    /**
     * Изменить прогресс уведомления
     *
     * @param max           Максимальный прогресс
     * @param progress      Текущий прогресс
     * @param indeterminate Отображать неопределенный прогресс
     */
    public void setProgressNotification(final int max, final int progress, final boolean indeterminate) {
        new Thread(() -> {
            if (mBuilder != null && mNotifyManager != null) {
                mBuilder.setProgress(max, progress, indeterminate);
                mNotifyManager.notify(mNotificationID, mBuilder.build());
            }
        }
        ).start();
    }

    public NotificationAppManager setAutoCancel(boolean autoCancel) {
        mBuilder.setAutoCancel(autoCancel);
        return this;
    }

    /**
     * Закончить и уведомить пользователя окончании загрузки
     */
    public void onCompleteProgressNotification() {
//        mNotifyManager.cancel(mNotificationID);
        mBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(mNotificationID, mBuilder.build());
    }

    /**
     * Опубликовать уведомление, которое будет отображаться в строке состояния.
     */
    public void onBuildNotification(){
        mNotifyManager.notify(mNotificationID, mBuilder.build());
    }

    public void cancelNotifications() {
        mNotifyManager.cancel(mNotificationID);
    }

    private void createChannel(String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelName, "Downloads channel",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Content download notifications");
            channel.enableVibration(false);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}
