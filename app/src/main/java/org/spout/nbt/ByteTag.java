package org.spout.nbt;

public final class ByteTag extends Tag {
    private final byte value;

    public ByteTag(String str, byte b) {
        super(str);
        this.value = b;
    }

    public ByteTag(String str, boolean z) {
        super(str);
        this.value = (byte) (z ? 1 : 0);
    }

    public ByteTag clone() {
        return new ByteTag(getName(), this.value);
    }

    public boolean getBooleanValue() {
        return this.value != (byte) 0;
    }

    public Byte getValue() {
        return Byte.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Byte" + str + ": " + this.value;
    }
}
