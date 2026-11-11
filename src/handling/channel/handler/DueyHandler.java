/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.MapleDueyActions;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

public class DueyHandler {
    public static final void DueyOperation(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte operation = slea.readByte();
        switch (operation) {
            case 1: {
                String AS13Digit = slea.readMapleAsciiString();
                int conv = c.getPlayer().getConversation();
                if (conv != 2) break;
                c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)10, DueyHandler.loadItems(c.getPlayer())));
                break;
            }
            case 3: {
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                byte inventId = slea.readByte();
                short itemPos = slea.readShort();
                short amount = slea.readShort();
                int mesos = slea.readInt();
                String recipient = slea.readMapleAsciiString();
                boolean quickdelivery = slea.readByte() > 0;
                int finalcost = mesos + GameConstants.getTaxAmount(mesos) + (quickdelivery ? 0 : 5000);
                if (mesos >= 0 && mesos <= 100000000 && c.getPlayer().getMeso() >= finalcost) {
                    int accid = MapleCharacterUtil.getIdByName(recipient);
                    if (accid != -1) {
                        if (accid != c.getAccID()) {
                            boolean recipientOn = false;
                            Object rClient = null;
                            if (inventId > 0) {
                                MapleInventoryType inv = MapleInventoryType.getByType(inventId);
                                IItem item = c.getPlayer().getInventory(inv).getItem((byte)itemPos);
                                if (item == null) {
                                    c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                                    return;
                                }
                                byte flag = item.getFlag();
                                if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                                    return;
                                }
                                if (c.getPlayer().getItemQuantity(item.getItemId(), false) >= amount) {
                                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                    if (!ii.isDropRestricted(item.getItemId()) && !ii.isAccountShared(item.getItemId())) {
                                        if (DueyHandler.addItemToDB(item, amount, mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte)itemPos, item.getQuantity(), true);
                                            } else {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte)itemPos, amount, true, false);
                                            }
                                            c.getPlayer().gainMeso(-finalcost, false);
                                            c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)19, null));
                                            break;
                                        }
                                        c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                                        break;
                                    }
                                    c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                                    break;
                                }
                                c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                                break;
                            }
                            if (DueyHandler.addMesoToDB(mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                c.getPlayer().gainMeso(-finalcost, false);
                                c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)19, null));
                                break;
                            }
                            c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                            break;
                        }
                        c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)15, null));
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)14, null));
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)12, null));
                break;
            }
            case 5: {
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                int packageid = slea.readInt();
                MapleDueyActions dp = DueyHandler.loadSingleItem(packageid, c.getPlayer().getId());
                if (dp == null) {
                    return;
                }
                if (dp.getItem() != null && !MapleInventoryManipulator.checkSpace(c, dp.getItem().getItemId(), dp.getItem().getQuantity(), dp.getItem().getOwner())) {
                    c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)16, null));
                    return;
                }
                if (dp.getMesos() < 0 || dp.getMesos() + c.getPlayer().getMeso() < 0) {
                    c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)17, null));
                    return;
                }
                DueyHandler.removeItemFromDB(packageid, c.getPlayer().getId());
                if (dp.getItem() != null) {
                    MapleInventoryManipulator.addFromDrop(c, dp.getItem(), false);
                }
                if (dp.getMesos() != 0) {
                    c.getPlayer().gainMeso(dp.getMesos(), false);
                }
                c.getSession().write((Object)MaplePacketCreator.removeItemFromDuey(false, packageid));
                break;
            }
            case 6: {
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                int packageid = slea.readInt();
                DueyHandler.removeItemFromDB(packageid, c.getPlayer().getId());
                c.getSession().write((Object)MaplePacketCreator.removeItemFromDuey(true, packageid));
                break;
            }
            case 8: {
                c.getPlayer().setConversation(0);
                break;
            }
            default: {
                System.out.println("Unhandled Duey operation : " + slea.toString());
            }
        }
    }

    private static final boolean addMesoToDB(int mesos, String sName, int recipientID, boolean isOn) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, isOn ? 0 : 1);
            ps.setInt(6, 3);
            ps.executeUpdate();
            ps.close();
            return true;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    private static final boolean addItemToDB(IItem item, int quantity, int mesos, String sName, int recipientID, boolean isOn) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis() + 1296000000L);
            ps.setInt(5, isOn ? 0 : 1);
            ps.setInt(6, item.getType());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ItemLoader.DUEY.saveItems(Collections.singletonList(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId()))), rs.getInt(1));
            }
            rs.close();
            ps.close();
            return true;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public static final List<MapleDueyActions> loadItems(MapleCharacter chr) {
        LinkedList<MapleDueyActions> packages = new LinkedList<MapleDueyActions>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueypackages WHERE RecieverId = ?");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int packageid = rs.getInt("packageid");
                MapleDueyActions dueypack = DueyHandler.getItemByPID(packageid);
                dueypack.setSender(rs.getString("SenderName"));
                dueypack.setMesos(rs.getInt("Mesos"));
                dueypack.setSentTime(rs.getLong("TimeStamp"));
                if (dueypack.getSentTime() > System.currentTimeMillis()) {
                    packages.add(dueypack);
                    continue;
                }
                DueyHandler.removeItemFromDB(packageid, chr.getId());
            }
            rs.close();
            ps.close();
            return packages;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public static final MapleDueyActions loadSingleItem(int packageid, int charid) {
        LinkedList<MapleDueyActions> packages = new LinkedList<MapleDueyActions>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueypackages WHERE PackageId = ? and RecieverId = ?");
            ps.setInt(1, packageid);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MapleDueyActions dueypack = DueyHandler.getItemByPID(packageid);
                dueypack.setSender(rs.getString("SenderName"));
                dueypack.setMesos(rs.getInt("Mesos"));
                dueypack.setSentTime(rs.getLong("TimeStamp"));
                if (dueypack.getSentTime() > System.currentTimeMillis()) {
                    packages.add(dueypack);
                } else {
                    DueyHandler.removeItemFromDB(packageid, charid);
                }
                rs.close();
                ps.close();
                return dueypack;
            }
            rs.close();
            ps.close();
            return null;
        }
        catch (SQLException se) {
            return null;
        }
    }

    public static final void reciveMsg(MapleClient c, int recipientId) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE dueypackages SET Checked = 0 where RecieverId = ?");
            ps.setInt(1, recipientId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static final void removeItemFromDB(int packageid, int charid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM dueypackages WHERE PackageId = ? and RecieverId = ?");
            ps.setInt(1, packageid);
            ps.setInt(2, charid);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static final MapleDueyActions getItemByPID(int packageid) {
        try {
            Iterator<Pair<IItem, MapleInventoryType>> iterator;
            Map<Integer, Pair<IItem, MapleInventoryType>> iter = ItemLoader.DUEY.loadItems(false, packageid);
            if (iter != null && iter.size() > 0 && (iterator = iter.values().iterator()).hasNext()) {
                Pair<IItem, MapleInventoryType> i = iterator.next();
                return new MapleDueyActions(packageid, i.getLeft());
            }
        }
        catch (Exception se) {
            se.printStackTrace();
        }
        return new MapleDueyActions(packageid);
    }
}

