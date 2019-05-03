package org.spout.nbt;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListTag<T extends Tag> extends Tag {
    private final Class<T> type;
    private final List<T> value;

    public ListTag(String str, Class<T> cls, List<T> list) {
        super(str);
        this.type = cls;
        this.value = Collections.unmodifiableList(list);
    }

    public ListTag<T> clone() {
        List arrayList = new ArrayList();
        for (Tag clone : this.value) {
            arrayList.add(clone.clone());
        }
        return new ListTag(getName(), this.type, arrayList);
    }

    public Class<T> getType() {
        return this.type;
    }

    public List<T> getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String str = "";
        if (!(name == null || name.equals(""))) {
            str = "(\"" + getName() + "\")";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TAG_List").append(str).append(": ").append(this.value.size()).append(" entries of type ").append(NBTUtils.getTypeName(this.type)).append("\r\n{\r\n");
        for (Tag obj : this.value) {
            stringBuilder.append("   ").append(obj.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
