package org.spout.nbt;

public final class ShortTag extends Tag {
    private final short value;

    public ShortTag(String str, short s) {
        super(str);
        this.value = s;
    }

    public ShortTag clone() {
        return new ShortTag(getName(), this.value);
    }

    public Short getValue() {
        return Short.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Short" + str + ": " + this.value;
    }
}
