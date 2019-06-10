package com.alegangames.master.apps.builder.utils;


public class CuboidRegion {
    public int height;
    public int length;
    public int width;
    public int x;
    public int y;
    public int z;

    public CuboidRegion(final int x, final int y, final int z, final int width, final int height, final int length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public CuboidRegion(final CuboidRegion cuboidRegion) {
        this(cuboidRegion.x, cuboidRegion.y, cuboidRegion.z, cuboidRegion.width, cuboidRegion.height, cuboidRegion.length);
    }

    public CuboidRegion(Vector3f vector3f, Vector3f vector3f2) {
        this((int) vector3f.getX(), (int) vector3f.getY(), (int) vector3f.getZ(), (int) vector3f2.getX(), (int) vector3f2.getY(), (int) vector3f2.getZ());
    }

    public static CuboidRegion fromPoints(Vector3f vector3f, Vector3f vector3f2) {
        float n;
        if (vector3f.getX() < vector3f2.getX()) {
            n = vector3f.getX();
        } else {
            n = vector3f2.getX();
        }
        final int n2 = (int) n;
        float n3;
        if (vector3f.getX() >= vector3f2.getX()) {
            n3 = vector3f.getX();
        } else {
            n3 = vector3f2.getX();
        }
        final int n4 = (int) n3;
        float n5;
        if (vector3f.getY() < vector3f2.getY()) {
            n5 = vector3f.getY();
        } else {
            n5 = vector3f2.getY();
        }
        final int n6 = (int) n5;
        float n7;
        if (vector3f.getY() >= vector3f2.getY()) {
            n7 = vector3f.getY();
        } else {
            n7 = vector3f2.getY();
        }
        final int n8 = (int) n7;
        float n9;
        if (vector3f.getZ() < vector3f2.getZ()) {
            n9 = vector3f.getZ();
        } else {
            n9 = vector3f2.getZ();
        }
        final int n10 = (int) n9;
        float n11;
        if (vector3f.getZ() >= vector3f2.getZ()) {
            n11 = vector3f.getZ();
        } else {
            n11 = vector3f2.getZ();
        }
        return new CuboidRegion(n2, n6, n10, n4 - n2 + 1, n8 - n6 + 1, (int) n11 - n10 + 1);
    }

    public boolean contains(final CuboidRegion cuboidRegion) {
        return cuboidRegion.x >= this.x && cuboidRegion.y >= this.y && cuboidRegion.z >= this.z && cuboidRegion.x + cuboidRegion.width <= this.x + this.width && cuboidRegion.y + cuboidRegion.height <= this.y + this.height && cuboidRegion.z + cuboidRegion.length <= this.z + this.length;
    }

    public CuboidRegion createIntersection(final CuboidRegion cuboidRegion) {
        int n;
        if (this.x > cuboidRegion.x) {
            n = this.x;
        } else {
            n = cuboidRegion.x;
        }
        int n2;
        if (this.y > cuboidRegion.y) {
            n2 = this.y;
        } else {
            n2 = cuboidRegion.y;
        }
        int n3;
        if (this.z > cuboidRegion.z) {
            n3 = this.z;
        } else {
            n3 = cuboidRegion.z;
        }
        int n4 = this.x + this.width;
        int n5 = this.y + this.height;
        int n6 = this.z + this.length;
        final int n7 = cuboidRegion.x + cuboidRegion.width;
        final int n8 = cuboidRegion.y + cuboidRegion.height;
        final int n9 = cuboidRegion.z + cuboidRegion.length;
        if (n4 >= n7) {
            n4 = n7;
        }
        if (n5 >= n8) {
            n5 = n8;
        }
        if (n6 >= n9) {
            n6 = n9;
        }
        return new CuboidRegion(n, n2, n3, n4 - n, n5 - n2, n6 - n3);
    }

    public int getBlockCount() {
        return this.width * this.height * this.length;
    }

    public Vector3f getPosition() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public Vector3f getSize() {
        return new Vector3f(this.width, this.height, this.length);
    }

    public boolean isValid() {
        return this.width >= 0 && this.height >= 0 && this.length >= 0;
    }
}
