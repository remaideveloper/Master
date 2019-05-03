package org.spout.nbt;

import java.util.Arrays;

public class IntArrayTag extends Tag {
    private final int[] value;

    public IntArrayTag(String str, int[] iArr) {
        super(str);
        this.value = iArr;
    }

    private int[] cloneArray(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        int length = iArr.length;
        System.arraycopy(iArr, 0, new byte[length], 0, length);
        return iArr;
    }

    public IntArrayTag clone() {
        return new IntArrayTag(getName(), cloneArray(this.value));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IntArrayTag)) {
            return false;
        }
        IntArrayTag intArrayTag = (IntArrayTag) obj;
        return Arrays.equals(this.value, intArrayTag.value) && getName().equals(intArrayTag.getName());
    }

    public int[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int toHexString : this.value) {
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
