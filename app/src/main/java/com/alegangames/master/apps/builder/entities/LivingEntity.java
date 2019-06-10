package com.alegangames.master.apps.builder.entities;

public class LivingEntity extends Entity {
    private short attackTime;
    private short deathTime;
    private short health = ((short) getMaxHealth());
    private short hurtTime;

    public int getMaxHealth() {
        return 20;
    }

    public short getAttackTime() {
        return this.attackTime;
    }

    public void setAttackTime(short s) {
        this.attackTime = s;
    }

    public short getDeathTime() {
        return this.deathTime;
    }

    public void setDeathTime(short s) {
        this.deathTime = s;
    }

    public short getHealth() {
        return this.health;
    }

    public void setHealth(short s) {
        this.health = s;
    }

    public short getHurtTime() {
        return this.hurtTime;
    }

    public void setHurtTime(short s) {
        this.hurtTime = s;
    }
}
