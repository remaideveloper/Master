package com.alegangames.master.model.enums;

public enum ItemEnum {

    //PROMO(0, "Promo"),
    MAPS(0, "Maps"),
    MODS(1, "Mods"),
    BUILDING(2, "Building"),
    CRAFT(3, "Craft"),
    SKINS(4, "Skins"),
    EDITOR(5, "Editor"),
    STEALER(6, "Stealer"),
    PACKS(7, "Packs"),
    SEEDS(8, "Seeds"),
    SERVERS(9, "Servers"),
    TEXTURES(10, "Textures"),
    WALLPAPERS(11, "Wallpapers");


    ItemEnum(int position, String title) {
        mPosition = position;
        mTitle = title;
    }

    private int mPosition;
    private String mTitle;

    public int getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }
}
