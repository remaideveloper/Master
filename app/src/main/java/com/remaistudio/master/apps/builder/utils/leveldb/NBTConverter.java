package com.remaistudio.master.apps.builder.utils.leveldb;

import com.remaistudio.master.apps.builder.entities.InventorySlot;
import com.remaistudio.master.apps.builder.entities.ItemStack;
import com.remaistudio.master.apps.builder.entities.Level;
import com.remaistudio.master.apps.builder.entities.Player;
import com.remaistudio.master.apps.builder.entities.PlayerAbilities;
import com.remaistudio.master.apps.builder.utils.Vector3f;

import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class NBTConverter {
    public static void readAbilities(final CompoundTag compoundTag, final PlayerAbilities playerAbilities) {
        for (final Tag tag : compoundTag.getValue()) {
            final String name = tag.getName();
            if (tag instanceof ByteTag) {
                final boolean b = ((ByteTag) tag).getValue() != 0;
                switch (name) {
                    case "flying":
                        playerAbilities.flying = b;
                        break;
                    case "instabuild":
                        playerAbilities.instabuild = b;
                        break;
                    case "invulnerable":
                        playerAbilities.invulnerable = b;
                        break;
                    default:
                        if (!name.equals("mayfly")) {
                            continue;
                        }
                        playerAbilities.mayfly = b;
                        break;
                }
            }
        }
    }

    public static List<ItemStack> readArmor(final ListTag<CompoundTag> listTag) {
        final ArrayList<ItemStack> list = new ArrayList<>();
        final Iterator<CompoundTag> iterator = listTag.getValue().iterator();
        while (iterator.hasNext()) {
            list.add(readItemStack(iterator.next()));
        }
        return list;
    }

    public static List<InventorySlot> readInventory(final ListTag<CompoundTag> listTag) {
        final ArrayList<InventorySlot> list = new ArrayList<>();
        final Iterator<CompoundTag> iterator = listTag.getValue().iterator();
        while (iterator.hasNext()) {
            list.add(readInventorySlot(iterator.next()));
        }
        return list;
    }

    public static InventorySlot readInventorySlot(final CompoundTag compoundTag) {
        final List<Tag> value = compoundTag.getValue();
        byte byteValue = 0;
        short shortValue = 0;
        short shortValue2 = 0;
        int n = 0;
        for (final Tag tag : value) {
            switch (tag.getName()) {
                case "Slot":
                    byteValue = ((ByteTag) tag).getValue();
                    break;
                case "id":
                    shortValue = ((ShortTag) tag).getValue();
                    break;
                case "Damage":
                    shortValue2 = ((ShortTag) tag).getValue();
                    break;
                default:
                    if (!tag.getName().equals("Count")) {
                        continue;
                    }
                    final byte byteValue2 = ((ByteTag) tag).getValue();
                    if ((n = byteValue2) >= 0) {
                        continue;
                    }
                    n = byteValue2 + 256;
                    break;
            }
        }
        return new InventorySlot(byteValue, new ItemStack(shortValue, shortValue2, n));
    }

    public static ItemStack readItemStack(final CompoundTag compoundTag) {
        final List<Tag> value = compoundTag.getValue();
        short shortValue = 0;
        short shortValue2 = 0;
        int n = 0;
        for (final Tag tag : value) {
            switch (tag.getName()) {
                case "id":
                    shortValue = ((ShortTag) tag).getValue();
                    break;
                case "Damage":
                    shortValue2 = ((ShortTag) tag).getValue();
                    break;
                default:
                    if (!tag.getName().equals("Count")) {
                        continue;
                    }
                    final byte byteValue = ((ByteTag) tag).getValue();
                    if ((n = byteValue) >= 0) {
                        continue;
                    }
                    n = byteValue + 256;
                    break;
            }
        }
        return new ItemStack(shortValue, shortValue2, n);
    }

    public static Level readLevel(final CompoundTag compoundTag) {
        final Level level = new Level();
        for (final Tag tag : compoundTag.getValue()) {
            final String name = tag.getName();
            switch (name) {
                case "GameType":
                    level.setGameType(((IntTag) tag).getValue());
                    break;
                case "LastPlayed":
                    level.setLastPlayed(((LongTag) tag).getValue());
                    break;
                case "LevelName":
                    level.setLevelName(((StringTag) tag).getValue());
                    break;
                case "Platform":
                    level.setPlatform(((IntTag) tag).getValue());
                    break;
                case "Player":
                    level.setPlayer(readPlayer((CompoundTag) tag));
                    break;
                case "RandomSeed":
                    level.setRandomSeed(((LongTag) tag).getValue());
                    break;
                case "SizeOnDisk":
                    level.setSizeOnDisk(((LongTag) tag).getValue());
                    break;
                case "SpawnX":
                    level.setSpawnX(((IntTag) tag).getValue());
                    break;
                case "SpawnY":
                    level.setSpawnY(((IntTag) tag).getValue());
                    break;
                case "SpawnZ":
                    level.setSpawnZ(((IntTag) tag).getValue());
                    break;
                case "StorageVersion":
                    level.setStorageVersion(((IntTag) tag).getValue());
                    break;
                case "Time":
                    level.setTime(((LongTag) tag).getValue());
                    break;
                case "dayCycleStopTime":
                    level.setDayCycleStopTime(((LongTag) tag).getValue());
                    break;
                case "spawnMobs":
                    level.setSpawnMobs(((ByteTag) tag).getValue() != 0);
                    break;
                case "Dimension":
                    level.setDimension(((IntTag) tag).getValue());
                    break;
                case "Generator":
                    level.setGenerator(((IntTag) tag).getValue());
                    break;
                default:
                    System.out.println("Unhandled level tag: " + name + ":" + tag);
                    break;
            }
        }
        return level;
    }

    public static List<InventorySlot> readLoadout(final CompoundTag compoundTag) {
        for (final Tag tag : compoundTag.getValue()) {
            if (tag.getName().equals("Inventory")) {
                return readInventory((ListTag<CompoundTag>) tag);
            }
        }
        return null;
    }

    public static Player readPlayer(final CompoundTag compoundTag) {
        final List<Tag> value = compoundTag.getValue();
        final Player player = new Player();
        for (final Tag tag : value) {
            final String name = tag.getName();
            if (tag.getName().equals("Pos")) {
                player.setLocation(readVector((ListTag<FloatTag>) tag));
            } else if (tag.getName().equals("Motion")) {
                player.setVelocity(readVector((ListTag<FloatTag>) tag));
            } else if (tag.getName().equals("Air")) {
                player.setAirTicks(((ShortTag) tag).getValue());
            } else if (tag.getName().equals("Fire")) {
                player.setFireTicks(((ShortTag) tag).getValue());
            } else if (tag.getName().equals("FallDistance")) {
                player.setFallDistance(((FloatTag) tag).getValue());
            } else if (tag.getName().equals("Rotation")) {
                final List<FloatTag> value2 = ((ListTag<FloatTag>) tag).getValue();
                player.setYaw(value2.get(0).getValue());
                player.setPitch(value2.get(1).getValue());
            } else if (tag.getName().equals("OnGround")) {
                player.setOnGround(((ByteTag) tag).getValue() > 0);
            } else if (tag.getName().equals("AttackTime")) {
                player.setAttackTime(((ShortTag) tag).getValue());
            } else if (tag.getName().equals("DeathTime")) {
                player.setDeathTime(((ShortTag) tag).getValue());
            } else if (tag.getName().equals("Health")) {
                player.setHealth(((ShortTag) tag).getValue());
            } else if (tag.getName().equals("HurtTime")) {
                player.setHurtTime(((ShortTag) tag).getValue());
            } else if (name.equals("Armor")) {
                player.setArmor(readArmor((ListTag<CompoundTag>) tag));
            } else if (name.equals("BedPositionX")) {
                player.setBedPositionX(((IntTag) tag).getValue());
            } else if (name.equals("BedPositionY")) {
                player.setBedPositionY(((IntTag) tag).getValue());
            } else if (name.equals("BedPositionZ")) {
                player.setBedPositionZ(((IntTag) tag).getValue());
            } else if (tag.getName().equals("Dimension")) {
                player.setDimension(((IntTag) tag).getValue());
            } else if (tag.getName().equals("Inventory")) {
                player.setInventory(readInventory((ListTag<CompoundTag>) tag));
            } else if (tag.getName().equals("Score")) {
                player.setScore(((IntTag) tag).getValue());
            } else if (tag.getName().equals("Sleeping")) {
                player.setSleeping(((ByteTag) tag).getValue() != 0);
            } else if (name.equals("SleepTimer")) {
                player.setSleepTimer(((ShortTag) tag).getValue());
            } else if (name.equals("SpawnX")) {
                player.setSpawnX(((IntTag) tag).getValue());
            } else if (name.equals("SpawnY")) {
                player.setSpawnY(((IntTag) tag).getValue());
            } else if (name.equals("SpawnZ")) {
                player.setSpawnZ(((IntTag) tag).getValue());
            } else if (name.equals("abilities")) {
                readAbilities((CompoundTag) tag, player.getAbilities());
            } else {
                System.out.println("Unhandled player tag: " + name);
            }
        }
        return player;
    }

    public static Vector3f readVector(final ListTag<FloatTag> listTag) {
        final List<FloatTag> value = listTag.getValue();
        return new Vector3f(value.get(0).getValue(), value.get(1).getValue(), value.get(2).getValue());
    }

    public static CompoundTag writeAbilities(final PlayerAbilities playerAbilities, final String s) {
        final boolean b = true;
        List<Tag> list = new ArrayList<>(4);
        byte b2;
        if (playerAbilities.flying) {
            b2 = 1;
        } else {
            b2 = 0;
        }
        list.add(new ByteTag("flying", b2));
        byte b3;
        if (playerAbilities.instabuild) {
            b3 = 1;
        } else {
            b3 = 0;
        }
        list.add(new ByteTag("instabuild", b3));
        byte b4;
        if (playerAbilities.invulnerable) {
            b4 = 1;
        } else {
            b4 = 0;
        }
        list.add(new ByteTag("invulnerable", b4));
        byte b5;
        if (playerAbilities.mayfly) {
            b5 = (byte) (b ? 1 : 0);
        } else {
            b5 = 0;
        }
        list.add(new ByteTag("mayfly", b5));
        return new CompoundTag(s, list);
    }

    public static ListTag<CompoundTag> writeArmor(final List<ItemStack> list, final String s) {
        final ArrayList<CompoundTag> list2 = new ArrayList<>(list.size());
        final Iterator<ItemStack> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(writeItemStack(iterator.next(), ""));
        }
        return new ListTag<>(s, CompoundTag.class, list2);
    }

    public static ListTag<CompoundTag> writeInventory(final List<InventorySlot> list, final String s) {
        final ArrayList<CompoundTag> list2 = new ArrayList<>(list.size());
        final Iterator<InventorySlot> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(writeInventorySlot(iterator.next()));
        }
        return new ListTag<>(s, CompoundTag.class, list2);
    }

    public static CompoundTag writeInventorySlot(final InventorySlot inventorySlot) {
        List<Tag> list = new ArrayList<>(4);
        final ItemStack contents = inventorySlot.getContents();
        list.add(new ByteTag("Count", (byte) contents.getCount()));
        list.add(new ShortTag("Damage", contents.getDurability()));
        list.add(new ByteTag("Slot", inventorySlot.getSlot()));
        list.add(new ShortTag("id", contents.getTypeId()));
        return new CompoundTag("", list);
    }

    public static CompoundTag writeItemStack(final ItemStack itemStack, final String s) {
        List<Tag> list = new ArrayList<>(3);
        list.add(new ByteTag("Count", (byte) itemStack.getCount()));
        list.add(new ShortTag("Damage", itemStack.getDurability()));
        list.add(new ShortTag("id", itemStack.getTypeId()));
        return new CompoundTag(s, list);
    }

    public static CompoundTag writeLevel(final Level level) {
        List<Tag> list = new ArrayList<>(11);
        list.add(new IntTag("GameType", level.getGameType()));
        list.add(new LongTag("LastPlayed", level.getLastPlayed()));
        list.add(new StringTag("LevelName", level.getLevelName()));
        list.add(new IntTag("Platform", level.getPlatform()));
        list.add(writePlayer(level.getPlayer(), "Player"));
        list.add(new LongTag("RandomSeed", level.getRandomSeed()));
        list.add(new LongTag("SizeOnDisk", level.getSizeOnDisk()));
        list.add(new IntTag("SpawnX", level.getSpawnX()));
        list.add(new IntTag("SpawnY", level.getSpawnY()));
        list.add(new IntTag("SpawnZ", level.getSpawnZ()));
        list.add(new IntTag("StorageVersion", level.getStorageVersion()));
        list.add(new LongTag("Time", level.getTime()));
        list.add(new LongTag("dayCycleStopTime", level.getDayCycleStopTime()));
        byte b;
        if (level.getSpawnMobs()) {
            b = 1;
        } else {
            b = 0;
        }
        list.add(new ByteTag("spawnMobs", b));
        list.add(new IntTag("Dimension", level.getDimension()));
        list.add(new IntTag("Generator", level.getGenerator()));
        return new CompoundTag("", list);
    }

//    public static CompoundTag writeLoadout(final List<InventorySlot> list) {
//        return new CompoundTag("", Collections.singletonList(writeInventory(list, "Inventory")));
//    }

    public static CompoundTag writePlayer(final Player player, final String s) {
        return writePlayer(player, s, false);
    }

    public static CompoundTag writePlayer(final Player player, final String s, final boolean b) {
        final boolean b2 = true;
        List<Tag> list = new ArrayList<>();
        list.add(new ShortTag("Air", player.getAirTicks()));
        list.add(new FloatTag("FallDistance", player.getFallDistance()));
        list.add(new ShortTag("Fire", player.getFireTicks()));
        list.add(writeVector(player.getVelocity(), "Motion"));
        list.add(writeVector(player.getLocation(), "Pos"));
        List<FloatTag> list2 = new ArrayList<>(2);
        list2.add(new FloatTag("", player.getYaw()));
        list2.add(new FloatTag("", player.getPitch()));
        list.add(new ListTag<>("Rotation", FloatTag.class, list2));
        byte b3 = (byte) (b2 ? 1 : 0);
        if (player.isOnGround()) {
            list.add(new ByteTag("OnGround", (byte) 1));
            list.add(new ShortTag("AttackTime", player.getAttackTime()));
            list.add(new ShortTag("DeathTime", player.getDeathTime()));
            list.add(new ShortTag("Health", player.getHealth()));
            list.add(new ShortTag("HurtTime", player.getHurtTime()));
            if (player.getArmor() != null) {
                list.add(writeArmor(player.getArmor(), "Armor"));
            }
            list.add(new IntTag("BedPositionX", player.getBedPositionX()));
            list.add(new IntTag("BedPositionY", player.getBedPositionY()));
            list.add(new IntTag("BedPositionZ", player.getBedPositionZ()));
            list.add(new IntTag("Dimension", player.getDimension()));
            list.add(writeInventory(player.getInventory(), "Inventory"));
            list.add(new IntTag("Score", player.getScore()));
            b3 = (byte) (b2 ? 1 : 0);
            if (!player.isSleeping()) {
                b3 = 0;
            }
        }
        list.add(new ByteTag("Sleeping", b3));
        list.add(new ShortTag("SleepTimer", player.getSleepTimer()));
        list.add(new IntTag("SpawnX", player.getSpawnX()));
        list.add(new IntTag("SpawnY", player.getSpawnY()));
        list.add(new IntTag("SpawnZ", player.getSpawnZ()));
        list.add(writeAbilities(player.getAbilities(), "abilities"));
        if (player.getRiding() != null) {
            list.add(new IntTag("Riding", 0));
        }
        if (b) {
            list.add(new IntTag("id", 63));
        }
        Collections.sort(list, new Comparator<Tag>() {
            @Override
            public int compare(final Tag tag, final Tag tag2) {
                return tag.getName().compareTo(tag2.getName());
            }

            public boolean equals(final Tag tag, final Tag tag2) {
                return tag.getName().equals(tag2.getName());
            }
        });
        return new CompoundTag(s, list);
    }

    public static ListTag<FloatTag> writeVector(final Vector3f vector3f, final String s) {
        final ArrayList<FloatTag> list = new ArrayList<>(3);
        list.add(new FloatTag("", vector3f.getX()));
        list.add(new FloatTag("", vector3f.getY()));
        list.add(new FloatTag("", vector3f.getZ()));
        return new ListTag<>(s, FloatTag.class, list);
    }
}
