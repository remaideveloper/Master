package com.alegangames.master.apps.builder.utils;

import android.app.Activity;
import android.util.Log;

import com.alegangames.master.apps.builder.litl.leveldb.DB;
import com.alegangames.master.events.BuildingProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChunkManager implements AreaChunkAccess {

    private static final String TAG = ChunkManager.class.getSimpleName();

    private static final int ADDITIONAL_LENGTH = 34048;
    private static final int BLOCKLIGHT_LENGTH = 16384;
    private static final int BLOCK_LENGTH = 32768;
    private static final int DIRTY_LENGTH = 256;
    private static final int GRASS_LENGTH = 1024;
    private static final int LIGHT_LENGTH = 32768;
    private static final int METADATA_LENGTH = 16384;
    private static final int SKYLIGHT_LENGTH = 16384;
    private static final int WORLD_HEIGHT = 128;
    private static final int WORLD_LENGTH = 256;
    private static final int WORLD_WIDTH = 256;
    Chunk.Key tmpKey;
    private Map<Chunk.Key, Chunk> chunks;
    private DB db;
    private File dbFile;
    private int highestY;
    private boolean isConflict;
    private Chunk lastChunk;
    private Chunk.Key lastKey;
    private Activity mActivity;
    private Map<Chunk.Key, byte[]> tempDataList;

    public ChunkManager(final File dbFile, final Activity mActivity) {
        this.highestY = 0;
        this.isConflict = false;
        this.chunks = new HashMap<>();
        this.tempDataList = new HashMap<>();
        this.lastKey = null;
        this.lastChunk = null;
        this.tmpKey = new Chunk.Key(0, 0);
        this.dbFile = dbFile;
        this.mActivity = mActivity;
        this.openDatabase();
    }

    private byte[] generateGrassData(final float n) {
        final byte[] array = new byte[83200];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 128; ++j) {
                if (j < n) {
                    array[i * 128 + j] = 2;
                    if (j < n - 1.0f) {
                        array[i * 128 + j] = 3;
                    }
                    if (j < n - 4.0f) {
                        array[i * 128 + j] = 1;
                    }
                } else {
                    array[i * 128 + j] = 0;
                }
            }
        }
        return array;
    }

    private void refillChunk() {
        Log.d(TAG, "refillChunk");
        byte[] array = new byte[32768];
        byte[] array2 = new byte[34048];
        int n = 0;

        long size = this.tempDataList.size();
        long current = 0;
        int progress = 0;
        int currentProgress = 0;

        for (Map.Entry<Chunk.Key, byte[]> keyEntry : this.tempDataList.entrySet()) {
            byte[] array3 = keyEntry.getValue();
            if (array3 != null) {
                int n2 = 0;
                int n3;
                for (int i = 0; i < 32768; ++i, n2 = n3) {
                    n3 = n2;
                    if (array3[i] == 0) {
                        n3 = n2 + 1;
                    }
                }
                if (n2 <= n) {
                    continue;
                }
                for (int j = 0; j < 32768; ++j) {
                    array[j] = array3[49152 + j];
                    if (array[j] == 0) {
                        array[j] = -1;
                    }
                }
                int n4 = 0;
                while (true) {
                    n = n2;
                    if (n4 >= 34048) {
                        break;
                    }
                    array2[n4] = array3[49152 + n4];
                    if (array2[n4] == 0) {
                        array2[n4] = 127;
                    }
                    ++n4;
                }
            }

            //Post event with progress
            current++;
            currentProgress = (int)((current * 10.0f)/ size);
            if(currentProgress > progress) {
                progress = currentProgress;
                EventBus.getDefault().post(new BuildingProgressEvent(progress + 70));
            }
        }

        size = this.chunks.entrySet().size();
        current = 0;
        progress = 0;

        for (Map.Entry<Chunk.Key, Chunk> entry : this.chunks.entrySet()) {
            for (Map.Entry<Chunk.Key, byte[]> entry2 : this.tempDataList.entrySet()) {
                Chunk.Key key = entry.getKey();
                Chunk.Key key2 = entry2.getKey();
                byte[] array4 = entry2.getValue();
                if (array4 != null && key.getX() == key2.getX() && key.getZ() == key2.getZ()) {
                    final Chunk chunk = new Chunk(key2.getX(), key2.getZ());
                    chunk.loadFromByteArray(array4);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            final int highestBlockYAt = chunk.getHighestBlockYAt(k, l);
                            if (highestBlockYAt > this.highestY) {
                                this.highestY = highestBlockYAt;
                            }
                        }
                    }
                    int highestY = (int) Float.parseFloat(MinecraftWorldUtil.buildY);
                    if (this.highestY > highestY) {
                        this.highestY = highestY;
                    }
                    byte[] saveToByteArray = entry.getValue().saveToByteArray();
                    for (int n5 = 0; n5 < 256; ++n5) {
                        for (int n6 = 0; n6 < 128; ++n6) {
                            if (n6 >= Integer.valueOf(MinecraftWorldUtil.buildY)) {
                                final int n7 = n5 * 128 + n6;
                                if (array4[n7] != 0 && saveToByteArray[n7] == 0) {
                                    this.isConflict = true;
                                }
                                array4[n7] = saveToByteArray[n7];
                            }
                        }
                    }

                    for (int n8 = 32768; n8 < 49152; ++n8) {
                        if (saveToByteArray[n8] != 0) {
                            array4[n8] = saveToByteArray[n8];
                        }
                    }

                    System.arraycopy(array, 0, array4, 49152, 32768);

                    Chunk chunk2 = new Chunk(key.getX(), key.getZ());
                    chunk2.loadFromByteArray(array4);
                    chunk2.needsSaving = true;
                    this.chunks.put(new Chunk.Key(key.getX(), key.getZ()), chunk2);
                    System.err.println("refilled:" + key.getX() + "-" + key.getZ());
                }
            }

            //Post event with progress
            current++;
            currentProgress = (int)((current * 10.0f)/ size);
            if(currentProgress > progress) {
                progress = currentProgress;
                EventBus.getDefault().post(new BuildingProgressEvent(progress + 80));
            }
        }

        size = this.chunks.entrySet().size();
        current = 0;
        progress = 0;

        for (Map.Entry<Chunk.Key, Chunk> entry3 : chunks.entrySet()) {
            for (Map.Entry<Chunk.Key, byte[]> entry4 : tempDataList.entrySet()) {

                Chunk.Key key3 = entry3.getKey();
                Chunk.Key key4 = entry4.getKey();

                if ((entry4.getValue() == null || isConflict) && key3.getX() == key4.getX() && key3.getZ() == key4.getZ()) {

                    byte[] generateGrassData = null;
                    byte[] saveToByteArray2 = null;

                    while (true) {

                        try {

                            highestY = Integer.valueOf(MinecraftWorldUtil.buildY);
                            generateGrassData = generateGrassData(highestY);
                            saveToByteArray2 = entry3.getValue().saveToByteArray();

                            for (int n10 = 0; n10 <= 256; n10++) {

                                for (int n11 = 0; n11 < 128; n11++) {
                                    if (n11 >= Integer.valueOf(MinecraftWorldUtil.buildY)) {
                                        final int n12 = n10 * 128 + n11;
                                        generateGrassData[n12] = saveToByteArray2[n12];
                                    }
                                }

                            }

                        } catch (NumberFormatException ex) {
                            Log.d(TAG, "refillChunk: NumberFormatException");
                            highestY = 50;
                            ex.printStackTrace();
                            continue;
                        }

                        break;

                    }

                    for (int n13 = 32768; n13 < 49152; ++n13) {
                        if (saveToByteArray2 != null && saveToByteArray2[n13] != 0) {
                            generateGrassData[n13] = saveToByteArray2[n13];
                        }
                    }

                    for (int n14 = 0; n14 < 34048; ++n14) {
                        if (generateGrassData != null) {
                            generateGrassData[49152 + n14] = array2[n14];
                        }
                    }

                    try {
                        Chunk chunk3 = new Chunk(key3.getX(), key3.getZ());
                        Log.d(TAG, "refillChunk: chunk3");
                        chunk3.loadFromByteArray(generateGrassData);
                        chunk3.needsSaving = true;
                        this.chunks.put(new Chunk.Key(key3.getX(), key3.getZ()), chunk3);
                        System.err.println("refilled:" + key3.getX() + "-" + key3.getZ());
                    } catch (Exception e) {
                        Log.d(TAG, "refillChunk: Exception");
                        e.printStackTrace();
                    }
                }
            }

            //post event with progress
            current++;
            currentProgress = (int)((current * 10.0f)/ size);
            if(currentProgress > progress) {
                progress = currentProgress;
                EventBus.getDefault().post(new BuildingProgressEvent(progress + 90));
            }
        }


    }

    public void close() throws IOException {
        this.closeDatabase();
    }

    public void closeDatabase() {
        if(db != null) {
            this.db.close();
            this.db = null;
        }
    }

    @Override
    public int getBlockData(final int n, final int n2, final int n3) {
        return this.getChunk(n >> 4, n3 >> 4).getBlockData(n & 0xF, n2, n3 & 0xF);
    }

    @Override
    public int getBlockTypeId(final int n, final int n2, final int n3) {
        return this.getChunk(n >> 4, n3 >> 4).getBlockTypeId(n & 0xF, n2, n3 & 0xF);
    }

    @Override
    public Chunk getChunk(final int x, final int z) {
        if (this.lastKey != null && this.lastKey.getX() == x && this.lastKey.getZ() == z) {
            return this.lastChunk;
        }
        Chunk.Key lastKey;
        if (this.lastKey == null) {
            lastKey = new Chunk.Key(x, z);
            this.lastKey = lastKey;
        } else {
            lastKey = this.lastKey;
            lastKey.setX(x);
            lastKey.setZ(z);
        }
        Chunk loadChunk;
        if ((loadChunk = this.chunks.get(lastKey)) == null) {
            loadChunk = this.loadChunk(lastKey);
            System.err.println("load chunk from chunkS:" + lastKey.getX() + ":" + lastKey.getZ());
        }
        return this.lastChunk = loadChunk;
    }

    public int getHighestBlockYAt(final int n, final int n2) {
        if (n >= 256 || n2 >= 256) {
            return 0;
        }
        return this.getChunk(n >> 4, n2 >> 4).getHighestBlockYAt(n & 0xF, n2 & 0xF);
    }

    public Chunk loadChunk(Chunk.Key key) {
        Chunk chunk = new Chunk(key.getX(), key.getZ());
        byte[] chunkData = LevelDBConverter.getChunkData(this.db, key.getX(), key.getZ());
        this.tempDataList.put(new Chunk.Key(key), chunkData);
        if (chunkData != null) {
            chunk.loadFromByteArray(chunkData);
            System.err.println("Chunk data not null:" + key.getX() + "-" + key.getZ() + ":" + chunkData.length);
        } else {
            System.err.println("Chunk data is null:" + key.getX() + "-" + key.getZ());
        }
        this.chunks.put(new Chunk.Key(key), chunk);
        return chunk;
    }

    public void openDatabase() {
        if (this.db != null) {
            return;
        }
        try {
            this.db = LevelDBConverter.openDatabase(this.dbFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int saveAll() {
        Log.d(TAG, "saveAll");

        refillChunk();
        int n = 0;
        System.err.println("Save chunk...");
        System.err.println("ChunkS size:" + this.chunks.size());

        Log.d(TAG, "saveAll for");
        for (Map.Entry<Chunk.Key, Chunk> entry : this.chunks.entrySet()) {
            Log.d(TAG, "saveAll: entry.getValue() " + entry.getValue());
            Chunk.Key key = entry.getKey();
            Chunk chunk = entry.getValue();

            if (key.getX() != chunk.x || key.getZ() != chunk.z) {
                Log.d(TAG, "saveAll: AssertionError");
                throw new AssertionError("WTF: key x = " + key.getX() + " z = " + key.getZ() + " chunk x=" + chunk.x + " chunk z=" + chunk.z);
            }
            if (!chunk.needsSaving) {
                Log.d(TAG, "saveAll: !chunk.needsSaving");
                continue;
            }

            this.saveChunk(chunk);
            ++n;
        }
        Log.d(TAG, "saveAll return");
        return n;
    }

    protected void saveChunk(final Chunk chunk) {
        Log.d(TAG, "saveChunk");
        LevelDBConverter.writeChunkData(this.db, chunk.x, chunk.z, chunk.saveToByteArray());
    }

    @Override
    public void setBlockData(final int n, final int n2, final int n3, final int n4) {
        this.getChunk(n >> 4, n3 >> 4).setBlockData(n & 0xF, n2, n3 & 0xF, n4);
    }

    @Override
    public void setBlockTypeId(final int n, final int n2, final int n3, final int n4) {
        this.getChunk(n >> 4, n3 >> 4).setBlockTypeId(n & 0xF, n2, n3 & 0xF, n4);
    }

    public void unloadChunks(final boolean b) {
        if (b) {
            this.saveAll();
        }
        this.chunks.clear();
    }
}
