/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import server.ItemMakerFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

public class ItemMakerHandler {
    public static final void ItemMaker(SeekableLittleEndianAccessor slea, MapleClient c) {
        int makerType = slea.readInt();
        switch (makerType) {
            case 1: {
                int toCreate = slea.readInt();
                if (GameConstants.isGem(toCreate)) {
                    ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!ItemMakerHandler.hasSkill(c, gem.getReqSkillLevel())) {
                        return;
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return;
                    }
                    int randGemGiven = ItemMakerHandler.getRandomGem(gem.getRandomReward());
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(randGemGiven)).isFull()) {
                        return;
                    }
                    int taken = ItemMakerHandler.checkRequiredNRemove(c, gem.getReqRecipes());
                    if (taken == 0) {
                        return;
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    MapleInventoryManipulator.addById(c, randGemGiven, (byte)(taken == randGemGiven ? 9 : 1));
                    c.getSession().write((Object)MaplePacketCreator.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    break;
                }
                if (GameConstants.isOtherGem(toCreate)) {
                    ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!ItemMakerHandler.hasSkill(c, gem.getReqSkillLevel())) {
                        return;
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return;
                    }
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                        return;
                    }
                    if (ItemMakerHandler.checkRequiredNRemove(c, gem.getReqRecipes()) == 0) {
                        return;
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    if (GameConstants.getInventoryType(toCreate) == MapleInventoryType.EQUIP) {
                        MapleInventoryManipulator.addbyItem(c, MapleItemInformationProvider.getInstance().getEquipById(toCreate));
                    } else {
                        MapleInventoryManipulator.addById(c, toCreate, (short)1);
                    }
                    c.getSession().write((Object)MaplePacketCreator.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    break;
                }
                boolean stimulator = slea.readByte() > 0;
                int numEnchanter = slea.readInt();
                ItemMakerFactory.ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(toCreate);
                if (create == null) {
                    return;
                }
                if (numEnchanter > create.getTUC()) {
                    return;
                }
                if (!ItemMakerHandler.hasSkill(c, create.getReqSkillLevel())) {
                    return;
                }
                if (c.getPlayer().getMeso() < create.getCost()) {
                    return;
                }
                if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                    return;
                }
                if (ItemMakerHandler.checkRequiredNRemove(c, create.getReqItems()) == 0) {
                    return;
                }
                c.getPlayer().gainMeso(-create.getCost(), false);
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                Equip toGive = (Equip)ii.getEquipById(toCreate);
                if (stimulator || numEnchanter > 0) {
                    if (c.getPlayer().haveItem(create.getStimulator(), 1, false, true)) {
                        ii.randomizeStats(toGive);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
                    }
                    for (int i = 0; i < numEnchanter; ++i) {
                        Map<String, Byte> stats;
                        int enchant = slea.readInt();
                        if (!c.getPlayer().haveItem(enchant, 1, false, true) || (stats = ii.getItemMakeStats(enchant)) == null) continue;
                        ItemMakerHandler.addEnchantStats(stats, toGive);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, enchant, 1, false, false);
                    }
                }
                MapleInventoryManipulator.addbyItem(c, toGive);
                c.getSession().write((Object)MaplePacketCreator.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            }
            case 3: {
                int etc = slea.readInt();
                if (!c.getPlayer().haveItem(etc, 100, false, true)) break;
                MapleInventoryManipulator.addById(c, ItemMakerHandler.getCreateCrystal(etc), (short)1);
                MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, etc, 100, false, false);
                c.getSession().write((Object)MaplePacketCreator.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            }
            case 4: {
                int itemId = slea.readInt();
                c.getPlayer().updateTick(slea.readInt());
                byte slot = (byte)slea.readInt();
                IItem toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
                if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                    return;
                }
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (!ii.isDropRestricted(itemId) && !ii.isAccountShared(itemId)) {
                    int[] toGive = ItemMakerHandler.getCrystal(itemId, ii.getReqLevel(itemId));
                    MapleInventoryManipulator.addById(c, toGive[0], (byte)toGive[1]);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, slot, (short)1, false);
                }
                c.getSession().write((Object)MaplePacketCreator.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            }
        }
    }

    private static final int getCreateCrystal(int etc) {
        int itemid;
        short level = MapleItemInformationProvider.getInstance().getItemMakeLevel(etc);
        if (level >= 31 && level <= 50) {
            itemid = 4260000;
        } else if (level >= 51 && level <= 60) {
            itemid = 4260001;
        } else if (level >= 61 && level <= 70) {
            itemid = 4260002;
        } else if (level >= 71 && level <= 80) {
            itemid = 4260003;
        } else if (level >= 81 && level <= 90) {
            itemid = 4260004;
        } else if (level >= 91 && level <= 100) {
            itemid = 4260005;
        } else if (level >= 101 && level <= 110) {
            itemid = 4260006;
        } else if (level >= 111 && level <= 120) {
            itemid = 4260007;
        } else if (level >= 121) {
            itemid = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker id");
        }
        return itemid;
    }

    private static final int[] getCrystal(int itemid, int level) {
        int[] all = new int[2];
        all[0] = -1;
        if (level >= 31 && level <= 50) {
            all[0] = 4260000;
        } else if (level >= 51 && level <= 60) {
            all[0] = 4260001;
        } else if (level >= 61 && level <= 70) {
            all[0] = 4260002;
        } else if (level >= 71 && level <= 80) {
            all[0] = 4260003;
        } else if (level >= 81 && level <= 90) {
            all[0] = 4260004;
        } else if (level >= 91 && level <= 100) {
            all[0] = 4260005;
        } else if (level >= 101 && level <= 110) {
            all[0] = 4260006;
        } else if (level >= 111 && level <= 120) {
            all[0] = 4260007;
        } else if (level >= 121 && level <= 200) {
            all[0] = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker type" + level);
        }
        all[1] = GameConstants.isWeapon(itemid) || GameConstants.isOverall(itemid) ? Randomizer.rand(5, 11) : Randomizer.rand(3, 7);
        return all;
    }

    private static final void addEnchantStats(Map<String, Byte> stats, Equip item) {
        boolean success;
        short s = stats.get("incPAD").byteValue();
        if (s != 0) {
            item.setWatk((short)(item.getWatk() + s));
        }
        if ((s = (short)stats.get("incMAD").byteValue()) != 0) {
            item.setMatk((short)(item.getMatk() + s));
        }
        if ((s = (short)stats.get("incACC").byteValue()) != 0) {
            item.setAcc((short)(item.getAcc() + s));
        }
        if ((s = (short)stats.get("incEVA").byteValue()) != 0) {
            item.setAvoid((short)(item.getAvoid() + s));
        }
        if ((s = (short)stats.get("incSpeed").byteValue()) != 0) {
            item.setSpeed((short)(item.getSpeed() + s));
        }
        if ((s = (short)stats.get("incJump").byteValue()) != 0) {
            item.setJump((short)(item.getJump() + s));
        }
        if ((s = (short)stats.get("incMaxHP").byteValue()) != 0) {
            item.setHp((short)(item.getHp() + s));
        }
        if ((s = (short)stats.get("incMaxMP").byteValue()) != 0) {
            item.setMp((short)(item.getMp() + s));
        }
        if ((s = (short)stats.get("incSTR").byteValue()) != 0) {
            item.setStr((short)(item.getStr() + s));
        }
        if ((s = (short)stats.get("incDEX").byteValue()) != 0) {
            item.setDex((short)(item.getDex() + s));
        }
        if ((s = (short)stats.get("incINT").byteValue()) != 0) {
            item.setInt((short)(item.getInt() + s));
        }
        if ((s = (short)stats.get("incLUK").byteValue()) != 0) {
            item.setLuk((short)(item.getLuk() + s));
        }
        if ((s = (short)stats.get("randOption").byteValue()) > 0) {
            success = Randomizer.nextBoolean();
            short ma = item.getMatk();
            short wa = item.getWatk();
            if (wa > 0) {
                item.setWatk((short)(success ? wa + s : wa - s));
            }
            if (ma > 0) {
                item.setMatk((short)(success ? ma + s : ma - s));
            }
        }
        if ((s = (short)stats.get("randStat").byteValue()) > 0) {
            success = Randomizer.nextBoolean();
            short str = item.getStr();
            short dex = item.getDex();
            short luk = item.getLuk();
            short int_ = item.getInt();
            if (str > 0) {
                item.setStr((short)(success ? str + s : str - s));
            }
            if (dex > 0) {
                item.setDex((short)(success ? dex + s : dex - s));
            }
            if (int_ > 0) {
                item.setInt((short)(success ? int_ + s : int_ - s));
            }
            if (luk > 0) {
                item.setLuk((short)(success ? luk + s : luk - s));
            }
        }
    }

    private static final int getRandomGem(List<Pair<Integer, Integer>> rewards) {
        ArrayList<Integer> items = new ArrayList<Integer>();
        for (Pair<Integer, Integer> p : rewards) {
            int itemid = p.getLeft();
            for (int i = 0; i < p.getRight(); ++i) {
                items.add(itemid);
            }
        }
        return (Integer)items.get(Randomizer.nextInt(items.size()));
    }

    private static final int checkRequiredNRemove(MapleClient c, List<Pair<Integer, Integer>> recipe) {
        int itemid = 0;
        for (Pair<Integer, Integer> p : recipe) {
            if (c.getPlayer().haveItem(p.getLeft(), p.getRight(), false, true)) continue;
            return 0;
        }
        for (Pair<Integer, Integer> p : recipe) {
            itemid = p.getLeft();
            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, p.getRight(), false, false);
        }
        return itemid;
    }

    private static final boolean hasSkill(MapleClient c, int reqlvl) {
        if (GameConstants.isKOC(c.getPlayer().getJob())) {
            return c.getPlayer().getSkillLevel(SkillFactory.getSkill(10001007)) >= reqlvl;
        }
        if (GameConstants.isAran(c.getPlayer().getJob())) {
            return c.getPlayer().getSkillLevel(SkillFactory.getSkill(20001007)) >= reqlvl;
        }
        if (GameConstants.isEvan(c.getPlayer().getJob())) {
            return c.getPlayer().getSkillLevel(SkillFactory.getSkill(20011007)) >= reqlvl;
        }
        if (GameConstants.isResist(c.getPlayer().getJob())) {
            return c.getPlayer().getSkillLevel(SkillFactory.getSkill(30001007)) >= reqlvl;
        }
        return c.getPlayer().getSkillLevel(SkillFactory.getSkill(1007)) >= reqlvl;
    }
}

