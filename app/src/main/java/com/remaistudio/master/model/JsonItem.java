package com.remaistudio.master.model;

import org.json.JSONObject;

public class JsonItem {

    public JSONObject mJSONObject;

    public JsonItem(JSONObject JSONObject) {
        mJSONObject = JSONObject;
    }

    public JSONObject getJsonObject() {
        return mJSONObject;
    }

    public String getId() {
        return mJSONObject.optString("id");
    }

}
