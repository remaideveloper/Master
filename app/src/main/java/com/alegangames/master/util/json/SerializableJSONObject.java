package com.alegangames.master.util.json;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Helper class to to keep JSONObject's in a Serializable object
 */
public class SerializableJSONObject implements Serializable {
    private transient JSONObject mJSONObject;

    public SerializableJSONObject(JSONObject jsonObject) {
        this.mJSONObject = jsonObject;
    }

    @NonNull
    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(mJSONObject.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        mJSONObject = new JSONObject((String) ois.readObject());
    }
}