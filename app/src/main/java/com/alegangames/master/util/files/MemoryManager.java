package com.alegangames.master.util.files;


import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

public class MemoryManager {

    private static final String TAG = MemoryManager.class.getSimpleName();

    /**
     * @return Доступность внешней памяти для редактирования
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * @return Доступный размер внутренней памяти в килобайтах или мегабайтах
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * @return Полный размер внутренней памяти в килобайтах или мегабайтах
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * @return Полный размер внешней памяти в килобайтах или мегабайтах
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        return 0;
    }

    /**
     * @return Доступный размер внешней памяти в байтах
     */
    public static long getAvailableExternalMemorySizeLong() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (blockSize * availableBlocks);
        } else {
            return 0;
        }
    }

    /**
     * Форматирование чисел
     *
     * @param size Размер в байтах
     * @return Размер в килобайтах или мегабайтах
     */
    public static String formatSize(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }

    /**
     * Проверка на доступность памяти
     *
     * @param fileSize   Размер файла
     * @param memorySize Размер памяти
     * @return Хватает ли размера памяти по сравнению с размером файла
     */
    public static boolean getEnoughMemory(long fileSize, long memorySize) {
        Log.d(TAG, "getEnoughMemory: " +
                " fileSize " + formatSize(fileSize) +
                " memorySize " + formatSize(memorySize));
        return memorySize > fileSize;
    }


}
