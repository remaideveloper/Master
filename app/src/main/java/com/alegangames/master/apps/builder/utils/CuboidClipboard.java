package com.alegangames.master.apps.builder.utils;


import com.alegangames.master.events.BuildingProgressEvent;

import org.greenrobot.eventbus.EventBus;

public class CuboidClipboard implements AreaBlockAccess, SizeLimitedArea {
    public static final String TAG = CuboidClipboard.class.getSimpleName();
    public static final int AIR = 0;
    public byte[] blocks;
    public byte[] metaData;
    protected int height;
    protected int length;
    protected int width;

    public volatile boolean isCanceled;

    public CuboidClipboard(Vector3f vector3f) {
        this(vector3f, new byte[(int) (vector3f.getX() * vector3f.getY() * vector3f.getZ())], new byte[(int) (vector3f.getX() * vector3f.getY() * vector3f.getZ())]);
    }

    public CuboidClipboard(Vector3f vector3f, final byte[] blocks, final byte[] metaData) {
        this.width = (int) vector3f.getX();
        this.height = (int) vector3f.getY();
        this.length = (int) vector3f.getZ();
        this.blocks = blocks;
        this.metaData = metaData;
    }

    public void copy(AreaBlockAccess areaBlockAccess, Vector3f vector3f) {
        final int blockX = vector3f.getBlockX();
        final int blockY = vector3f.getBlockY();
        final int blockZ = vector3f.getBlockZ();
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.length; ++j) {
                for (int k = 0; k < this.height; ++k) {
                    final int blockTypeId = areaBlockAccess.getBlockTypeId(blockX + i, blockY + k, blockZ + j);
                    final int blockData = areaBlockAccess.getBlockData(blockX + i, blockY + k, blockZ + j);
                    this.setBlockTypeId(i, k, j, blockTypeId);
                    this.setBlockData(i, k, j, blockData);
                }
            }
        }
    }

    @Override
    public int getBlockData(final int n, final int n2, final int n3) {
        return this.metaData[this.getOffset(n, n2, n3)];
    }

    @Override
    public int getBlockTypeId(final int n, final int n2, final int n3) {
        return this.blocks[this.getOffset(n, n2, n3)];
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    public int getOffset(final int n, final int n2, final int n3) {
        return this.width * n2 * this.length + this.width * n3 + n;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public void place(AreaBlockAccess areaBlockAccess, Vector3f vector3f, final boolean b) {
        final int n = (int) vector3f.getX();
        final int n2 = (int) vector3f.getY();
        final int n3 = (int) vector3f.getZ();
        final int[] array2;
        final int[] array = array2 = new int[21];
        array2[0] = 36;
        array2[1] = 84;
        array2[2] = 119;
        array2[3] = 122;
        array2[4] = 130;
        array2[5] = 137;
        array2[6] = 138;
        array2[7] = 160;
        array2[8] = 166;
        array2[9] = 168;
        array2[10] = 169;
        array2[11] = 176;
        array2[12] = 177;
        array2[13] = 188;
        array2[14] = 189;
        array2[15] = 190;
        array2[16] = 191;
        array2[17] = 192;
        array2[18] = 252;
        array2[19] = 253;
        array2[20] = 254;
        long totalSize = width * length * height;
        long currentChunk = 0;
        int progress = 0;
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.length; ++j) {
                for (int k = 0; k < this.height; ++k) {
                    if(isCanceled) return;

                    final int abs = Math.abs(this.getBlockTypeId(i, k, j));
                    int n4;
                    if (abs == 27 || (n4 = abs) == 126) {
                        n4 = 158;
                    }
                    int n5;
                    if ((n5 = n4) == 100) {
                        n5 = 44;
                    }
                    int n6;
                    if ((n6 = n5) == 93) {
                        n6 = 180;
                    }
                    int n7;
                    if ((n7 = n6) == 101) {
                        n7 = 42;
                    }
                    int n8;
                    if ((n8 = n7) == 117) {
                        n8 = 4;
                    }
                    int n9;
                    if ((n8 > 199 && n8 < 243) || (n9 = n8) > 255) {
                        n9 = 1;
                    }
                    int n10;
                    for (int l = 0; l < array.length; ++l, n9 = n10) {
                        if ((n10 = n9) == array[l]) {
                            n10 = 1;
                        }
                    }
                    final int blockData = this.getBlockData(i, k, j);
                    areaBlockAccess.setBlockTypeId(n + i, n2 + k, n3 + j, n9);
                    areaBlockAccess.setBlockData(n + i, n2 + k, n3 + j, blockData);

                    currentChunk++;
                    int nextPercent =(int)((currentChunk * 70.0f) / totalSize);
                    if(nextPercent > progress) {
                        progress = nextPercent;
                        EventBus.getDefault().post(new BuildingProgressEvent(progress));
                    }
                }
            }
        }
    }

    @Override
    public void setBlockData(final int n, final int n2, final int n3, final int n4) {
        this.metaData[this.getOffset(n, n2, n3)] = (byte) n4;
    }

    @Override
    public void setBlockTypeId(final int n, final int n2, final int n3, final int n4) {
        this.blocks[this.getOffset(n, n2, n3)] = (byte) n4;
    }
}
