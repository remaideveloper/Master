package org.spout.nbt;

public final class EndTag extends Tag {
    public EndTag clone() {
        return new EndTag();
    }

    public Object getValue() {
        return null;
    }

    public String toString() {
        return "TAG_End";
    }
}
