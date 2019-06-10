package com.alegangames.master.apps.builder.entities;

public class ItemStack {
    private int count;
    private short durability;
    private short id;

    public ItemStack(ItemStack itemStack) {
        this(itemStack.getTypeId(), itemStack.getDurability(), itemStack.getCount());
    }

    public ItemStack(short s, short s2, int i) {
        this.id = s;
        this.durability = s2;
        this.count = i;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public short getDurability() {
        return this.durability;
    }

    public void setDurability(short s) {
        this.durability = s;
    }

    public String getIconURL() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("item_");
        stringBuilder.append(String.valueOf(this.id));
        return stringBuilder.toString();
    }

    public short getTypeId() {
        return this.id;
    }

    public void setTypeId(short s) {
        this.id = s;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ItemStack: type=");
        stringBuilder.append(getTypeId());
        stringBuilder.append(", durability=");
        stringBuilder.append(getDurability());
        stringBuilder.append(", amount=");
        stringBuilder.append(getCount());
        return stringBuilder.toString();
    }
}
