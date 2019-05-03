package com.remaistudio.master.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.remaistudio.master.R;
import com.remaistudio.master.events.DownloadEvent;
import com.remaistudio.master.interfaces.InterfaceDownload;
import com.remaistudio.master.util.files.FileUtil;
import com.remaistudio.master.util.files.MemoryManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.remaistudio.master.architecture.viewmodel.DownloadViewModel.DOWNLOAD_CONTENT;
import static com.remaistudio.master.download.DownloadAsyncTask.STATUS_MEMORY_ERROR;
import static com.remaistudio.master.download.DownloadAsyncTask.STATUS_SUCCESS;

public class ServerHelper {

    public static final String TAG = ServerHelper.class.getSimpleName();

    public static void addServer(String name, String adress, String port, InterfaceDownload downloadInterface) {

        AsyncTask.execute(() -> {
            //Сформировать полную строку сервера
            String fullServer = name + ":" + adress + ":" + port;
            //Создаем экземляр файла настроек
            File fileServers = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/external_servers.txt");

            //Если файл существет, записать сервер в последнюю строку
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;
            List<String> stringList = new ArrayList<>();
            int lines = 0;
            boolean repeat = false;

            if (!MemoryManager.externalMemoryAvailable()) {
                //Проверка на доступность внешней памяти
                new Handler(Looper.getMainLooper()).post(() ->
                        downloadInterface.onDownloadComplete(new DownloadEvent(null, STATUS_MEMORY_ERROR, DOWNLOAD_CONTENT)));
            }

            try {

                if (!fileServers.exists()) {
                    //Если файл не существет, создать новый файл, и записать в него строку с сервером
                    FileUtil.createFileWithString(fileServers, "1:" + fullServer);
                } else {
                    //Если файл существет, и записать в него строку с сервером, после других строк с серверами

                    // открываем поток для чтения
                    bufferedReader = new BufferedReader(new FileReader(fileServers));
                    String str;
                    // читаем содержимое
                    while ((str = bufferedReader.readLine()) != null) {
                        ++lines;
                        if (str.contains(fullServer)) {
                            repeat = true;
                        }
                        stringList.add(str);
                    }
                    if (!repeat) {
                        stringList.add(++lines + ":" + fullServer);
                    }

                    // открываем поток для записи
                    bufferedWriter = new BufferedWriter(new FileWriter(fileServers));

                    // пишем данные
                    for (String s : stringList) {
                        bufferedWriter.write(s); //записываем в файл строку
                        bufferedWriter.write("\n"); // переходим на след строку
                    }
                }
                new Handler(Looper.getMainLooper()).post(() ->
                        downloadInterface.onDownloadComplete(new DownloadEvent(null, STATUS_SUCCESS, DOWNLOAD_CONTENT)));

            } catch (IOException e) {
                e.printStackTrace();
//                Crashlytics.logException(e);
            } finally {
                // закрываем потоки
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.flush();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                    Crashlytics.logException(e);
                }
            }
        });
    }

    public static void openServer(Context context, String name, String adress, String port) {
        Log.d(TAG, "openServer");
        Uri uri = Uri.parse("minecraft://?addExternalServer=" + name + "|" + adress + ":" + port);
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            ToastUtil.show(context, R.string.error);
            e.printStackTrace();
        }
    }
}
