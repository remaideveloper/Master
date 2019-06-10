package com.alegangames.master.apps.builder.utils;

public interface AreaBlockAccess {
    int getBlockData(final int p0, final int p1, final int p2);

    int getBlockTypeId(final int p0, final int p1, final int p2);

    void setBlockData(final int p0, final int p1, final int p2, final int p3);

    void setBlockTypeId(final int p0, final int p1, final int p2, final int p3);
}
