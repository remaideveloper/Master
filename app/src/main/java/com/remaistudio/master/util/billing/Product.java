package com.remaistudio.master.util.billing;

import com.remaistudio.master.R;


public class Product {

    private String id;
    private String description;
    private String subDescription;
    private int imageId = R.mipmap.ic_launcher_round;
    private int amount;
    private int bonus;


    public Product(String id, int imageId, int amount) {
        this(id, "", "", imageId, amount, 0);
    }

    public Product(String id, String description, String subDescription, int imageId, int amount, int bonus) {
        this.id = id;
        this.description = description;
        this.subDescription = subDescription;
        this.imageId = imageId;
        this.amount = amount;
        this.bonus = bonus;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public int getImageId() {
        return imageId;
    }

    public int getAmount() {
        return amount;
    }

    public int getBonus() {
        return bonus;
    }
}
