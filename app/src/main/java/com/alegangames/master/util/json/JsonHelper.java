package com.alegangames.master.util.json;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class JsonHelper {

    private static final String TAG = JsonHelper.class.getSimpleName();
    private static SecretKey secretKey;

    static {
        try {
            secretKey = generateKey("&^*aleganGames_master&^*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить JsonObject из файла находящегося в папке Assets
     *
     * @param string Название файла находящегося в папке Assets
     * @return JsonObject который находится в файле
     */
    public static JSONObject getJsonObjectFromStorage(Context context, String string) {
        Log.d(TAG, "getJsonObjectFromStorage");
        try {
            byte[] buffer;
            InputStream is = context.getAssets().open(string);
            int n = is.available();
            buffer = new byte[n];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить JsonArray из файла находящегося в папке Assets
     *
     * @param string Название файла находящегося в папке Assets
     * @return JsonArray который находится в файле
     */
    public static JSONArray getJsonArrayFromStorage(Context context, String string) {

        try {
            InputStream is = context.getAssets().open(string);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            Log.d(TAG, "getJsonArrayFromStorage size: " + size);
            return new JSONArray(decryptString(buffer));
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static SecretKey generateKey(String password) throws Exception {
        return  new SecretKeySpec(password.getBytes(), "AES");
    }

    public static String decryptString(byte[] cipherText) throws Exception {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(cipherText), Charset.forName("UTF8"));
    }


    public static JsonReader getJsonReaderFromStorage(Context context, String string) {
        try {
            InputStream is = context.getAssets().open(string);
            return new JsonReader(new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Get response from an URL request (GET)
    public static String getDataFromUrl(String url) {
        // Making HTTP request
        android.util.Log.v("INFO", "Requesting: " + url);

        String data = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new URL(url).openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            data = readAll(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    //Get JSON from an url and parse it to a JSON Object.
    public static JSONObject getJSONObjectFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            Log.d(TAG, "getJSONObjectFromUrl: " + data);
            return new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //Get JSON from an url and parse it to a JSON Array.
    public static JSONArray getJSONArrayFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            Log.d(TAG, "getJSONArrayFromUrl: " + data);
            return new JSONArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String checkLocaleJsonObject(JSONObject jsonObject, String object) {
        String locale = Locale.getDefault().getLanguage();
        String string = object + "-" + locale;
        if (!jsonObject.isNull(string) && !jsonObject.optString(string).isEmpty()) {
            return jsonObject.optString(string);
        }
        return jsonObject.optString(object);
    }

}