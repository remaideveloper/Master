package com.alegangames.master.util;

public class StorageUtil {

    public static String STORAGE = "https://storage.googleapis.com/alegan_games";
    public static String checkUrlPrefix(String url) {
        if (url.contains(STORAGE))
            return url;
        else
            return STORAGE + url;
    }
}
