package com.alegangames.master.util.json;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Cache implements Serializable {
    private String cacheString;
    private long updateTime;

    public Cache(String cacheString, long updateTime){
        this.cacheString = cacheString;
        this.updateTime = updateTime;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(cacheString);
        oos.writeLong(updateTime);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        cacheString = (String) ois.readObject();
        updateTime = ois.readLong();
    }

    public JSONArray getJsonArray() {
        try {
            return new JSONArray(cacheString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public void setmJsonArray(String cacheString) {
        this.cacheString = cacheString;
    }

    public long getmUpdateTime() {
        return updateTime;
    }

    public void setmUpdateTime(long mUpdateTime) {
        this.updateTime = mUpdateTime;
    }
}