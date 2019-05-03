package org.spout.nbt;

import java.util.Arrays;

public final class ByteArrayTag extends Tag {
    private final byte[] value;

    public ByteArrayTag(String str, byte[] bArr) {
        super(str);
        this.value = bArr;
    }

    private byte[] cloneArray(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        int length = bArr.length;
        Object obj = new byte[length];
        System.arraycopy(bArr, 0, obj, 0, length);
        return (byte[]) obj;
    }

    public ByteArrayTag clone() {
        return new ByteArrayTag(getName(), cloneArray(this.value));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ByteArrayTag)) {
            return false;
        }
        ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
        return Arrays.equals(this.value, byteArrayTag.value) && getName().equals(byteArrayTag.getName());
    }

    public byte[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte toHexString : this.value) {
            String toUpperCase = Integer.toHexString(toHexString).toUpperCase();
            if (toUpperCase.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(toUpperCase).append(" ");
        }
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Byte_Array" + str + ": " + stringBuilder.toString();
    }
}
