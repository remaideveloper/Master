package org.spout.nbt;

public final class NBTUtils {
    private NBTUtils() {
    }

    public static Class<? extends Tag> getTypeClass(int i) {
        switch (i) {
            case 0:
                return EndTag.class;
            case 1:
                return ByteTag.class;
            case 2:
                return ShortTag.class;
            case 3:
                return IntTag.class;
            case 4:
                return LongTag.class;
            case 5:
                return FloatTag.class;
            case 6:
                return DoubleTag.class;
            case 7:
                return ByteArrayTag.class;
            case 8:
                return StringTag.class;
            case 9:
                return ListTag.class;
            case 10:
                return CompoundTag.class;
            case 11:
                return IntArrayTag.class;
            case 100:
                return ShortArrayTag.class;
            default:
                throw new IllegalArgumentException("Invalid tag type : " + i + ".");
        }
    }

    public static int getTypeCode(Class<? extends Tag> cls) {
        if (cls.equals(ByteArrayTag.class)) {
            return 7;
        }
        if (cls.equals(ByteTag.class)) {
            return 1;
        }
        if (cls.equals(CompoundTag.class)) {
            return 10;
        }
        if (cls.equals(DoubleTag.class)) {
            return 6;
        }
        if (cls.equals(EndTag.class)) {
            return 0;
        }
        if (cls.equals(FloatTag.class)) {
            return 5;
        }
        if (cls.equals(IntTag.class)) {
            return 3;
        }
        if (cls.equals(ListTag.class)) {
            return 9;
        }
        if (cls.equals(LongTag.class)) {
            return 4;
        }
        if (cls.equals(ShortTag.class)) {
            return 2;
        }
        if (cls.equals(StringTag.class)) {
            return 8;
        }
        if (cls.equals(IntArrayTag.class)) {
            return 11;
        }
        if (cls.equals(ShortArrayTag.class)) {
            return 100;
        }
        throw new IllegalArgumentException("Invalid tag classs (" + cls.getName() + ").");
    }

    public static String getTypeName(Class<? extends Tag> cls) {
        if (cls.equals(ByteArrayTag.class)) {
            return "TAG_Byte_Array";
        }
        if (cls.equals(ByteTag.class)) {
            return "TAG_Byte";
        }
        if (cls.equals(CompoundTag.class)) {
            return "TAG_Compound";
        }
        if (cls.equals(DoubleTag.class)) {
            return "TAG_Double";
        }
        if (cls.equals(EndTag.class)) {
            return "TAG_End";
        }
        if (cls.equals(FloatTag.class)) {
            return "TAG_Float";
        }
        if (cls.equals(IntTag.class)) {
            return "TAG_Int";
        }
        if (cls.equals(ListTag.class)) {
            return "TAG_List";
        }
        if (cls.equals(LongTag.class)) {
            return "TAG_Long";
        }
        if (cls.equals(ShortTag.class)) {
            return "TAG_Short";
        }
        if (cls.equals(StringTag.class)) {
            return "TAG_String";
        }
        if (cls.equals(IntArrayTag.class)) {
            return "TAG_Int_Array";
        }
        if (cls.equals(ShortArrayTag.class)) {
            return "TAG_Short_Array";
        }
        throw new IllegalArgumentException("Invalid tag classs (" + cls.getName() + ").");
    }
}
