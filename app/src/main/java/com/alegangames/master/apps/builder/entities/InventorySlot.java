package com.alegangames.master.apps.builder.entities;

public class InventorySlot {
    private ItemStack contents;
    private byte slot;

    public InventorySlot(byte b, ItemStack itemStack) {
        this.slot = b;
        this.contents = itemStack;
    }

    public ItemStack getContents() {
        return this.contents;
    }

    public void setContents(ItemStack itemStack) {
        this.contents = itemStack;
    }

    public byte getSlot() {
        return this.slot;
    }

    public void setSlot(byte b) {
        this.slot = b;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Slot ");
        stringBuilder.append(getSlot());
        stringBuilder.append(": Type: ");
        stringBuilder.append(this.contents.getTypeId());
        stringBuilder.append("; Damage: ");
        stringBuilder.append(this.contents.getDurability());
        stringBuilder.append("; Amount: ");
        stringBuilder.append(this.contents.getCount());
        return stringBuilder.toString();
    }
}
