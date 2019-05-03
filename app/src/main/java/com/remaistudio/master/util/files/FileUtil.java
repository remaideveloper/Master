package com.remaistudio.master.util.files;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    private final static String TAG = FileUtil.class.getSimpleName();

    /**
     * Получить расширение файла
     *
     * @param filePath String Имя или путь файла
     * @return Расширение файла (Символы после последней точки)
     */
    public static String getExtensionFile(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    /**
     * Получить расширение файла
     *
     * @param filePath Файл
     * @return Расширение файла (Символы после последней точки)
     */
    public static String getExtensionFile(File filePath) {
        return filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf(".") + 1);
    }

    /**
     * Получить название файла
     * File.getName()
     *
     * @param filePath Файл
     * @return Название файла (Символы после последней косой черты)
     */
    public static String getFileName(File filePath) {
        return filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf('/') + 1);
    }

    /**
     * Получить название файла
     * File.getName()
     *
     * @param filePath String Имя или путь файла
     * @return Название файла (Символы после последней косой черты)
     */
    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    /**
     * Получить путь до файла без названия файла
     * File.getParent()
     *
     * @param filePath Файл
     * @return Путь до файла без названия файла (Символы до последней косой черты)
     */
    public static String getPathWithOutFileName(File filePath) {
        return filePath.getAbsolutePath().substring(0, filePath.getAbsolutePath().lastIndexOf('/') + 1);
    }

    /**
     * Получить путь до файла без названия файла
     * File.getParent()
     *
     * @param filePath String Путь файла
     * @return Путь до файла без названия файла (Символы до последней косой черты)
     */
    public static String getPathWithOutFileName(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf('/') + 1);
    }


    /**
     * Создать новый файл и записать в него строку
     *
     * @param file   Файл
     * @param string Строка
     * @return True/False Файл успешно создан и строка записана/Файл не создан или строка не записана
     */
    public static void createFileWithString(File file, String string) {
        File directory = new File(FileUtil.getPathWithOutFileName(file));
        //Если директория файла не существует, создаем ее
        if (!directory.exists()) {
            directory.mkdirs();
        }
        FileOutputStream fileOutput = null;
        BufferedWriter bufferedWriter = null;
        try {
            //Создаем файл
            fileOutput = new FileOutputStream(file);
            //Открываем поток для записи
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            //Записываем строку в файл
            bufferedWriter.write(string);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Очищаем и закрываем потоки
            try {
                if (fileOutput != null) fileOutput.flush();
                if (fileOutput != null) fileOutput.close();
                if (bufferedWriter != null) bufferedWriter.flush();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Копировать директории
     *
     * @param file
     * @param file2
     * @throws IOException
     */
    public static void copyDirectory(File file, File file2) throws IOException {
        if (file.isDirectory()) {
            if (!file2.exists() && !file2.mkdirs()) {
                throw new IOException("Cannot create dir " + file2.getAbsolutePath());
            }
            String[] list = file.list();
            for (String aList : list) {
                copyDirectory(new File(file, aList), new File(file2, aList));
            }
        } else {
            File parentFile = file2.getParentFile();
            if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                throw new IOException("Cannot create dir " + parentFile.getAbsolutePath());
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            byte[] array = new byte[1024];
            while (true) {
                final int read = fileInputStream.read(array);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(array, 0, read);
            }
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    public static void onDeleteHiddenFilesMac(File path) {
        File folder = new File(path.getPath());
        for (File file : folder.listFiles()) {
            if (file.getName().equals(".DS_Store")) {
                file.delete();
            }
        }
    }
}
