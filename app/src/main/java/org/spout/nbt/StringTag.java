package org.spout.nbt;

public final class StringTag extends Tag {
    private final String value;

    public StringTag(String str, String str2) {
        super(str);
        this.value = str2;
    }

    public StringTag clone() {
        return new StringTag(getName(), this.value);
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        return "TAG_String" + str + ": " + this.value;
    }
}
