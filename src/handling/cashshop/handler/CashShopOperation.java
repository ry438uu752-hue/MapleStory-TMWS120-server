/*
 * Decompiled with CFR 0.152.
 */
package handling.cashshop.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.MTSOperation;
import handling.channel.ChannelServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;

public class CashShopOperation {
    public static void LeaveCS(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(1, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write((Object)MaplePacketCreator.getChannelChange(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        }
        finally {
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }

    public static void EnterCS(int playerid, MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                c.getSession().close();
                return;
            }
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) {
            c.getSession().close();
            return;
        }
        byte state = c.getLoginState();
        boolean allowLogin = false;
        if (!(state != 1 && state != 6 || World.isCharacterListConnected(c.loadCharacterNames(c.getWorld())))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        if (mts) {
            CashShopServer.getPlayerStorageMTS().registerPlayer(chr);
            c.getSession().write((Object)MTSCSPacket.startMTS(chr, c));
            MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
        } else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write((Object)MTSCSPacket.warpCS(c));
            CashShopOperation.CSUpdate(c);
        }
    }

    public static void CSUpdate(MapleClient c) {
        c.getSession().write((Object)MTSCSPacket.getCSGifts(c));
        c.getSession().write((Object)MTSCSPacket.showAcc(c));
        CashShopOperation.doCSPackets(c);
        c.getSession().write((Object)MTSCSPacket.sendWishList(c.getPlayer(), false));
    }

    public static void CouponCode(String code, MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;
        try {
            validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (validcode) {
            try {
                type = MapleCharacterUtil.getNXCodeType(code);
                item = MapleCharacterUtil.getNXCodeItem(code);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            if (type != 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            HashMap<Integer, IItem> itemz = new HashMap<Integer, IItem>();
            int maplePoints = 0;
            int mesos = 0;
            switch (type) {
                case 1: 
                case 2: {
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                }
                case 3: {
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                        CashShopOperation.doCSPackets(c);
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short)1, "");
                    if (slot <= -1) {
                        c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                        CashShopOperation.doCSPackets(c);
                        return;
                    }
                    itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    break;
                }
                case 4: {
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    break;
                }
                case 5: {
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                }
            }
            c.getSession().write((Object)MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(validcode ? 165 : 167));
        }
        CashShopOperation.doCSPackets(c);
    }

    public static final void BuyCashItem(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte action = slea.readByte();
        if (action == 0) {
            slea.skip(2);
            CashShopOperation.CouponCode(slea.readMapleAsciiString(), c);
        } else if (action == 3) {
            int type = slea.readByte() == 0 ? 1 : 2;
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item != null && chr.getCSPoints(type) >= item.getPrice()) {
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(166));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                for (int i : GameConstants.cashBlock) {
                    if (item.getId() != i) continue;
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                chr.modifyCSPoints(type, -item.getPrice(), false);
                IItem itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    chr.getCashInventory().addToInventory(itemz);
                    c.getSession().write((Object)MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                }
            } else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            }
        } else if (action == 4 || action == 38) {
            slea.readMapleAsciiString();
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            String partnerName = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                CashShopOperation.doCSPackets(c);
                return;
            }
            Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft() <= 0 || info.getLeft().intValue() == c.getPlayer().getId() || info.getRight().getLeft().intValue() == c.getAccID()) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(162));
                CashShopOperation.doCSPackets(c);
                return;
            }
            if (!item.genderEquals(info.getRight().getRight())) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(194));
                CashShopOperation.doCSPackets(c);
                return;
            }
            for (int i : GameConstants.cashBlock) {
                if (item.getId() != i) continue;
                c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                CashShopOperation.doCSPackets(c);
                return;
            }
            c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
            c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
            c.getSession().write((Object)MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, action != 4));
        } else if (action == 5) {
            chr.clearWishlist();
            if (slea.available() < 40L) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                CashShopOperation.doCSPackets(c);
                return;
            }
            int[] wishlist = new int[10];
            for (int i = 0; i < 10; ++i) {
                wishlist[i] = slea.readInt();
            }
            chr.setWishlist(wishlist);
            c.getSession().write((Object)MTSCSPacket.sendWishList(chr, true));
        } else if (action == 6) {
            boolean coupon;
            int useCash = slea.readByte() == 0 ? 1 : 2;
            boolean bl = coupon = slea.readByte() > 0;
            if (coupon) {
                MapleInventoryType type = CashShopOperation.getInventoryType(slea.readInt());
                if (chr.getCSPoints(useCash) >= 100 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(useCash, -100, false);
                    chr.getInventory(type).addSlot((byte)8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
                }
            } else {
                byte inv = slea.readByte();
                MapleInventoryType type = MapleInventoryType.getByType(inv);
                if (chr.getCSPoints(useCash) >= 100 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(useCash, -100, false);
                    chr.getInventory(type).addSlot((byte)4);
                    c.getSession().write((Object)MTSCSPacket.increasedInvSlots(inv, chr.getInventory(type).getSlotLimit()));
                } else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
                }
            }
        } else if (action == 7) {
            int useCash;
            int n = useCash = slea.readByte() == 0 ? 1 : 2;
            if (chr.getCSPoints(useCash) >= 100 && chr.getStorage().getSlots() < 45) {
                chr.modifyCSPoints(useCash, -100, false);
                chr.getStorage().increaseSlots((byte)4);
                chr.getStorage().saveToDB();
                chr.dropMessage(1, "\u5009\u5eab\u6b04\u4f4d\u589e\u52a0\u81f3 " + chr.getStorage().getSlots());
            } else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
            }
        } else if (action == 8) {
            slea.readByte();
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            int slots = c.getCharacterSlots();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || slots > 15) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                CashShopOperation.doCSPackets(c);
                return;
            }
            c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
            if (c.gainCharacterSlot()) {
                c.getSession().write((Object)MTSCSPacket.increasedStorageSlots(slots + 1));
            } else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            }
        } else if (action == 14) {
            IItem item = c.getPlayer().getCashInventory().findByCashId((int)slea.readLong());
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                IItem item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                        c.getPlayer().addPet(item_.getPet());
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.getSession().write((Object)MTSCSPacket.confirmFromCSInventory(item_, pos));
                } else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                }
            } else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
            }
        } else if (action == 15) {
            int uniqueid = (int)slea.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
            IItem item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                IItem item_ = item.copy();
                c.getPlayer().getInventory(type).removeItem(item.getPosition(), item.getQuantity(), false);
                if (item_.getPet() != null) {
                    c.getPlayer().removePetCS(item_.getPet());
                }
                item_.setPosition((short)0);
                c.getPlayer().getCashInventory().addToInventory(item_);
                c.getSession().write((Object)MTSCSPacket.confirmToCSInventory(item, c.getAccID(), 0));
            } else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
            }
        } else {
            if (action == 27) {
                slea.readMapleAsciiString();
                IItem item = c.getPlayer().getCashInventory().findByCashId((int)slea.readLong());
                if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    chr.modifyCSPoints(2, 5, false);
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.getPlayer().dropMessage(1, "\u7372\u5f97\u4e86 5 \u9ede\u6953\u8449\u9ede\u6578");
                }
                CashShopOperation.doCSPackets(c);
                return;
            }
            if (action == 30 || action == 36) {
                slea.readMapleAsciiString();
                CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                String partnerName = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(166));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                for (int i : GameConstants.cashBlock) {
                    if (item.getId() != i) continue;
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft().intValue() == c.getPlayer().getId()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(180));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (info.getRight().getLeft().intValue() == c.getAccID()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(163));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (info.getRight().getRight().intValue() == c.getPlayer().getGender() && action == 30) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(211));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft(), item.getSN());
                if (err != 1) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                c.getSession().write((Object)MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, false));
            } else if (action == 31) {
                slea.skip(1);
                CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                List<CashItemInfo> ccc = null;
                if (item != null) {
                    ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
                }
                if (item == null || ccc == null || c.getPlayer().getCSPoints(1) < item.getPrice()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(166));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100 - ccc.size()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (item.getId() != iz) continue;
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                HashMap<Integer, IItem> ccz = new HashMap<Integer, IItem>();
                for (CashItemInfo i : ccc) {
                    for (int iz : GameConstants.cashBlock) {
                        if (i.getId() != iz) continue;
                    }
                    IItem itemz = c.getPlayer().getCashInventory().toItem(i);
                    if (itemz == null || itemz.getUniqueId() <= 0 || itemz.getItemId() != i.getId()) continue;
                    ccz.put(i.getSN(), itemz);
                    c.getPlayer().getCashInventory().addToInventory(itemz);
                }
                chr.modifyCSPoints(1, -item.getPrice(), false);
                c.getSession().write((Object)MTSCSPacket.showBoughtCSPackage(ccz, c.getAccID()));
            } else if (action == 33) {
                CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getMeso() < item.getPrice()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(202));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (item.getId() != iz) continue;
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short)item.getCount(), null);
                if (pos < 0) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(209));
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                chr.gainMeso(-item.getPrice(), false);
                c.getSession().write((Object)MTSCSPacket.showBoughtCSQuestItem(item.getPrice(), (short)item.getCount(), pos, item.getId()));
            } else {
                if (action == 53) {
                    CashShopOperation.doCSPackets(c);
                    return;
                }
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            }
        }
        CashShopOperation.doCSPackets(c);
    }

    public static final void SendCashGift(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.readMapleAsciiString();
        CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
        String partnerName = slea.readMapleAsciiString();
        String msg = slea.readMapleAsciiString();
        if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            CashShopOperation.doCSPackets(c);
            return;
        }
        Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
        if (info == null || info.getLeft() <= 0 || info.getLeft().intValue() == c.getPlayer().getId() || info.getRight().getLeft().intValue() == c.getAccID()) {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(212));
            CashShopOperation.doCSPackets(c);
            return;
        }
        if (!item.genderEquals(info.getRight().getRight())) {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(196));
            CashShopOperation.doCSPackets(c);
            return;
        }
        for (int i : GameConstants.cashBlock) {
            if (item.getId() != i) continue;
            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
            CashShopOperation.doCSPackets(c);
            return;
        }
        c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
        c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
        c.getSession().write((Object)MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, true));
    }

    private static final MapleInventoryType getInventoryType(int id) {
        switch (id) {
            case 50200075: {
                return MapleInventoryType.EQUIP;
            }
            case 50200074: {
                return MapleInventoryType.USE;
            }
            case 50200073: {
                return MapleInventoryType.ETC;
            }
        }
        return MapleInventoryType.UNDEFINED;
    }

    private static final void doCSPackets(MapleClient c) {
        c.getSession().write((Object)MTSCSPacket.getCSInventory(c));
        c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write((Object)MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}

