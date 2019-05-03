package com.remaistudio.master.apps.builder.entities;

import java.io.Serializable;

public class Minecraft implements Serializable {
    private String author;
    private String category;
    private String desc;
    private int download;
    private String file_url;
    private String img1_url;
    private String img2_url;
    private String img3_url;
    private String name;
    private String premium;
    private String thumb_url;

    public Minecraft(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i, String str10) {
        this.name = str;
        this.author = str2;
        this.category = str3;
        this.desc = str4;
        this.thumb_url = str5;
        this.img1_url = str6;
        this.img2_url = str7;
        this.img3_url = str8;
        this.file_url = str9;
        this.download = i;
        this.premium = str10;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String str) {
        this.author = str;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public int getDownload() {
        return this.download;
    }

    public void setDownload(int i) {
        this.download = i;
    }

    public String getFile_url() {
        return this.file_url;
    }

    public void setFile_url(String str) {
        this.file_url = str;
    }

    public String getImg1_url() {
        return this.img1_url;
    }

    public void setImg1_url(String str) {
        this.img1_url = str;
    }

    public String getImg2_url() {
        return this.img2_url;
    }

    public void setImg2_url(String str) {
        this.img2_url = str;
    }

    public String getImg3_url() {
        return this.img3_url;
    }

    public void setImg3_url(String str) {
        this.img3_url = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPremium() {
        return this.premium;
    }

    public void setPremium(String str) {
        this.premium = str;
    }

    public String getThumb_url() {
        return this.thumb_url;
    }

    public void setThumb_url(String str) {
        this.thumb_url = str;
    }
}
