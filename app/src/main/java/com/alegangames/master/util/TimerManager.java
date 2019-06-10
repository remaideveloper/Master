package com.alegangames.master.util;


import android.util.Log;

public class TimerManager {

    private static final String TAG = "TimerManager";
    private long timeout;

    /**
     * Запустить таймер
     */
    public void startTimer() {
        timeout = System.currentTimeMillis();
    }

    /**
     * Остановить таймер и получить в лог время выполнения в милисекундах.
     *
     * @param tag Тег таймера
     */
    public void stopTimer(String tag) {
        timeout = System.currentTimeMillis() - timeout;
        Log.d(TAG, "Timer: " + tag + " " + timeout);
    }

}
