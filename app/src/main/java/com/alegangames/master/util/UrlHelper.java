package com.alegangames.master.util;


import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import java.util.HashMap;
import java.util.Map;

public class UrlHelper {

    public static boolean isGooglePlayUrl(String url) {
        return url.contains("market://") || url.contains("play.google");
    }

    public static String getPackageNameFromUrl(String url) {
        return getQueryMap(url).get("id");
    }

    public static String onVerificationUrl(String url) {
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * Получить из строки запроса пары ключ/значение.
     */
    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<>();
        String[] queryString = query.split("\\?");
        if (queryString.length > 1) {
            String[] params = queryString[1].split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * @param source  Источник
     * @param company Компания
     * @param medium  Канал
     * @return Ссылка UTM
     */
    public static String getUtmRefferer(String source, String company, String medium) {
        return "&referrer=" +
                "utm_source" + "%3D" + source + "%26" +
                "utm_campaign" + "%3D" + company + "%26" +
                "utm_medium" + "%3D" + medium;
    }

    /**
     * @param source  Источник
     * @param company Компания
     * @return Ссылка UTM
     */
    public static String getUtmRefferer(String source, String company) {
        return "&referrer=" +
                "utm_source" + "%3D" + source + "%26" +
                "utm_campaign" + "%3D" + company;
    }

    /**
     * Получить окончание URL
     *
     * @param url String URL ссылка
     * @return окончание URL ссылки (Символы после последнего символа "/")
     */
    public static String getExtensionUrl(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    public static String getFileNameUrl(String url) {
        return URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));
    }

}
