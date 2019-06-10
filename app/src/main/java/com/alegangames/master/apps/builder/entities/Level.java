package com.alegangames.master.apps.builder.entities;

public class Level {
    private long dayCycleStopTime = -1;
    private int dimension = 0;
    private int gameType;
    private int generator = 0;
    private long lastPlayed;
    private String levelName;
    private int platform;
    private Player player;
    private long randomSeed;
    private long sizeOnDisk;
    private boolean spawnMobs = true;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int storageVersion;
    private long time;

    public long getDayCycleStopTime() {
        return this.dayCycleStopTime;
    }

    public void setDayCycleStopTime(long j) {
        this.dayCycleStopTime = j;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int i) {
        this.dimension = i;
    }

    public int getGameType() {
        return this.gameType;
    }

    public void setGameType(int i) {
        this.gameType = i;
    }

    public int getGenerator() {
        return this.generator;
    }

    public void setGenerator(int i) {
        this.generator = i;
    }

    public long getLastPlayed() {
        return this.lastPlayed;
    }

    public void setLastPlayed(long j) {
        this.lastPlayed = j;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void setLevelName(String str) {
        this.levelName = str;
    }

    public int getPlatform() {
        return this.platform;
    }

    public void setPlatform(int i) {
        this.platform = i;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getRandomSeed() {
        return this.randomSeed;
    }

    public void setRandomSeed(long j) {
        this.randomSeed = j;
    }

    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    public void setSizeOnDisk(long j) {
        this.sizeOnDisk = j;
    }

    public boolean getSpawnMobs() {
        return this.spawnMobs;
    }

    public void setSpawnMobs(boolean z) {
        this.spawnMobs = z;
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

    public int getStorageVersion() {
        return this.storageVersion;
    }

    public void setStorageVersion(int i) {
        this.storageVersion = i;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }
}
