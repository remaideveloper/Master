package com.alegangames.master.util;

import android.os.StrictMode;

import com.alegangames.master.BuildConfig;

/**
 * StrictMode - это инструмент, который позволит нам узнать,
 * выполняет ли наш код неэффективные или некорректные операции.
 */
public class StrictModeUtil {

    public static void init() {
        if (BuildConfig.DEBUG) {
            //Операции с диском или сетью
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll() //Обнаружение всех ошибок
//                    .permitAll() //Отключите обнаружение всего.
                    .penaltyLog() //Фиксация ошибок в лог
                    .build());
            //Операции с памятью
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }
}
