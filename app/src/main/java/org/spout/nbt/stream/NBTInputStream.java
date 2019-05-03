package org.spout.nbt.stream;

import androidx.core.internal.view.SupportMenu;

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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class NBTInputStream implements Closeable {
    private final DataInputStream is;
    private final boolean littleEndian;

    public NBTInputStream(InputStream inputStream) throws IOException {
        this(inputStream, true, false);
    }

    public NBTInputStream(InputStream inputStream, boolean z) throws IOException {
        this(inputStream, z, false);
    }

    public NBTInputStream(InputStream inputStream, boolean z, boolean z2) throws IOException {
        this.littleEndian = z2;
        if (z) {
            inputStream = new GZIPInputStream(inputStream);
        }
        this.is = new DataInputStream(inputStream);
    }

    private Tag readTag(int i) throws IOException {
        String str;
        int readByte = this.is.readByte() & 255;
        if (readByte != 0) {
            int readShort = this.is.readShort() & SupportMenu.USER_MASK;
            if (this.littleEndian) {
                readShort = Short.reverseBytes((short) readShort);
            }
            byte[] bArr = new byte[readShort];
            this.is.readFully(bArr);
            str = new String(bArr, NBTConstants.CHARSET.name());
        } else {
            str = "";
        }
        return readTagPayload(readByte, str, i);
    }

    private Tag readTagPayload(int i, String str, int i2) throws IOException {
        int i3 = 0;
        byte[] bArr;
        int reverseBytes;
        int i4;
        switch (i) {
            case 0:
                if (i2 != 0) {
                    return new EndTag();
                }
                throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
            case 1:
                return new ByteTag(str, this.is.readByte());
            case 2:
                return new ShortTag(str, this.littleEndian ? Short.reverseBytes(this.is.readShort()) : this.is.readShort());
            case 3:
                return new IntTag(str, this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt());
            case 4:
                return new LongTag(str, this.littleEndian ? Long.reverseBytes(this.is.readLong()) : this.is.readLong());
            case 5:
                return new FloatTag(str, this.littleEndian ? Float.intBitsToFloat(Integer.reverseBytes(this.is.readInt())) : this.is.readFloat());
            case 6:
                return new DoubleTag(str, this.littleEndian ? Double.longBitsToDouble(Long.reverseBytes(this.is.readLong())) : this.is.readDouble());
            case 7:
                bArr = new byte[(this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt())];
                this.is.readFully(bArr);
                return new ByteArrayTag(str, bArr);
            case 8:
                bArr = new byte[(this.littleEndian ? Short.reverseBytes(this.is.readShort()) : this.is.readShort())];
                this.is.readFully(bArr);
                return new StringTag(str, new String(bArr, NBTConstants.CHARSET.name()));
            case 9:
                byte readByte = this.is.readByte();
                reverseBytes = this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt();
                Class typeClass = NBTUtils.getTypeClass(readByte);
                List arrayList = new ArrayList();
                while (i3 < reverseBytes) {
                    Tag readTagPayload = readTagPayload(readByte, "", i2 + 1);
                    if (readTagPayload instanceof EndTag) {
                        throw new IOException("TAG_End not permitted in a list.");
                    } else if (typeClass.isInstance(readTagPayload)) {
                        arrayList.add(readTagPayload);
                        i3++;
                    } else {
                        throw new IOException("Mixed tag types within a list.");
                    }
                }
                return new ListTag(str, typeClass, arrayList);
            case 10:
                List arrayList2 = new ArrayList();
                while (true) {
                    Tag readTag = readTag(i2 + 1);
                    if (readTag instanceof EndTag) {
                        return new CompoundTag(str, arrayList2);
                    }
                    arrayList2.add(readTag);
                }
            case 11:
                reverseBytes = this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt();
                int[] iArr = new int[reverseBytes];
                for (i4 = 0; i4 < reverseBytes; i4++) {
                    iArr[i4] = this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt();
                }
                return new IntArrayTag(str, iArr);
            case 100:
                reverseBytes = this.littleEndian ? Integer.reverseBytes(this.is.readInt()) : this.is.readInt();
                short[] sArr = new short[reverseBytes];
                for (i4 = 0; i4 < reverseBytes; i4++) {
                    sArr[i4] = this.littleEndian ? Short.reverseBytes(this.is.readShort()) : this.is.readShort();
                }
                return new ShortArrayTag(str, sArr);
            default:
                throw new IOException("Invalid tag type: " + i + ".");
        }
    }

    public void close() throws IOException {
        this.is.close();
    }

    public boolean isLittleEndian() {
        return this.littleEndian;
    }

    public Tag readTag() throws IOException {
        return readTag(0);
    }
}
