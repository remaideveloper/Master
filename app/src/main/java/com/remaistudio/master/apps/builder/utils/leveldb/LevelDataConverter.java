package com.remaistudio.master.apps.builder.utils.leveldb;

import com.remaistudio.master.apps.builder.entities.Level;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class LevelDataConverter {
    public static final byte[] header;

    static {
        header = new byte[]{4, 0, 0, 0};
    }

    public static void main(final String[] array) throws Exception {
        Level read = read(new File(array[0]));
        System.out.println(read);
        write(read, new File(array[1]));
    }

    public static Level read(File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        bufferedInputStream.skip(8L);
        Level level = NBTConverter.readLevel((CompoundTag) new NBTInputStream(bufferedInputStream, false, true).readTag());
        bufferedInputStream.close();
        file = new File(file.getParentFile(), "db");
        if (!file.exists()) {
            return level;
        }
        try {
            LevelDBConverter.readLevel(level, file);
            return level;
        } catch (IOException ex) {
            ex.printStackTrace();
            return level;
        }
    }

    public static void write(final Level level, File file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new NBTOutputStream(byteArrayOutputStream, false, true).writeTag(NBTConverter.writeLevel(level));
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        int size = byteArrayOutputStream.size();
        dataOutputStream.write(LevelDataConverter.header);
        dataOutputStream.writeInt(Integer.reverseBytes(size));
        byteArrayOutputStream.writeTo(dataOutputStream);
        dataOutputStream.close();
        file = new File(file.getParentFile(), "db");
        if (file.exists()) {
            LevelDBConverter.writeLevel(level, file);
        }
    }
}
