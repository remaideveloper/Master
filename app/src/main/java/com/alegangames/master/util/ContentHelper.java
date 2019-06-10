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
                return "maps.json";
            case JsonItemFactory.MODS:
                return "mods.json";
            case JsonItemFactory.BUILDING:
                return "building.json";
            case JsonItemFactory.PACKS:
                return "packs.json";
            case JsonItemFactory.SEEDS:
                return "seeds.json";
            case JsonItemFactory.SERVERS:
                return "servers.json";
            case JsonItemFactory.TEXTURES:
                return "textures.json";
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
