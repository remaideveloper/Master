package org.spout.nbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CompoundTag extends Tag {
    private final List<Tag> value;

    public CompoundTag(String str, List<Tag> list) {
        super(str);
        this.value = Collections.unmodifiableList(list);
    }

    public CompoundTag clone() {
        return new CompoundTag(getName(), new ArrayList(this.value));
    }

    public List<Tag> getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TAG_Compound").append(str).append(": ").append(this.value.size()).append(" entries\r\n{\r\n");
        for (Tag value : this.value) {
            stringBuilder.append("   ").append(value.getValue().toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
