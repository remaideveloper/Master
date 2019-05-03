package com.remaistudio.master.apps.builder.utils;

import com.remaistudio.master.apps.builder.litl.leveldb.DB;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LevelDBConverter {
    private static byte[] bytes(final String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] getChunkData(final DB db, final int n, final int n2) {
        return db.get(new DBKey(n, n2, 48).toBytes());
    }

    public static DB openDatabase(final File file) throws IOException {
        new File(file, "LOCK");
        int n = 0;
        while (true) {
            if (n < 10) {
                try {
                    final DB db = new DB(file);
                    db.open();
                    return db;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                Thread.sleep(200L);
                ++n;
            } catch (Exception ex2) {
            }
        }
    }

    public static void writeChunkData(final DB db, final int n, final int n2, final byte[] array) {
        db.put(new DBKey(n, n2, 48).toBytes(), array);
    }
}
