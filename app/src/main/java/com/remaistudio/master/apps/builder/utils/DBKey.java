package com.remaistudio.master.apps.builder.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DBKey {
    public static final int CHUNK = 48;
    public static final int ENTITY = 50;
    public static final int PLACEHOLDER = 118;
    public static final int TILE_ENTITY = 49;
    private int type;
    private int x;
    private int z;

    public DBKey() {
        this(0, 0, 0);
    }

    public DBKey(final int x, final int z, final int type) {
        this.x = x;
        this.z = z;
        this.type = type;
    }

    public DBKey(final DBKey dbKey) {
        this(dbKey.x, dbKey.z, dbKey.type);
    }

    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
        }
        if (!(o instanceof DBKey)) {
            b = false;
        }
        final DBKey dbKey = (DBKey) o;
        boolean b2 = b;
        if (dbKey.x == this.x) {
            b2 = b;
            if (dbKey.z == this.z) {
                b2 = b;
                if (dbKey.type == this.type) {
                    b2 = false;
                }
            }
        }
        return b2;
    }

    public void fromBytes(final byte[] array) {
        if (array.length <= 8) {
        }
        this.type = 0;
        this.x = (array[0] | array[1] << 8 | array[2] << 16 | array[3] << 24);
        this.z = (array[4] | array[5] << 8 | array[6] << 16 | array[7] << 24);
    }

    public int getType() {
        return this.type;
    }

    public DBKey setType(final int type) {
        this.type = type;
        return this;
    }

    public int getX() {
        return this.x;
    }

    public DBKey setX(final int x) {
        this.x = x;
        return this;
    }

    public int getZ() {
        return this.z;
    }

    public DBKey setZ(final int z) {
        this.z = z;
        return this;
    }

    @Override
    public int hashCode() {
        return ((this.x + 31) * 31 + this.z) * 31 + this.type;
    }

    public byte[] toBytes() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(9);
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(Integer.reverseBytes(this.x));
            dataOutputStream.writeInt(Integer.reverseBytes(this.z));
            dataOutputStream.writeByte(this.type);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.x + "_" + this.z + "_" + this.type;
    }
}
