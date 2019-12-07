package com.alegangames.master.util.json;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static Map<String, JSONArray> cache = new HashMap<>();

    public static void putCahce(String key, JSONArray object){
        cache.put(key, object);
    }

    public static JSONArray getCache(String key){
        return cache.get(key);
    }
}
