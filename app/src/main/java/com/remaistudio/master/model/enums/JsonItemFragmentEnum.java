package com.remaistudio.master.model.enums;

import androidx.fragment.app.Fragment;

import com.remaistudio.master.fragment.FragmentViewPager;

public enum JsonItemFragmentEnum {

    MAIN("Main", "data-main.json", 3, false, false, false),
    SKINS("Skins", "skins.json", 2, false, true, true),
    PACKS("Packs", "packs.json", 2, false, true, true),
    MODS("Mods", "mods.json", 2, false, true, true),
    BUILDING("Building", "building.json", 2, false, true, true),
    ADDONS("Addons", "addons.json", 2, false, true, true),
    SEEDS("Seeds", "seeds.json", 2, false, true, true),
    TEXTURES("Textures", "textures.json", 2, false, true, true),
    SERVERS("Servers", "servers.json", 2, false, true, true),
    WALLPAPERS("Wallpapers", "wallpapers.json", 2, false, true, true),
    MAPS("Maps", "maps.json", 2, false, true, true);

    private String mTitle;
    private String mData;
    private int mColumn;
    private boolean mInterstitial;
    private boolean mBanner;
    private boolean mShuffle;

    JsonItemFragmentEnum(String title, String data, int column, boolean interstitial, boolean banner, boolean shuffle) {
        mTitle = title;
        mData = data;
        mColumn = column;
        mInterstitial = interstitial;
        mBanner = banner;
        mShuffle = shuffle;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getData() {
        return mData;
    }

    public int getColumn() {
        return mColumn;
    }

    public boolean isInterstitial() {
        return mInterstitial;
    }

    public boolean isBanner() {
        return mBanner;
    }

    public boolean isShuffle() {
        return mShuffle;
    }

    public Class<? extends Fragment> getFragment() {
        return FragmentViewPager.class;
    }

}
