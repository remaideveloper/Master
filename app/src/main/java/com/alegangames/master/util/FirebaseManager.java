package com.alegangames.master.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.alegangames.master.util.json.Cache;
import com.alegangames.master.util.json.CacheManager;
import com.alegangames.master.util.json.JsonHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FirebaseManager {

    final static long TWO_MEGABYTE = 1024 * 1024 * 2;

    static FirebaseStorage storage = FirebaseStorage.getInstance("gs://alegan_games");

    public interface Listener<T> {
        void onResponse(T response);
    }

    public static void loading(Context context, Listener<JSONArray> listener, String url){

        url = "gs://alegan_games/data/content/"+ url;
        StorageReference storageReference = storage.getReferenceFromUrl(url);

        String finalPath = url.substring(url.lastIndexOf("/")+1);

        JSONArray cacheRAM = CacheManager.getCache(finalPath);

        if (cacheRAM!=null) {
            listener.onResponse(cacheRAM);
            return;
        }


        final Cache cacheJSONArray = readCache(context, finalPath);

        //Если дисковый кэш существует
        if (cacheJSONArray!=null) {
            storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {

                long updateTime = storageMetadata.getUpdatedTimeMillis();

                //Если локальная дата последнего обновления не совпадает с датой на сервере
                if (cacheJSONArray.getmUpdateTime()!=updateTime){
                    storageReference.getBytes(TWO_MEGABYTE).addOnSuccessListener(bytes -> {
                        String str = null;
                        try {
                            str = JsonHelper.decryptString(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(str);
                            listener.onResponse(jsonArray);
                            writeCache(context, finalPath, str, updateTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onResponse(new JSONArray());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
                else {
                    listener.onResponse(cacheJSONArray.getJsonArray());
                }
            });
        }else {
            storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                storageReference.getBytes(TWO_MEGABYTE).addOnSuccessListener(bytes -> {
                    String str = null;
                    try {
                        str = JsonHelper.decryptString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        listener.onResponse(jsonArray);
                        writeCache(context, finalPath, str, storageMetadata.getUpdatedTimeMillis());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onResponse(new JSONArray());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });

        }
    }

    private static void writeCache(Context context, String path, String cacheString, long updateTime){
        ObjectOutputStream oos = null;
        try {
            CacheManager.putCahce(path, new JSONArray(cacheString));
            oos = new ObjectOutputStream(context.openFileOutput(path+".out",Context.MODE_PRIVATE));
            oos.writeObject(new Cache(cacheString, updateTime));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (oos!=null){
                try {
                    oos.flush();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static Cache readCache(Context context, String path){
        ObjectInputStream ois = null;
        Cache cache = null;
        try {
            FileInputStream fis = context.openFileInput(path+".out");
            ois = new ObjectInputStream(fis);
            cache = (Cache) ois.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (ois!=null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cache;
    }
}
