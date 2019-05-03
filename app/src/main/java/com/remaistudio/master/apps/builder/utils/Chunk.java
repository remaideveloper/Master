package com.remaistudio.master.apps.builder.utils;

public class Chunk {
    private static int HEIGHT;
    private static int LENGTH;
    private static int WIDTH;

    static {
        Chunk.WIDTH = 16;
        Chunk.HEIGHT = 128;
        Chunk.LENGTH = 16;
    }

    public final int x;
    public final int z;
    public byte[] data;
    public boolean needsSaving;
    private byte[] blockLight;
    private byte[] blocks;
    private byte[] dirtyTable;
    private byte[] grassColor;
    private boolean hasFilledDirtyTable;
    private byte[] metaData;
    private byte[] skyLight;

    public Chunk(final int x, final int z) {
        this.needsSaving = false;
        this.hasFilledDirtyTable = false;
        this.x = x;
        this.z = z;
        this.blocks = new byte[Chunk.WIDTH * Chunk.HEIGHT * Chunk.LENGTH];
        this.metaData = new byte[Chunk.WIDTH * Chunk.HEIGHT * Chunk.LENGTH >> 1];
        this.blockLight = new byte[Chunk.WIDTH * Chunk.HEIGHT * Chunk.LENGTH >> 1];
        this.skyLight = new byte[Chunk.WIDTH * Chunk.HEIGHT * Chunk.LENGTH >> 1];
        this.dirtyTable = new byte[Chunk.WIDTH * Chunk.LENGTH];
        this.grassColor = new byte[Chunk.WIDTH * Chunk.LENGTH * 4];
    }

    private static int getOffset(final int n, final int n2, final int n3) {
        return Chunk.HEIGHT * n * Chunk.LENGTH + Chunk.HEIGHT * n3 + n2;
    }

    public int countDiamonds() {
        int n = 0;
        int n2;
        for (int i = 0; i < this.blocks.length; ++i, n = n2) {
            n2 = n;
            if (this.blocks[i] == 56) {
                n2 = n + 1;
            }
        }
        return n;
    }

    public boolean dirtyTableIsReallyGross() {
        for (int i = 0; i < this.dirtyTable.length; ++i) {
            if (this.dirtyTable[i] != 0) {
                return true;
            }
        }
        return false;
    }

    public int getBlockData(int offset, int n, final int n2) {
        if (offset >= Chunk.WIDTH || n >= Chunk.HEIGHT || n2 >= Chunk.LENGTH) {
            return 0;
        }
        offset = getOffset(offset, n, n2);
        n = this.metaData[offset >> 1];
        if (offset % 2 == 1) {
            return n >> 4 & 0xF;
        }
        return n & 0xF;
    }

    public int getBlockTypeId(int n, int n2, final int n3) {
        if (n >= Chunk.WIDTH || n2 >= Chunk.HEIGHT || n3 >= Chunk.LENGTH) {
            n = 0;
        } else {
            n2 = this.blocks[getOffset(n, n2, n3)];
            if ((n = n2) < 0) {
                return n2 + 256;
            }
        }
        return n;
    }

    public int getHighestBlockYAt(final int n, final int n2) {
        for (int i = 127; i >= 0; --i) {
            if (this.getBlockTypeId(n, i, n2) != 0 && this.getBlockTypeId(n, i, n2) == 2) {
                return i;
            }
        }
        return 0;
    }

    public void loadFromByteArray(final byte[] array) {
        System.arraycopy(array, 0, this.blocks, 0, this.blocks.length);
        final int n = this.blocks.length;
        System.arraycopy(array, n, this.metaData, 0, this.metaData.length);
        final int n2 = n + this.metaData.length;
        System.arraycopy(array, n2, this.skyLight, 0, this.skyLight.length);
        final int n3 = n2 + this.skyLight.length;
        System.arraycopy(array, n3, this.blockLight, 0, this.blockLight.length);
        final int n4 = n3 + this.blockLight.length;
        System.arraycopy(array, n4, this.dirtyTable, 0, this.dirtyTable.length);
        System.arraycopy(array, n4 + this.dirtyTable.length, this.grassColor, 0, this.grassColor.length);
    }

    public byte[] saveToByteArray() {
        final byte[] array = new byte[this.blocks.length + this.metaData.length + this.skyLight.length + this.blockLight.length + this.dirtyTable.length + this.grassColor.length];
        System.arraycopy(this.blocks, 0, array, 0, this.blocks.length);
        final int n = this.blocks.length;
        System.arraycopy(this.metaData, 0, array, n, this.metaData.length);
        final int n2 = n + this.metaData.length;
        System.arraycopy(this.skyLight, 0, array, n2, this.skyLight.length);
        final int n3 = n2 + this.skyLight.length;
        System.arraycopy(this.blockLight, 0, array, n3, this.blockLight.length);
        final int n4 = n3 + this.blockLight.length;
        System.arraycopy(this.dirtyTable, 0, array, n4, this.dirtyTable.length);
        System.arraycopy(this.grassColor, 0, array, n4 + this.dirtyTable.length, this.grassColor.length);
        return array;
    }

    public void setBlockData(final int n, final int n2, final int n3, final int n4) {
        if (n >= Chunk.WIDTH || n2 >= Chunk.HEIGHT || n3 >= Chunk.LENGTH) {
            return;
        }
        this.setBlockDataNoDirty(n, n2, n3, n4);
        this.setDirtyTable(n, n2, n3);
        this.setNeedsSaving(true);
    }

    public void setBlockDataNoDirty(int offset, int n, final int n2, final int n3) {
        offset = getOffset(offset, n, n2);
        n = this.metaData[offset >> 1];
        if (offset % 2 == 1) {
            this.metaData[offset >> 1] = (byte) (n3 << 4 | (n & 0xF));
            return;
        }
        this.metaData[offset >> 1] = (byte) ((n & 0xF0) | (n3 & 0xF));
    }

    public void setBlockTypeId(final int n, final int n2, final int n3, final int n4) {
        if (n >= Chunk.WIDTH || n2 >= Chunk.HEIGHT || n3 >= Chunk.LENGTH) {
            return;
        }
        this.setBlockTypeIdNoDirty(n, n2, n3, n4);
        this.setDirtyTable(n, n2, n3);
        this.setNeedsSaving(true);
    }

    public void setBlockTypeIdNoDirty(final int n, final int n2, final int n3, final int n4) {
        this.blocks[getOffset(n, n2, n3)] = (byte) n4;
    }

    public void setDirtyTable(int i, final int n, final int n2) {
        if (this.hasFilledDirtyTable) {
            return;
        }
        for (i = 0; i < 256; ++i) {
            this.dirtyTable[i] = -1;
        }
        this.hasFilledDirtyTable = true;
    }

    public void setNeedsSaving(final boolean needsSaving) {
        this.needsSaving = needsSaving;
    }

    public static final class Key {
        private int x;
        private int z;

        public Key(final int x, final int z) {
            this.x = x;
            this.z = z;
        }

        public Key(final Key key) {
            this(key.x, key.z);
        }

        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (!(o instanceof Key)) {
                    return false;
                }
                final Key key = (Key) o;
                if (key.getX() != this.x || key.getZ() != this.z) {
                    return false;
                }
            }
            return true;
        }

        public int getX() {
            return this.x;
        }

        public void setX(final int x) {
            this.x = x;
        }

        public int getZ() {
            return this.z;
        }

        public void setZ(final int z) {
            this.z = z;
        }

        @Override
        public int hashCode() {
            return (this.x + 31) * 31 + this.z;
        }
    }
}
