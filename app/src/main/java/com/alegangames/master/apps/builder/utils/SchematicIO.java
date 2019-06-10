package com.alegangames.master.apps.builder.utils;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchematicIO {
    public static CuboidClipboard read(final File file) throws IOException {
        final NBTInputStream nbtInputStream = new NBTInputStream(new FileInputStream(file));
        final CompoundTag compoundTag = (CompoundTag) nbtInputStream.readTag();
        nbtInputStream.close();
        short shortValue = 0;
        short shortValue2 = 0;
        short shortValue3 = 0;
        byte[] value = null;
        byte[] value2 = null;
        for (final Tag tag : compoundTag.getValue()) {
            final String name = tag.getName();
            switch (name) {
                case "Width":
                    shortValue = ((ShortTag) tag).getValue();
                    break;
                case "Height":
                    shortValue2 = ((ShortTag) tag).getValue();
                    break;
                case "Length":
                    shortValue3 = ((ShortTag) tag).getValue();
                    break;
                case "Materials":
                    ((StringTag) tag).getValue();
                    break;
                case "Blocks":
                    value = ((ByteArrayTag) tag).getValue();
                    break;
                case "Data":
                    value2 = ((ByteArrayTag) tag).getValue();
                    break;
                default:
                    if (name.equals("Entities") || name.equals("TileEntities")) {
                        continue;
                    }
                    System.err.println("WTF: invalid tag name: " + name);
                    break;
            }
        }
        return new CuboidClipboard(new Vector3f(shortValue, shortValue2, shortValue3), value, value2);
    }

    public static void save(final CuboidClipboard cuboidClipboard, final File file) throws IOException {
        List<Tag> list = new ArrayList<>();
        list.add(new ShortTag("Width", (short) cuboidClipboard.getWidth()));
        list.add(new ShortTag("Height", (short) cuboidClipboard.getHeight()));
        list.add(new ShortTag("Length", (short) cuboidClipboard.getLength()));
        list.add(new StringTag("Materials", "Alpha"));
        list.add(new ByteArrayTag("Blocks", cuboidClipboard.blocks));
        list.add(new ByteArrayTag("Data", cuboidClipboard.metaData));
        list.add(new ListTag<>("Entities", CompoundTag.class, Collections.EMPTY_LIST));
        list.add(new ListTag<>("TileEntities", CompoundTag.class, Collections.EMPTY_LIST));
        final CompoundTag compoundTag = new CompoundTag("Schematic", list);
        final NBTOutputStream nbtOutputStream = new NBTOutputStream(new FileOutputStream(file));
        nbtOutputStream.writeTag(compoundTag);
        nbtOutputStream.close();
    }
}
