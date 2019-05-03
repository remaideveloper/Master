package com.remaistudio.master.model;

import androidx.fragment.app.Fragment;

import com.remaistudio.master.fragment.FragmentViewPager;

import org.json.JSONObject;

public class JsonItemFragment extends JsonItem {

    public JsonItemFragment(JSONObject JSONObject) {
        super(JSONObject);
    }

    public String getTitle() {
        return mJSONObject.optString("title");
    }

    public Class<? extends Fragment> getFragment() {
        return FragmentViewPager.class;
    }

    public String getData() {
        return mJSONObject.optString("data");
    }

    public String getSettings() {
        return mJSONObject.optString("settings");
    }

}
