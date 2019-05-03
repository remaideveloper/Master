package org.spout.nbt;

public final class DoubleTag extends Tag {
    private final double value;

    public DoubleTag(String str, double d) {
        super(str);
        this.value = d;
    }

    public DoubleTag clone() {
        return new DoubleTag(getName(), this.value);
    }

    public Double getValue() {
        return Double.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Double" + str + ": " + this.value;
    }
}
