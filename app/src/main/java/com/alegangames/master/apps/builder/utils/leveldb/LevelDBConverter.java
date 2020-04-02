package com.alegangames.master.apps.builder.utils.leveldb;

import com.alegangames.master.apps.builder.entities.Level;
import com.alegangames.master.apps.builder.litl.leveldb.DB;
import com.crashlytics.android.Crashlytics;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LevelDBConverter {
    private static byte[] bytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DB openDatabase(File file) throws IOException {
        new File(file, "LOCK");
        try {
            DB localDB = new DB(file);
            localDB.open();
            return localDB;
        } catch (Exception localException1) {
            localException1.printStackTrace();
        } catch (UnsatisfiedLinkError error) {
            Crashlytics.logException(error);
        }
        return null;
    }

    public static void readLevel(Level level, File openDatabase) throws IOException {
        DB db = openDatabase(openDatabase);
        try {
            byte[] value = new byte[0];
            if (db != null) {
                value = db.get(bytes("~local_player"));
            }
            if (value != null) {
                level.setPlayer(NBTConverter.readPlayer((CompoundTag) new NBTInputStream(new ByteArrayInputStream(value), false, true).readTag()));
            }
        } catch (NullPointerException ex) {
            Crashlytics.logException(ex);
        } finally {
            System.out.println("Closing db");
            if(db != null) db.close();
        }
    }

    public static void writeLevel(Level level, File openDatabase) throws IOException {
        DB db = openDatabase(openDatabase);
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new NBTOutputStream(byteArrayOutputStream, false, true).writeTag(NBTConverter.writePlayer(level.getPlayer(), "", true));
            if( db != null) db.put(bytes("~local_player"), byteArrayOutputStream.toByteArray());
        } finally {
            System.out.println("Closing db");
            if(db != null) db.close();
        }
    }
}
