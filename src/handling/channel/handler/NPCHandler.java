/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.RockPaperScissors;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.SendPacketOpcode;
import java.util.List;
import java.util.Map;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import server.AutobanManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleStorage;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

public class NPCHandler {
    public static final void NPCAnimation(SeekableLittleEndianAccessor slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
        int length = (int)slea.available();
        if (length == 6) {
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
        } else if (length > 6) {
            mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
            mplew.write(slea.read(length - 9));
        } else {
            return;
        }
        c.getSession().write((Object)mplew.getPacket());
    }

    public static final void NPCShop(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte bmode = slea.readByte();
        if (chr == null) {
            return;
        }
        switch (bmode) {
            case 0: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                slea.skip(2);
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.buy(c, itemId, quantity);
                break;
            }
            case 1: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte)slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case 2: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte)slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            default: {
                chr.setConversation(0);
            }
        }
    }

    public static final void NPCTalk(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        MapleNPC npc = chr.getMap().getNPCByOid(slea.readInt());
        if (npc == null) {
            return;
        }
        if (chr.getConversation() != 0) {
            chr.dropMessage(1, "\u89d2\u8272\u72c0\u614b\u7570\u5e38\uff0c\u8acb\u4f7f\u7528@\u89e3\u5361/@ea \u4f86\u89e3\u9664\u7570\u5e38\u72c0\u614b");
            return;
        }
        if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
    }

    public static final void QuestAction(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte action = slea.readByte();
        short quest = slea.readShort();
        if (quest < 0) {
            quest = (short)(quest + 65536);
        }
        if (chr == null) {
            return;
        }
        MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: {
                chr.updateTick(slea.readInt());
                int itemid = slea.readInt();
                MapleQuest.getInstance(quest).RestoreLostItem(chr, itemid);
                break;
            }
            case 1: {
                int npc = slea.readInt();
                q.start(chr, npc);
                break;
            }
            case 2: {
                int npc = slea.readInt();
                chr.updateTick(slea.readInt());
                if (slea.available() >= 4L) {
                    q.complete(chr, npc, slea.readInt());
                    break;
                }
                q.complete(chr, npc);
                break;
            }
            case 3: {
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                    break;
                }
                chr.dropMessage(1, "You may not forfeit this quest.");
                break;
            }
            case 4: {
                int npc = slea.readInt();
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: {
                int npc = slea.readInt();
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.getSession().write((Object)MaplePacketCreator.showSpecialEffect(10));
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showSpecialEffect(chr.getId(), 10), false);
                break;
            }
        }
    }

    public static final void Storage(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte mode = slea.readByte();
        if (chr == null) {
            return;
        }
        MapleStorage storage = chr.getStorage();
        switch (mode) {
            case 4: {
                byte type = slea.readByte();
                byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
                IItem item = storage.takeOut(slot);
                if (item != null) {
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "\u4f60\u7684\u7269\u54c1\u6514\u5df2\u7d93\u6eff\u4e86..");
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                    }
                    storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    break;
                }
                return;
            }
            case 5: {
                byte slot = (byte)slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (quantity < 1) {
                    return;
                }
                if (storage.isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getStorageFull());
                    return;
                }
                if (chr.getMeso() < 100) {
                    chr.dropMessage(1, "\u4f60\u6c92\u6709\u8db3\u5920\u7684\u6953\u5e63.");
                } else {
                    MapleInventoryType type = GameConstants.getInventoryType(itemId);
                    IItem item = chr.getInventory(type).getItem(slot).copy();
                    if (GameConstants.isPet(item.getItemId())) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    byte flag = item.getFlag();
                    if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    if (item.getItemId() == itemId && (item.getQuantity() >= quantity || GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId))) {
                        if (ii.isDropRestricted(item.getItemId())) {
                            if (ItemFlag.KARMA_EQ.check(flag)) {
                                item.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                            } else if (ItemFlag.KARMA_USE.check(flag)) {
                                item.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                            } else {
                                c.getSession().write((Object)MaplePacketCreator.enableActions());
                                return;
                            }
                        }
                        if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                            quantity = item.getQuantity();
                        }
                        chr.gainMeso(-100, false, true, false);
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                        item.setQuantity(quantity);
                        storage.store(item);
                    } else {
                        AutobanManager.getInstance().addPoints(c, 1000, 0L, "Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ") or quantity not in posession (" + quantity + "/" + item.getQuantity() + ")");
                        return;
                    }
                }
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            }
            case 7: {
                int meso = slea.readInt();
                int storageMesos = storage.getMeso();
                int playerMesos = chr.getMeso();
                if (meso > 0 && storageMesos >= meso || meso < 0 && playerMesos >= -meso) {
                    if (meso < 0 && storageMesos - meso < 0 ? -(meso = -(Integer.MAX_VALUE - storageMesos)) > playerMesos : meso > 0 && playerMesos + meso < 0 && (meso = Integer.MAX_VALUE - playerMesos) > storageMesos) {
                        return;
                    }
                } else {
                    AutobanManager.getInstance().addPoints(c, 1000, 0L, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
                    return;
                }
                storage.setMeso(storageMesos - meso);
                chr.gainMeso(meso, false, true, false);
                storage.sendMeso(c);
                break;
            }
            case 8: {
                storage.close();
                chr.setConversation(0);
                break;
            }
            default: {
                System.out.println("Unhandled Storage mode : " + mode);
            }
        }
    }

    public static final void NPCMoreTalk(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte lastMsg = slea.readByte();
        byte action = slea.readByte();
        NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if (cm == null || c.getPlayer().getConversation() == 0 || cm.getLastMsg() != lastMsg) {
            return;
        }
        cm.setLastMsg((byte)-1);
        if (lastMsg == 3) {
            if (action != 0) {
                cm.setGetText(slea.readMapleAsciiString());
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
        } else {
            int selection = -1;
            if (slea.available() >= 4L) {
                selection = slea.readInt();
            } else if (slea.available() > 0L) {
                selection = slea.readByte();
            }
            if (lastMsg == 4 && selection == -1) {
                cm.dispose();
                return;
            }
            if (selection >= -1 && action != -1) {
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                }
            } else {
                cm.dispose();
            }
        }
    }

    public static final void repairAll(MapleClient c) {
        MapleInventoryType[] types;
        if (c.getPlayer().getMapId() != 240000000) {
            return;
        }
        int price = 0;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ArrayMap<Equip, Integer> eqs = new ArrayMap<Equip, Integer>();
        for (MapleInventoryType type : types = new MapleInventoryType[]{MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED}) {
            for (IItem item : c.getPlayer().getInventory(type)) {
                Map<String, Integer> eqStats;
                Equip eq;
                if (!(item instanceof Equip) || (eq = (Equip)item).getDurability() < 0 || (eqStats = ii.getEquipStats(eq.getItemId())).get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) continue;
                double rPercentage = 100.0 - Math.ceil((double)eq.getDurability() * 1000.0 / ((double)eqStats.get("durability").intValue() * 10.0));
                eqs.put(eq, eqStats.get("durability"));
                price += (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        for (Map.Entry eqqz : eqs.entrySet()) {
            Equip ez = (Equip)eqqz.getKey();
            ez.setDurability((Integer)eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }

    public static final void repair(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000 || slea.available() < 4L) {
            return;
        }
        int position = slea.readInt();
        MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        IItem item = c.getPlayer().getInventory(type).getItem((byte)position);
        if (item == null) {
            return;
        }
        Equip eq = (Equip)item;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        double rPercentage = 100.0 - Math.ceil((double)eq.getDurability() * 1000.0 / ((double)eqStats.get("durability").intValue() * 10.0));
        int price = (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }

    public static final void UpdateQuest(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

    public static final void UseItemQuest(SeekableLittleEndianAccessor slea, MapleClient c) {
        short slot = slea.readShort();
        int itemId = slea.readInt();
        IItem item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        short qid = slea.readShort();
        slea.readShort();
        MapleQuest quest = MapleQuest.getInstance(qid);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (IItem i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 != 422 || (questItemInfo = ii.questItemInfo(i.getItemId())) == null || questItemInfo.getLeft() != qid || !questItemInfo.getRight().contains(itemId)) continue;
            found = true;
            break;
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            int newData = slea.readInt();
            MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short)1, false);
            }
        }
    }

    public static final void RPSGame(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (slea.available() == 0L || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        byte mode = slea.readByte();
        switch (mode) {
            case 0: 
            case 5: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
                break;
            }
            case 1: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().answer(c, slea.readByte())) break;
                c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 2: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().timeOut(c)) break;
                c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 3: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().nextRound(c)) break;
                c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 4: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
            }
        }
    }
}

