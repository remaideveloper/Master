package com.remaistudio.master.util;

public class StorageUtil {

    public static String STORAGE = "https://storage.googleapis.com/json-data-base.appspot.com";
    public static String STORAGE_APPSCREAT = "https://storage.googleapis.com/appscreat-project";

    public static String checkUrlPrefix(String url) {
        if (url.contains(STORAGE))
            return url;
        else
            return STORAGE + url;
    }
}
