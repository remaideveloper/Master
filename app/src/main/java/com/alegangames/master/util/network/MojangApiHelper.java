package com.alegangames.master.util.network;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alex on 2/15/18.
 */

public class MojangApiHelper {
    public static final String TAG = MojangApiHelper.class.getSimpleName();

    private static String baseUrlUser = "https://api.mojang.com/users/profiles/minecraft/";
    private static String baseUrlProfile = "https://sessionserver.mojang.com/session/minecraft/profile/";

    /**
     * Получает ссылку на скин юзера его по имени.
     * @param user имя пользователя
     * @return ссылка на скин или null если не найден
     */
    public static String getLinkForUser(String user) {
        String skinLink = null;
        try {
            //Формируем запрос для получения uuid по имени
            String url = baseUrlUser + user + "?at=" + System.currentTimeMillis();
            String userJson = getResponseText(url);

            //Получаем uuid по нику
            JSONObject object = new JSONObject(userJson);
            String uuid = object.optString("id");

            //Получаем данные пользователя
            String profileUrl = baseUrlProfile + uuid;
            String profileResponce = getResponseText(profileUrl);

            //Json возвращаемы сервером содержит ссылки закодированные в hex64
            //получаем закодированный объект и берем из него ссылку
            JSONObject propertiesObject = new JSONObject(profileResponce);
            JSONArray texturesArray = propertiesObject.getJSONArray("properties");
            JSONObject textureObject = texturesArray.getJSONObject(0); //ссылки на текстуры в hex64

            String hexLink = textureObject.getString("value");

            //Декодируем объект
            byte[] data = Base64.decode(hexLink, Base64.DEFAULT);
            String decoded = new String(data, "UTF-8");

            Log.d(TAG, "getLinkForUser: decoded " + decoded);

            //Создаем Json из декодированной строки и берем из него ссылку на скин
            JSONObject playerObject = new JSONObject(decoded);
            JSONObject skinObject = playerObject.getJSONObject("textures").getJSONObject("SKIN");
            skinLink = skinObject.getString("url");

            android.util.Log.d(TAG, "getLinkForUser: ready url " + skinLink);

        } catch (Exception ex) {
            //ignore, just return null
        }
        return skinLink;
    }

    /**
     * Возвращает ответ от сервера в виде строки
     * @param stringUrl запрос
     * @return ответ
     * @throws IOException
     */
    private static String getResponseText(String stringUrl) throws IOException {
        StringBuilder response = new StringBuilder();

        URL url = new URL(stringUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()), 8192);
            String strLine = null;
            while ((strLine = input.readLine()) != null) {
                response.append(strLine);
            }
            input.close();
        }
        httpconn.disconnect();
        return response.toString();
    }
}
