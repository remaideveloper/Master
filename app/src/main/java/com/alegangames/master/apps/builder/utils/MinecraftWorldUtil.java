package com.alegangames.master.apps.builder.utils;


import com.alegangames.master.apps.builder.entities.Level;
import com.alegangames.master.apps.builder.entities.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MinecraftWorldUtil {

    private static final String TAG = MinecraftWorldUtil.class.getSimpleName();
    public static String buildY;
    public static Level level;

    public static ArrayList<World> getMinecraftWorld(String s) {
        ArrayList<World> list = null;
        final File file = new File(s);
        file.mkdirs();
        final File[] listFiles = file.listFiles();

        //avoid NPE
        if(listFiles == null) return null;

        for(File childFile : listFiles) {
            if (!childFile.isDirectory()) continue;
            String levelName = null;
            File[] nestedFiles = childFile.listFiles();

            //Получаем имя карты
            for (File nestedFile : nestedFiles) {

                if (nestedFile.getName().equals("levelname.txt")) {
                    levelName = readLevelName(s + "/" + childFile.getName() + "/" + "levelname.txt");
                    break;
                }

                if (nestedFile.getName().equals("level.dat")) {
                    levelName = childFile.getName();
                }
            }

            //Если имя карты найдено, то добавляем ее в лист
            if (levelName != null && !levelName.isEmpty()) {
                final String format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(new Date(new File(s + "/" + childFile.getName()).lastModified()));

                final World world = new World();
                world.setName(levelName);
                world.setFolder_name(childFile.getName());
                world.setDate(format);

                if (list == null) list = new ArrayList<>();
                list.add(world);
            }
        }
        return list;
    }

    public static String readLevelName(String path) {
        //Создаем экземляр файла настроект
        File file = new File(path);
        byte[] data = new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
