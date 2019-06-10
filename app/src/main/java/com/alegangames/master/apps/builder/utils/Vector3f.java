package com.alegangames.master.apps.builder.utils;

public class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distSquared(final Vector3f vector3f) {
        return Math.pow(vector3f.x - this.x, 2.0) + Math.pow(vector3f.y - this.y, 2.0) + Math.pow(vector3f.z - this.z, 2.0);
    }

    public int getBlockX() {
        return (int) this.x;
    }

    public int getBlockY() {
        return (int) this.y;
    }

    public int getBlockZ() {
        return (int) this.z;
    }

    public float getX() {
        return this.x;
    }

    public Vector3f setX(final float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return this.y;
    }

    public Vector3f setY(final float y) {
        this.y = y;
        return this;
    }

    public float getZ() {
        return this.z;
    }

    public Vector3f setZ(final float z) {
        this.z = z;
        return this;
    }
}
