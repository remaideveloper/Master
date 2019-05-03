package org.spout.nbt;

public final class LongTag extends Tag {
    private final long value;

    public LongTag(String str, long j) {
        super(str);
        this.value = j;
    }

    public LongTag clone() {
        return new LongTag(getName(), this.value);
    }

    public Long getValue() {
        return Long.valueOf(this.value);
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_Long" + str + ": " + this.value;
    }
}
