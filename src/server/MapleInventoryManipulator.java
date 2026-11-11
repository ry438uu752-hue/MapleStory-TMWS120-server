/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.InventoryException;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import server.AutobanManager;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleItemInformationProvider;
import server.Randomizer;
import tools.MaplePacketCreator;
import tools.packet.MTSCSPacket;

public class MapleInventoryManipulator {
    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        IItem ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().getSession().write((Object)MTSCSPacket.showBoughtCSItem(ring, sn, chr.getClient().getAccID()));
    }

    public static boolean addbyItem(MapleClient c, IItem item) {
        return MapleInventoryManipulator.addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(MapleClient c, IItem item, boolean fromcs) {
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
            }
            return newSlot;
        }
        if (!fromcs) {
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item));
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            uniqueid = pet != null ? pet.getUniqueId() : MapleInventoryIdentifier.getInstance();
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }

    public static boolean addById(MapleClient c, int itemId, short quantity) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, null, null, 0L);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, owner, null, 0L);
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner) {
        return MapleInventoryManipulator.addId(c, itemId, quantity, owner, null, 0L);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, owner, pet, 0L);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period) {
        return MapleInventoryManipulator.addId(c, itemId, quantity, owner, pet, period) >= 0;
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return -1;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = MapleInventoryManipulator.getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals((Object)MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemId);
            List<IItem> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) {
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        Item eItem = (Item)i.next();
                        short oldQ = eItem.getQuantity();
                        if (oldQ >= slotMax || !eItem.getOwner().equals(owner) && owner != null || eItem.getExpiration() != -1L) continue;
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ - oldQ));
                        eItem.setQuantity(newQ);
                        c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, false));
                    }
                }
                while (quantity > 0) {
                    short newQ = (short)Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity = (short)(quantity - newQ);
                        Item nItem = new Item(itemId, (short)0, newQ, (byte)0, uniqueid);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                            c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                            return -1;
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0L) {
                            nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                            c.getPlayer().addPet(pet);
                        }
                        c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                        if (!GameConstants.isRechargable(itemId) || quantity != 0) continue;
                        break;
                    }
                    c.getPlayer().havePartyQuest(itemId);
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return (byte)newSlot;
                }
            } else {
                Item nItem = new Item(itemId, (short)0, quantity, (byte)0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        } else if (quantity == 1) {
            IItem nEquip = ii.getEquipById(itemId);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            nEquip.setUniqueId(uniqueid);
            if (period > 0L) {
                nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            if ((newSlot = (short)c.getPlayer().getInventory(type).addItem(nEquip)) == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return -1;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nEquip));
        } else {
            throw new InventoryException("Trying to create equip with non-one quantity");
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte)newSlot;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static IItem addbyId_Gachapon(MapleClient c, int itemId, short quantity) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return null;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (!type.equals((Object)MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemId);
            List<IItem> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                short newQ;
                IItem nItem = null;
                boolean recieved = false;
                if (existing.size() > 0) {
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        nItem = (Item)i.next();
                        short oldQ = nItem.getQuantity();
                        if (oldQ >= slotMax) continue;
                        recieved = true;
                        short newQ2 = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ2 - oldQ));
                        nItem.setQuantity(newQ2);
                        c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, nItem, false));
                    }
                }
                while (quantity > 0 && (newQ = (short)Math.min(quantity, slotMax)) != 0) {
                    quantity = (short)(quantity - newQ);
                    nItem = new Item(itemId, (short)0, newQ, (byte)0);
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1 && recieved) {
                        return nItem;
                    }
                    if (newSlot == -1) {
                        return null;
                    }
                    recieved = true;
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                    if (!GameConstants.isRechargable(itemId) || quantity != 0) continue;
                    break;
                }
                if (!recieved) return null;
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
            Item nItem = new Item(itemId, (short)0, quantity, (byte)0);
            short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
            if (newSlot == -1) {
                return null;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
            c.getPlayer().havePartyQuest(nItem.getItemId());
            return nItem;
        }
        if (quantity != 1) throw new InventoryException("Trying to create equip with non-one quantity");
        Equip item = ii.randomizeStats((Equip)ii.getEquipById(itemId));
        short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            return null;
        }
        c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, true));
        c.getPlayer().havePartyQuest(item.getItemId());
        return item;
    }

    public static boolean addFromDrop(MapleClient c, IItem item, boolean show) {
        return MapleInventoryManipulator.addFromDrop(c, item, show, false);
    }

    public static boolean addFromDrop(MapleClient c, IItem item, boolean show, boolean enhance) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return false;
        }
        int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals((Object)MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, item.getItemId());
            List<IItem> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        Item eItem = (Item)i.next();
                        short oldQ = eItem.getQuantity();
                        if (oldQ >= slotMax || !item.getOwner().equals(eItem.getOwner()) || item.getExpiration() != eItem.getExpiration()) continue;
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ - oldQ));
                        eItem.setQuantity(newQ);
                        c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, true));
                    }
                }
                while (quantity > 0) {
                    short newQ = (short)Math.min(quantity, slotMax);
                    quantity = (short)(quantity - newQ);
                    Item nItem = new Item(item.getItemId(), (short)0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ));
                        return false;
                    }
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            } else {
                Item nItem = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        } else if (quantity == 1) {
            short newSlot;
            if (enhance) {
                item = MapleInventoryManipulator.checkEnhanced(item, c.getPlayer());
            }
            if ((newSlot = c.getPlayer().getInventory(type).addItem(item)) == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, true));
        } else {
            throw new RuntimeException("Trying to create equip with non-one quantity");
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write((Object)MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    private static final IItem checkEnhanced(IItem before, MapleCharacter chr) {
        Equip eq;
        if (before instanceof Equip && (eq = (Equip)before).getState() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && Randomizer.nextInt(100) > 80) {
            eq.resetPotential();
        }
        return before;
    }

    private static int rand(int min, int max) {
        return Math.abs(Randomizer.rand(min, max));
    }

    public static boolean checkSpace(MapleClient c, int itemid, int quantity, String owner) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (c == null || c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        if (!type.equals((Object)MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemid);
            List<IItem> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid) && existing.size() > 0) {
                for (IItem eItem : existing) {
                    short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                    }
                    if (quantity > 0) continue;
                    break;
                }
            }
            int numSlotsNeeded = slotMax > 0 ? (int)Math.ceil((double)quantity / (double)slotMax) : 1;
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !c.getPlayer().getInventory(type).isFull();
    }

    public static void removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop) {
        MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static void removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return;
        }
        IItem item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (item.getQuantity() == 0 && !allowZero) {
                c.getSession().write((Object)MaplePacketCreator.clearInventoryItem(type, item.getPosition(), fromDrop));
            } else {
                c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, (Item)item, fromDrop));
            }
        }
    }

    public static boolean removeById(MapleClient c, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
        int remremove = quantity;
        for (IItem item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (remremove <= item.getQuantity()) {
                MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), (short)remremove, fromDrop, consume);
                remremove = 0;
                break;
            }
            remremove -= item.getQuantity();
            MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
        }
        return remremove <= 0;
    }

    public static void move(MapleClient c, MapleInventoryType type, short src, short dst) {
        if (src < 0 || dst < 0 || dst > c.getPlayer().getInventory(type).getSlotLimit() || src == dst) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        IItem source = c.getPlayer().getInventory(type).getItem(src);
        IItem initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        int olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        short oldsrcQ = source.getQuantity();
        short slotMax = ii.getSlotMax(c, source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (!type.equals((Object)MapleInventoryType.EQUIP) && initialTarget != null && initialTarget.getItemId() == source.getItemId() && initialTarget.getOwner().equals(source.getOwner()) && initialTarget.getExpiration() == source.getExpiration() && !GameConstants.isRechargable(source.getItemId()) && !type.equals((Object)MapleInventoryType.CASH)) {
            if (olddstQ + oldsrcQ > slotMax) {
                c.getSession().write((Object)MaplePacketCreator.moveAndMergeWithRestInventoryItem(type, src, dst, (short)(olddstQ + oldsrcQ - slotMax), slotMax));
            } else {
                c.getSession().write((Object)MaplePacketCreator.moveAndMergeInventoryItem(type, src, dst, ((Item)c.getPlayer().getInventory(type).getItem(dst)).getQuantity()));
            }
        } else {
            c.getSession().write((Object)MaplePacketCreator.moveInventoryItem(type, src, dst));
        }
    }

    public static void equip(MapleClient c, short src, short dst) {
        byte flag;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        PlayerStats statst = c.getPlayer().getStat();
        Equip source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null || source.getDurability() == 0) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (dst >= -999 && dst < -99 && stats.get("cash") == 0) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11) {
            AutobanManager.getInstance().autoban(c, "Equipment hack, itemid " + source.getItemId() + " to slot " + dst);
            return;
        }
        if (!ii.isCash(source.getItemId()) && !GameConstants.isMountItemAvailable(source.getItemId(), c.getPlayer().getJob())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (GameConstants.isKatara(source.getItemId())) {
            dst = (short)-10;
        }
        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        switch (dst) {
            case -6: {
                IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5);
                if (top == null || !GameConstants.isOverall(top.getItemId())) break;
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                MapleInventoryManipulator.unequip(c, (short)-5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -5: {
                IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5);
                IItem bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    MapleInventoryManipulator.unequip(c, (short)-5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom == null || !GameConstants.isOverall(source.getItemId())) break;
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                MapleInventoryManipulator.unequip(c, (short)-6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -10: {
                IItem weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() == 900 || chr.getJob() >= 430 && chr.getJob() <= 434) && weapon != null && GameConstants.isDagger(weapon.getItemId())) break;
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                if (weapon == null || !GameConstants.isTwoHanded(weapon.getItemId())) break;
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                MapleInventoryManipulator.unequip(c, (short)-11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -11: {
                IItem shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
                if (shield == null || !GameConstants.isTwoHanded(source.getItemId())) break;
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                MapleInventoryManipulator.unequip(c, (short)-10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
        }
        source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (stats.get("equipTradeBlock") == 1 && !ItemFlag.UNTRADEABLE.check(flag = source.getFlag())) {
            flag = (byte)(flag | ItemFlag.UNTRADEABLE.getValue());
            source.setFlag(flag);
            c.getSession().write((Object)MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                chr.cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
            if (chr.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.LIGHTNING_CHARGE);
            }
        }
        if (source.getItemId() == 0x111EE1) {
            chr.startFairySchedule(true, true);
        }
        c.getSession().write((Object)MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)2));
        chr.equipChanged();
    }

    public static void unequip(MapleClient c, short src, short dst) {
        Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        if (dst < 0 || source == null) {
            return;
        }
        if (target != null && src <= 0) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            return;
        }
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (c.getPlayer().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
        }
        if (source.getItemId() == 0x111EE1) {
            c.getPlayer().cancelFairySchedule(true);
        }
        c.getSession().write((Object)MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)1));
        c.getPlayer().equipChanged();
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity) {
        return MapleInventoryManipulator.drop(c, type, src, quantity, false);
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null) {
            return false;
        }
        IItem source = c.getPlayer().getInventory(type).getItem(src);
        if (source == null || !npcInduced && GameConstants.isPet(source.getItemId())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        byte flag = source.getFlag();
        if (quantity > source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || quantity != 1 && type == MapleInventoryType.EQUIP) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            IItem target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short)(source.getQuantity() - quantity));
            c.getSession().write((Object)MaplePacketCreator.dropInventoryItemUpdate(type, source));
            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            } else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
            } else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
            }
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            c.getSession().write((Object)MaplePacketCreator.dropInventoryItem(src < 0 ? MapleInventoryType.EQUIP : type, src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            } else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
            } else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
            }
        }
        return true;
    }
}

