package com.alegangames.master.apps.builder.entities;

public class World {
    private String capacity;
    private String date;
    private String folder_name;
    private String name;

    public World() {
    }

    public World(String str, String str2, String str3, String str4) {
        this.name = str;
        this.date = str2;
        this.capacity = str3;
        this.folder_name = str4;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setCapacity(String str) {
        this.capacity = str;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public String getFolder_name() {
        return this.folder_name;
    }

    public void setFolder_name(String str) {
        this.folder_name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }
}
