/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MerchItemPackage;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {
    public static final void UseHiredMerchant(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMap().allowPersonalShop()) {
            byte state = HiredMerchantHandler.checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());
            switch (state) {
                case 1: {
                    c.getPlayer().dropMessage(1, "\u8acb\u5148\u53bb\u627e\u5bcc\u862d\u5fb7\u91cc\u9818\u53d6\u4f60\u4e4b\u524d\u64fa\u7684\u6771\u897f");
                    break;
                }
                case 0: {
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID());
                    if (!merch) {
                        c.getSession().write((Object)PlayerShopPacket.sendTitleBox());
                        break;
                    }
                    c.getPlayer().dropMessage(1, "\u8acb\u63db\u500b\u5730\u65b9\u958b\u6216\u8005\u662f\u4f60\u5df2\u7d93\u6709\u958b\u4e86");
                    break;
                }
                default: {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                    break;
                }
            }
        } else {
            c.getSession().close();
        }
    }

    private static final byte checkExistance(int accid, int charid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?");
            ps.setInt(1, accid);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                rs.close();
                return 1;
            }
            rs.close();
            ps.close();
            return 0;
        }
        catch (SQLException se) {
            return -1;
        }
    }

    public static final void MerchantItemStore(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        byte operation = slea.readByte();
        switch (operation) {
            case 20: {
                slea.readMapleAsciiString();
                int conv = c.getPlayer().getConversation();
                boolean merch = World.hasMerchant(c.getPlayer().getAccountID());
                if (merch) {
                    c.getPlayer().dropMessage(1, "\u8acb\u95dc\u9589\u5546\u5e97\u5f8c\u5728\u8a66\u4e00\u6b21.");
                    c.getPlayer().setConversation(0);
                    break;
                }
                if (conv != 3) break;
                MerchItemPackage pack = HiredMerchantHandler.loadItemFrom_Database(c.getPlayer().getId(), c.getPlayer().getAccountID());
                if (pack == null) {
                    c.getPlayer().dropMessage(1, "\u4f60\u6c92\u6709\u5728\u9019\u908a\u7f6e\u653e\u9053\u5177!");
                    c.getPlayer().setConversation(0);
                    break;
                }
                if (pack.getItems().size() <= 0) {
                    if (!HiredMerchantHandler.check(c.getPlayer(), pack)) {
                        c.getSession().write((Object)PlayerShopPacket.merchItem_Message((byte)34));
                        return;
                    }
                    if (HiredMerchantHandler.deletePackage(c.getPlayer().getId(), c.getPlayer().getAccountID(), pack.getPackageid())) {
                        c.getPlayer().gainMeso(pack.getMesos(), false);
                        c.getSession().write((Object)PlayerShopPacket.merchItem_Message((byte)30));
                        break;
                    }
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                    break;
                }
                c.getSession().write((Object)PlayerShopPacket.merchItemStore_ItemData(pack));
                break;
            }
            case 25: {
                if (c.getPlayer().getConversation() != 3) {
                    return;
                }
                c.getSession().write((Object)PlayerShopPacket.merchItemStore((byte)36));
                break;
            }
            case 26: {
                if (c.getPlayer().getConversation() != 3) {
                    return;
                }
                MerchItemPackage pack = HiredMerchantHandler.loadItemFrom_Database(c.getPlayer().getId(), c.getPlayer().getAccountID());
                if (pack == null) {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                    return;
                }
                if (!HiredMerchantHandler.check(c.getPlayer(), pack)) {
                    c.getSession().write((Object)PlayerShopPacket.merchItem_Message((byte)33));
                    return;
                }
                if (HiredMerchantHandler.deletePackage(c.getPlayer().getId(), c.getPlayer().getAccountID(), pack.getPackageid())) {
                    c.getPlayer().gainMeso(pack.getMesos(), false);
                    for (IItem item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                    }
                    c.getSession().write((Object)PlayerShopPacket.merchItem_Message((byte)29));
                    break;
                }
                c.getPlayer().dropMessage(1, "An unknown error occured.");
                break;
            }
            case 27: {
                c.getPlayer().setConversation(0);
            }
        }
    }

    private static final boolean check(MapleCharacter chr, MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        short eq = 0;
        short use = 0;
        short setup = 0;
        short etc = 0;
        short cash = 0;
        for (IItem item : pack.getItems()) {
            MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq = (byte)(eq + 1);
                continue;
            }
            if (invtype == MapleInventoryType.USE) {
                use = (byte)(use + 1);
                continue;
            }
            if (invtype == MapleInventoryType.SETUP) {
                setup = (byte)(setup + 1);
                continue;
            }
            if (invtype == MapleInventoryType.ETC) {
                etc = (byte)(etc + 1);
                continue;
            }
            if (invtype != MapleInventoryType.CASH) continue;
            cash = (byte)(cash + 1);
        }
        return chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > eq && chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() > use && chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() > setup && chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() > etc && chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() > cash;
    }

    private static final boolean deletePackage(int charid, int accid, int packageid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where characterid = ? OR accountid = ? OR packageid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accid);
            ps.setInt(3, packageid);
            ps.execute();
            ps.close();
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid, accid, charid);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    private static final MerchItemPackage loadItemFrom_Database(int charid, int accountid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accountid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            int packageid = rs.getInt("PackageId");
            MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));
            ps.close();
            rs.close();
            Map<Integer, Pair<IItem, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid, accountid, charid);
            if (items != null) {
                ArrayList<IItem> iters = new ArrayList<IItem>();
                for (Pair<IItem, MapleInventoryType> z : items.values()) {
                    iters.add((IItem)z.left);
                }
                pack.setItems(iters);
            }
            return pack;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

