package com.alegangames.master.util;


import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.model.JsonItemFactory;

public class ContentHelper {

    /**
     * Возвращает имя json файла по id итему
     */
    public static String getJsonNameItem(JsonItemContent item) {
        switch (item.getId()) {
            case JsonItemFactory.MAPS:
                return "ma.txt";
            case JsonItemFactory.MODS:
                return "mo.txt";
            case JsonItemFactory.BUILDING:
                return "b.txt";
            case JsonItemFactory.PACKS:
                return "p.txt";
            case JsonItemFactory.SEEDS:
                return "see.txt";
            case JsonItemFactory.SERVERS:
                return "ser.txt";
            case JsonItemFactory.TEXTURES:
                return "t.txt";
            default:
                return "";
        }
    }

    /**
     * Возвращает путь для сохранения загруженных файлов
     */
    public static String getFileSavePathItem(JsonItemContent item) {
        switch (item.getId()) {
            case JsonItemFactory.PACKS:
                return "/games/com.mojang/minecraftPacks/";
            case JsonItemFactory.MAPS:
                return "/games/com.mojang/minecraftWorlds/";
            case JsonItemFactory.MODS:
                return "/games/com.mojang/";
            case JsonItemFactory.ADDONS:
                return "/games/com.mojang/";
            case JsonItemFactory.SEEDS:
                return "/games/com.mojang/minecraftWorlds/";
            case JsonItemFactory.TEXTURES:
                return "/games/com.mojang/resource_packs/";
            case JsonItemFactory.BUILDING:
                return "/Download/";
            default:
                return null;
        }
    }
}
