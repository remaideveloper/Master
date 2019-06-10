package com.alegangames.master.apps.builder.utils;

import java.io.ByteArrayOutputStream;

class RegionFileChunkBuffer extends ByteArrayOutputStream {
    final RegionFile regionFile;
    private int chunkX;
    private int chunkZ;

    public RegionFileChunkBuffer(RegionFile regionfile, int i, int j) {
        super(8096);
        regionFile = regionfile;
        chunkX = i;
        chunkZ = j;
    }

    public void close() {
        regionFile.write(chunkX, chunkZ, buf, count);
    }
}