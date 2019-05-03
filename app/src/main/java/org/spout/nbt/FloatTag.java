package org.spout.nbt;

public final class FloatTag extends Tag {
    private final float value;

    public FloatTag(String str, float f) {
        super(str);
        this.value = f;
    }

    public FloatTag clone() {
        return new FloatTag(getName(), this.value);
    }

    public Float getValue() {
        return Float.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Float" + str + ": " + this.value;
    }
}
