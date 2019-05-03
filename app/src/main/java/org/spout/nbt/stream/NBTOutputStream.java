package org.spout.nbt.stream;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.DoubleTag;
import org.spout.nbt.EndTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.NBTConstants;
import org.spout.nbt.NBTUtils;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public final class NBTOutputStream implements Closeable {
    private final boolean littleEndian;
    private final DataOutputStream os;

    public NBTOutputStream(OutputStream outputStream) throws IOException {
        this(outputStream, true, false);
    }

    public NBTOutputStream(OutputStream outputStream, boolean z) throws IOException {
        this(outputStream, z, false);
    }

    public NBTOutputStream(OutputStream outputStream, boolean z, boolean z2) throws IOException {
        this.littleEndian = z2;
        if (z) {
            outputStream = new GZIPOutputStream(outputStream);
        }
        this.os = new DataOutputStream(outputStream);
    }

    private void writeByteArrayTagPayload(ByteArrayTag byteArrayTag) throws IOException {
        byte[] value = byteArrayTag.getValue();
        this.os.writeInt(this.littleEndian ? Integer.reverseBytes(value.length) : value.length);
        this.os.write(value);
    }

    private void writeByteTagPayload(ByteTag byteTag) throws IOException {
        this.os.writeByte(byteTag.getValue().byteValue());
    }

    private void writeCompoundTagPayload(CompoundTag compoundTag) throws IOException {
        for (Tag writeTag : compoundTag.getValue()) {
            writeTag(writeTag);
        }
        this.os.writeByte(0);
    }

    private void writeDoubleTagPayload(DoubleTag doubleTag) throws IOException {
        if (this.littleEndian) {
            this.os.writeLong(Long.reverseBytes(Double.doubleToLongBits(doubleTag.getValue().doubleValue())));
        } else {
            this.os.writeDouble(doubleTag.getValue().doubleValue());
        }
    }

    private void writeEndTagPayload(EndTag endTag) {
    }

    private void writeFloatTagPayload(FloatTag floatTag) throws IOException {
        if (this.littleEndian) {
            this.os.writeInt(Integer.reverseBytes(Float.floatToIntBits(floatTag.getValue().floatValue())));
        } else {
            this.os.writeFloat(floatTag.getValue().floatValue());
        }
    }

    private void writeIntArrayTagPayload(IntArrayTag intArrayTag) throws IOException {
        int[] value = intArrayTag.getValue();
        this.os.writeInt(this.littleEndian ? Integer.reverseBytes(value.length) : value.length);
        int i = 0;
        while (i < value.length) {
            this.os.writeInt(this.littleEndian ? Integer.reverseBytes(value[i]) : value[i]);
            i++;
        }
    }

    private void writeIntTagPayload(IntTag intTag) throws IOException {
        this.os.writeInt(this.littleEndian ? Integer.reverseBytes(intTag.getValue().intValue()) : intTag.getValue().intValue());
    }

    private void writeListTagPayload(ListTag<?> listTag) throws IOException {
        Class type = listTag.getType();
        List<Tag> value = (List<Tag>) listTag.getValue();
        int size = value.size();
        this.os.writeByte(NBTUtils.getTypeCode(type));
        DataOutputStream dataOutputStream = this.os;
        if (this.littleEndian) {
            size = Integer.reverseBytes(size);
        }
        dataOutputStream.writeInt(size);
        for (Tag writeTagPayload : value) {
            writeTagPayload(writeTagPayload);
        }
    }

    private void writeLongTagPayload(LongTag longTag) throws IOException {
        this.os.writeLong(this.littleEndian ? Long.reverseBytes(longTag.getValue().longValue()) : longTag.getValue().longValue());
    }

    private void writeShortArrayTagPayload(ShortArrayTag shortArrayTag) throws IOException {
        short[] value = shortArrayTag.getValue();
        this.os.writeInt(this.littleEndian ? Integer.reverseBytes(value.length) : value.length);
        int i = 0;
        while (i < value.length) {
            this.os.writeShort(this.littleEndian ? Short.reverseBytes(value[i]) : value[i]);
            i++;
        }
    }

    private void writeShortTagPayload(ShortTag shortTag) throws IOException {
        this.os.writeShort(this.littleEndian ? Short.reverseBytes(shortTag.getValue().shortValue()) : shortTag.getValue().shortValue());
    }

    private void writeStringTagPayload(StringTag stringTag) throws IOException {
        byte[] bytes = stringTag.getValue().getBytes(NBTConstants.CHARSET.name());
        this.os.writeShort(this.littleEndian ? Short.reverseBytes((short) bytes.length) : bytes.length);
        this.os.write(bytes);
    }

    private void writeTagPayload(Tag tag) throws IOException {
        int typeCode = NBTUtils.getTypeCode(tag.getClass());
        switch (typeCode) {
            case 0:
                writeEndTagPayload((EndTag) tag);
                return;
            case 1:
                writeByteTagPayload((ByteTag) tag);
                return;
            case 2:
                writeShortTagPayload((ShortTag) tag);
                return;
            case 3:
                writeIntTagPayload((IntTag) tag);
                return;
            case 4:
                writeLongTagPayload((LongTag) tag);
                return;
            case 5:
                writeFloatTagPayload((FloatTag) tag);
                return;
            case 6:
                writeDoubleTagPayload((DoubleTag) tag);
                return;
            case 7:
                writeByteArrayTagPayload((ByteArrayTag) tag);
                return;
            case 8:
                writeStringTagPayload((StringTag) tag);
                return;
            case 9:
                writeListTagPayload((ListTag) tag);
                return;
            case 10:
                writeCompoundTagPayload((CompoundTag) tag);
                return;
            case 11:
                writeIntArrayTagPayload((IntArrayTag) tag);
                return;
            case 100:
                writeShortArrayTagPayload((ShortArrayTag) tag);
                return;
            default:
                throw new IOException("Invalid tag type: " + typeCode + ".");
        }
    }

    public void close() throws IOException {
        this.os.close();
    }

    public boolean isLittleEndian() {
        return this.littleEndian;
    }

    public void writeTag(Tag tag) throws IOException {
        int typeCode = NBTUtils.getTypeCode(tag.getClass());
        byte[] bytes = tag.getName().getBytes(NBTConstants.CHARSET.name());
        this.os.writeByte(typeCode);
        this.os.writeShort(this.littleEndian ? Short.reverseBytes((short) bytes.length) : bytes.length);
        this.os.write(bytes);
        if (typeCode == 0) {
            throw new IOException("Named TAG_End not permitted.");
        }
        writeTagPayload(tag);
    }
}
