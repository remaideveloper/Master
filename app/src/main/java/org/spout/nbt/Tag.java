package org.spout.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Tag {
    private final String name;

    public Tag() {
        this("");
    }

    public Tag(String str) {
        this.name = str;
    }

    public static Map<String, Tag> cloneMap(Map<String, Tag> map) {
        if (map == null) {
            return null;
        }
        Map<String, Tag> hashMap = new HashMap();
        for (Entry entry : map.entrySet()) {
            hashMap.put(String.valueOf(entry.getKey()), ((Tag) entry.getValue()).clone());
        }
        return hashMap;
    }

    public abstract Tag clone();

    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) obj;
        return getValue().equals(tag.getValue()) && getName().equals(tag.getName());
    }

    public final String getName() {
        return this.name;
    }

    public abstract Object getValue();
}
