package com.alegangames.master.model;

import com.alegangames.master.util.json.JsonHelper;

import org.json.JSONObject;

public class JsonItemContent extends JsonItem {

    public JsonItemContent(JSONObject JSONObject) {
        super(JSONObject);
    }

    public String getName() {
        return JsonHelper.checkLocaleJsonObject(mJSONObject, "name");
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
        return true;
    }

    public int getCount() {
        return mJSONObject.optInt("count");
    }

    public int getPrice() {
        int price = mJSONObject.optInt("price",0);
        return (price == 0 ? 1 :price)*25;
    }

}
