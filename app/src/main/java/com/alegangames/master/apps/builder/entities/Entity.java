package com.alegangames.master.apps.builder.entities;

import com.alegangames.master.apps.builder.utils.Vector3f;

public class Entity {
    private short air = (short) 300;
    private float fallDistance;
    private short fire;
    private Vector3f location = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f motion = new Vector3f(0.0f, 0.0f, 0.0f);
    private boolean onGround = false;
    private float pitch;
    private Entity riding = null;
    private int typeId = 0;
    private float yaw;

    public short getAirTicks() {
        return this.air;
    }

    public void setAirTicks(short s) {
        this.air = s;
    }

    public int getEntityTypeId() {
        return this.typeId;
    }

    public void setEntityTypeId(int i) {
        this.typeId = i;
    }

    public float getFallDistance() {
        return this.fallDistance;
    }

    public void setFallDistance(float f) {
        this.fallDistance = f;
    }

    public short getFireTicks() {
        return this.fire;
    }

    public void setFireTicks(short s) {
        this.fire = s;
    }

    public Vector3f getLocation() {
        return this.location;
    }

    public void setLocation(Vector3f vector3f) {
        this.location = vector3f;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }

    public Entity getRiding() {
        return this.riding;
    }

    public void setRiding(Entity entity) {
        this.riding = entity;
    }

    public Vector3f getVelocity() {
        return this.motion;
    }

    public void setVelocity(Vector3f vector3f) {
        this.motion = vector3f;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float f) {
        this.yaw = f;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean z) {
        this.onGround = z;
    }
}
