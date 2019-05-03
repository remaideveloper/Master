package org.spout.nbt;

public final class IntTag extends Tag {
    private final int value;

    public IntTag(String str, int i) {
        super(str);
        this.value = i;
    }

    public IntTag clone() {
        return new IntTag(getName(), this.value);
    }

    public Integer getValue() {
        return Integer.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Int" + str + ": " + this.value;
    }
}
