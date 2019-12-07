package com.alegangames.master.model.enums;

import androidx.fragment.app.Fragment;

import com.alegangames.master.fragment.FragmentViewPager;

public enum JsonItemFragmentEnum {

    MAIN("Main", "main.txt", 2, false, false, false),
    SKINS("Skins", "sk.txt", 2, false, true, false),
    PACKS("Packs", "p.txt", 2, false, true, false),
    MODS("Mods", "mo.txt", 2, false, true, false),
    BUILDING("Building", "b.txt", 2, false, true, false),
    SEEDS("Seeds", "see.txt", 2, false, true, false),
    TEXTURES("Textures", "t.txt", 2, false, true, false),
    SERVERS("Servers", "ser.txt", 2, false, true, false),
    WALLPAPERS("Wallpapers", "w.txt", 2, false, true, false),
    MAPS("Maps", "ma.txt", 2, false, true, false);

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
