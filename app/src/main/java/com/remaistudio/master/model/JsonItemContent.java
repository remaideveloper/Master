package com.remaistudio.master.model;

import com.remaistudio.master.util.json.JsonHelper;

import org.json.JSONObject;

public class JsonItemContent extends JsonItem {

    public JsonItemContent(JSONObject JSONObject) {
        super(JSONObject);
    }

    public String getName() {
        return mJSONObject.optString("name");
    }

    public String getDescription() {
        return JsonHelper.checkLocaleJsonObject(mJSONObject, "description");
    }

    public String getCategory() {
        return mJSONObject.optString("category");
    }

    public String getVersion() {
        return mJSONObject.optString("version");
    }

    public String getImageLink() {
        return mJSONObject.optString("image_link");
    }

    public String getFileLink() {
        return mJSONObject.optString("file_link");
    }

    public Boolean isPremium() {
        return mJSONObject.optInt("price") > 0;
    }

    public int getCount() {
        return mJSONObject.optInt("count");
    }

    public int getPrice() {
        return mJSONObject.optInt("price");
    }

}
