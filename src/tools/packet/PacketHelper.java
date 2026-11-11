/*
 * Decompiled with CFR 0.152.
 */
package tools.packet;

import client.ISkill;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.SkillEntry;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import server.movement.LifeMovementFragment;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.KoreanDateUtil;
import tools.Triple;
import tools.data.output.MaplePacketLittleEndianWriter;

public class PacketHelper {
    private static final long FT_UT_OFFSET = 116444592000000000L;
    public static final long MAX_TIME = 150842304000000000L;
    public static final byte[] unk1 = new byte[]{0, 64, -32, -3};
    public static final byte[] unk2 = new byte[]{59, 55, 79, 1};

    public static final long getKoreanTimestamp(long realTimestamp) {
        if (realTimestamp == -1L) {
            return 150842304000000000L;
        }
        long time = realTimestamp / 1000L / 60L;
        return time * 600000000L + 116444592000000000L;
    }

    public static final long getTime(long realTimestamp) {
        if (realTimestamp == -1L) {
            return 150842304000000000L;
        }
        long time = realTimestamp / 1000L;
        return time * 10000000L + 116444592000000000L;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        long time = roundToMinutes ? timeStampinMillis / 1000L / 60L * 600000000L : timeStampinMillis * 10000L;
        return time + 116444592000000000L;
    }

    public static void addQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeMapleAsciiString(q.getCustomData() != null ? q.getCustomData() : "");
        }
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            int time = KoreanDateUtil.getQuestTimestamp(q.getCompletionTime());
            mplew.writeInt(time);
            mplew.writeInt(time);
        }
    }

    public static final void addSkillInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        Map<ISkill, SkillEntry> skills = chr.getSkills();
        mplew.writeShort(skills.size());
        for (Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            mplew.writeInt(skill.getValue().skillevel);
            PacketHelper.addExpirationTime(mplew, skill.getValue().expiration);
            if (!skill.getKey().isFourthJob()) continue;
            mplew.writeInt(skill.getValue().masterlevel);
        }
    }

    public static final void addCoolDownInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
        mplew.writeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            mplew.writeInt(cooling.skillId);
            mplew.writeShort((int)(cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
    }

    public static final void addRocksInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; ++i) {
            mplew.writeInt(mapz[i]);
        }
        int[] map = chr.getRocks();
        for (int i = 0; i < 10; ++i) {
            mplew.writeInt(map[i]);
        }
    }

    public static final void addMonsterBookInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getMonsterBookCover());
        mplew.write(0);
        chr.getMonsterBook().addCardPacket(mplew);
    }

    public static final void addRingInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        mplew.writeShort(cRing.size());
        for (MapleRing mapleRing : cRing) {
            mplew.writeInt(mapleRing.getPartnerChrId());
            mplew.writeAsciiString(mapleRing.getPartnerName(), 15);
            mplew.writeLong(mapleRing.getRingId());
            mplew.writeLong(mapleRing.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        mplew.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 15);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
        List<MapleRing> list = aRing.getRight();
        mplew.writeShort(list.size());
        int marriageId = 30000;
        for (MapleRing ring : list) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(chr.getName(), 15);
            mplew.writeAsciiString(ring.getPartnerName(), 15);
        }
    }

    public static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(chr.getMeso());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getBeans());
        mplew.writeInt(0);
        mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
        mplew.write(unk1);
        mplew.write(unk2);
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        ArrayList<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (IItem iItem : equippedC) {
            equipped.add((Item)iItem);
        }
        Collections.sort(equipped);
        for (Item item : equipped) {
            if (item.getPosition() >= 0 || item.getPosition() <= -100) continue;
            PacketHelper.addItemInfo(mplew, item, false, false);
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if (item.getPosition() > -100 || item.getPosition() <= -1000) continue;
            PacketHelper.addItemInfo(mplew, item, false, false);
        }
        mplew.writeShort(0);
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (IItem iItem : iv.list()) {
            PacketHelper.addItemInfo(mplew, iItem, false, false);
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if (item.getPosition() > -1000 || item.getPosition() <= -1100) continue;
            PacketHelper.addItemInfo(mplew, item, false, false);
        }
        mplew.writeShort(0);
        iv = chr.getInventory(MapleInventoryType.USE);
        for (IItem iItem : iv.list()) {
            PacketHelper.addItemInfo(mplew, iItem, false, false);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (IItem iItem : iv.list()) {
            PacketHelper.addItemInfo(mplew, iItem, false, false);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (IItem iItem : iv.list()) {
            PacketHelper.addItemInfo(mplew, iItem, false, false);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (IItem iItem : iv.list()) {
            PacketHelper.addItemInfo(mplew, iItem, false, false);
        }
        mplew.write(0);
    }

    public static final void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeAsciiString(chr.getName(), 15);
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());
        mplew.writeZeroBytes(24);
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        chr.getStat().connectData(mplew);
        mplew.writeShort(chr.getRemainingAp());
        if (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob())) {
            int size = chr.getRemainingSpSize();
            mplew.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; ++i) {
                if (chr.getRemainingSp(i) <= 0) continue;
                mplew.write(i + 1);
                mplew.write(chr.getRemainingSp(i));
            }
        } else {
            mplew.writeShort(chr.getRemainingSp());
        }
        mplew.writeInt(chr.getExp());
        mplew.writeShort(chr.getFame());
        mplew.writeInt(chr.getGachExp());
        mplew.writeLong(0L);
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeShort(chr.getSubcategory());
        mplew.writeZeroBytes(30);
    }

    public static final void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega) {
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair());
        LinkedHashMap<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        LinkedHashMap<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        for (IItem iItem : equip.list()) {
            if (iItem.getPosition() < -128) continue;
            byte pos = (byte)(iItem.getPosition() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, iItem.getItemId());
                continue;
            }
            if ((pos > 100 || pos == -128) && pos != 111) {
                if (myEquip.get(pos = (byte)(pos == -128 ? 28 : pos - 100)) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, iItem.getItemId());
                continue;
            }
            if (myEquip.get(pos) == null) continue;
            maskedEquip.put(pos, iItem.getItemId());
        }
        for (Map.Entry entry : myEquip.entrySet()) {
            mplew.write((Byte)entry.getKey());
            mplew.writeInt((Integer)entry.getValue());
        }
        mplew.write(255);
        for (Map.Entry entry : maskedEquip.entrySet()) {
            mplew.write((Byte)entry.getKey());
            mplew.writeInt((Integer)entry.getValue());
        }
        mplew.write(255);
        IItem cWeapon = equip.getItem((short)-111);
        mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        mplew.writeInt(0);
        mplew.writeLong(0L);
    }

    public static final void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time) {
        mplew.write(0);
        mplew.writeShort(1408);
        if (time != -1L) {
            mplew.writeInt(KoreanDateUtil.getItemTimestamp(time));
            mplew.write(1);
        } else {
            mplew.writeInt(400967355);
            mplew.write(2);
        }
    }

    public static final void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean zeroPosition, boolean leaveOut) {
        PacketHelper.addItemInfo(mplew, item, zeroPosition, leaveOut, false);
    }

    public static final void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean zeroPosition, boolean leaveOut, boolean trade) {
        short pos = item.getPosition();
        if (zeroPosition) {
            if (!leaveOut) {
                mplew.write(0);
            }
        } else {
            if (pos <= -1 && (pos = (short)(pos * -1)) > 100 && pos < 1000) {
                pos = (short)(pos - 100);
            }
            if (!trade && item.getType() == 1) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
        }
        mplew.write(item.getPet() != null ? (byte)3 : (byte)item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0;
        mplew.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            PacketHelper.addPetItemInfo(mplew, item, item.getPet());
        } else {
            PacketHelper.addExpirationTime(mplew, item.getExpiration());
            if (item.getType() == 1) {
                IEquip equip = (IEquip)item;
                mplew.write(equip.getUpgradeSlots());
                mplew.write(equip.getLevel());
                mplew.writeShort(equip.getStr());
                mplew.writeShort(equip.getDex());
                mplew.writeShort(equip.getInt());
                mplew.writeShort(equip.getLuk());
                mplew.writeShort(equip.getHp());
                mplew.writeShort(equip.getMp());
                mplew.writeShort(equip.getWatk());
                mplew.writeShort(equip.getMatk());
                mplew.writeShort(equip.getWdef());
                mplew.writeShort(equip.getMdef());
                mplew.writeShort(equip.getAcc());
                mplew.writeShort(equip.getAvoid());
                mplew.writeShort(equip.getHands());
                mplew.writeShort(equip.getSpeed());
                mplew.writeShort(equip.getJump());
                mplew.writeMapleAsciiString(equip.getOwner());
                mplew.writeShort(equip.getFlag());
                mplew.write(0);
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel()));
                if (hasUniqueId) {
                    mplew.write(unk1);
                } else {
                    mplew.writeShort(0);
                    mplew.writeShort(equip.getExpPercentage() * 4);
                }
                mplew.writeInt(equip.getDurability());
                mplew.writeInt(equip.getViciousHammer());
                if (!hasUniqueId) {
                    mplew.write(equip.getState());
                    mplew.write(equip.getEnhance());
                    mplew.writeShort(equip.getPotential1());
                    mplew.writeShort(equip.getPotential2());
                    mplew.writeShort(equip.getPotential3());
                }
                mplew.writeShort(equip.getHpR());
                mplew.writeShort(equip.getMpR());
                mplew.writeLong(equip.getInventoryId() <= 0L ? -1L : equip.getInventoryId());
                mplew.writeLong(PacketHelper.getTime(-2L));
                mplew.writeInt(-1);
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                mplew.writeShort(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    mplew.writeLong(item.getInventoryId() <= 0L ? -1L : item.getInventoryId());
                }
            }
        }
    }

    public static final void serializeMovementList(MaplePacketLittleEndianWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static final void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            PacketHelper.addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static final void addInteraction(MaplePacketLittleEndianWriter mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore)shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        mplew.write(shop.getItemId() % 10);
        mplew.write(shop.getSize());
        mplew.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static final void addCharacterInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeLong(-1L);
        mplew.write(0);
        PacketHelper.addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());
        if (chr.getBlessOfFairyOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            mplew.write(0);
        }
        PacketHelper.addInventoryInfo(mplew, chr);
        PacketHelper.addSkillInfo(mplew, chr);
        PacketHelper.addCoolDownInfo(mplew, chr);
        PacketHelper.addQuestInfo(mplew, chr);
        PacketHelper.addRingInfo(mplew, chr);
        PacketHelper.addRocksInfo(mplew, chr);
        PacketHelper.addMonsterBookInfo(mplew, chr);
        chr.QuestInfoPacket(mplew);
        mplew.writeInt(0);
        mplew.writeShort(0);
    }

    public static final void addPetItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, MaplePet pet) {
        PacketHelper.addExpirationTime(mplew, -1L);
        mplew.writeAsciiString(pet.getName(), 13);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long)((double)System.currentTimeMillis() * 1.5)));
        } else {
            PacketHelper.addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeShort(0);
        mplew.writeShort(pet.getFlags());
        mplew.writeInt(pet.getPetItemId() == 5000054 && pet.getSecondsLeft() > 0 ? pet.getSecondsLeft() : 0);
        mplew.writeShort(0);
        mplew.write(pet.getSummoned() ? pet.getSummonedValue() : (byte)0);
        mplew.writeZeroBytes(4);
    }
}

