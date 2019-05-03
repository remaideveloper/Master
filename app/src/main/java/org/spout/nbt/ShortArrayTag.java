package org.spout.nbt;

import java.util.Arrays;

public class ShortArrayTag extends Tag {
    private final short[] value;

    public ShortArrayTag(String str, short[] sArr) {
        super(str);
        this.value = sArr;
    }

    private short[] cloneArray(short[] sArr) {
        if (sArr == null) {
            return null;
        }
        int length = sArr.length;
        System.arraycopy(sArr, 0, new short[length], 0, length);
        return sArr;
    }

    public ShortArrayTag clone() {
        return new ShortArrayTag(getName(), cloneArray(this.value));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ShortArrayTag)) {
            return false;
        }
        ShortArrayTag shortArrayTag = (ShortArrayTag) obj;
        return Arrays.equals(this.value, shortArrayTag.value) && getName().equals(shortArrayTag.getName());
    }

    public short[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (short toHexString : this.value) {
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
        return "TAG_Short_Array" + str + ": " + stringBuilder.toString();
    }
}
