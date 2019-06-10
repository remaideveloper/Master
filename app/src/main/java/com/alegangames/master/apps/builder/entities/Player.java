package com.alegangames.master.apps.builder.entities;

import java.util.List;

public class Player extends LivingEntity {
    private PlayerAbilities abilities = new PlayerAbilities();
    private List<ItemStack> armorSlots;
    private int bedPositionX = 0;
    private int bedPositionY = 0;
    private int bedPositionZ = 0;
    private int dimension;
    private List<InventorySlot> inventory;
    private int score;
    private short sleepTimer = (short) 0;
    private boolean sleeping = false;
    private int spawnX = 0;
    private int spawnY = 64;
    private int spawnZ = 0;

    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    public void setAbilities(PlayerAbilities playerAbilities) {
        this.abilities = playerAbilities;
    }

    public List<ItemStack> getArmor() {
        return this.armorSlots;
    }

    public void setArmor(List<ItemStack> list) {
        this.armorSlots = list;
    }

    public int getBedPositionX() {
        return this.bedPositionX;
    }

    public void setBedPositionX(int i) {
        this.bedPositionX = i;
    }

    public int getBedPositionY() {
        return this.bedPositionY;
    }

    public void setBedPositionY(int i) {
        this.bedPositionY = i;
    }

    public int getBedPositionZ() {
        return this.bedPositionZ;
    }

    public void setBedPositionZ(int i) {
        this.bedPositionZ = i;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int i) {
        this.dimension = i;
    }

    public List<InventorySlot> getInventory() {
        return this.inventory;
    }

    public void setInventory(List<InventorySlot> list) {
        this.inventory = list;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public short getSleepTimer() {
        return this.sleepTimer;
    }

    public void setSleepTimer(short s) {
        this.sleepTimer = s;
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public void setSpawnX(int i) {
        this.spawnX = i;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public void setSpawnY(int i) {
        this.spawnY = i;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public void setSpawnZ(int i) {
        this.spawnZ = i;
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    public void setSleeping(boolean z) {
        this.sleeping = z;
    }
}
