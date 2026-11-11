/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.Pair;
import tools.packet.MTSCSPacket;

public class CashShop
implements Serializable {
    private static final long serialVersionUID = 231541893513373579L;
    private int accountId;
    private int characterId;
    private ItemLoader factory;
    private List<IItem> inventory = new ArrayList<IItem>();
    private List<Integer> uniqueids = new ArrayList<Integer>();

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;
        this.factory = jobType / 1000 == 1 ? ItemLoader.CASHSHOP_CYGNUS : ((jobType / 100 == 21 || jobType / 100 == 20) && jobType != 2001 ? ItemLoader.CASHSHOP_ARAN : (jobType == 2001 || jobType / 100 == 22 ? ItemLoader.CASHSHOP_EVAN : (jobType >= 3000 ? ItemLoader.CASHSHOP_RESIST : (jobType / 10 == 43 ? ItemLoader.CASHSHOP_DB : ItemLoader.CASHSHOP_EXPLORER))));
        for (Pair<IItem, MapleInventoryType> item : this.factory.loadItems(false, accountId).values()) {
            this.inventory.add(item.getLeft());
        }
    }

    public int getItemsSize() {
        return this.inventory.size();
    }

    public List<IItem> getInventory() {
        return this.inventory;
    }

    public IItem findByCashId(int cashId) {
        for (IItem item : this.inventory) {
            if (item.getUniqueId() != cashId) continue;
            return item;
        }
        return null;
    }

    public void checkExpire(MapleClient c) {
        ArrayList<IItem> toberemove = new ArrayList<IItem>();
        for (IItem item : this.inventory) {
            if (item == null || GameConstants.isPet(item.getItemId()) || item.getExpiration() <= 0L || item.getExpiration() >= System.currentTimeMillis()) continue;
            toberemove.add(item);
        }
        if (toberemove.size() > 0) {
            for (IItem item : toberemove) {
                this.removeFromInventory(item);
                c.getSession().write((Object)MTSCSPacket.cashItemExpired(item.getUniqueId()));
            }
            toberemove.clear();
        }
    }

    public IItem toItem(CashItemInfo cItem) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }

    public IItem toItem(CashItemInfo cItem, String gift) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }

    public IItem toItem(CashItemInfo cItem, int uniqueid) {
        return this.toItem(cItem, uniqueid, "");
    }

    public IItem toItem(CashItemInfo cItem, int uniqueid, String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        if (GameConstants.isPet(cItem.getId())) {
            period = 45L;
        }
        IItem ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            MapleRing ring;
            Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(cItem.getId());
            eq.setUniqueId(uniqueid);
            if (period != 0L) {
                eq.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0 && (ring = MapleRing.loadFromDb(uniqueid)) != null) {
                eq.setRing(ring);
            }
            ret = eq.copy();
        } else {
            MaplePet pet;
            Item item = new Item(cItem.getId(), (short)0, (short)cItem.getCount(), (byte)0, uniqueid);
            if (period != 0L) {
                item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId()) && (pet = MaplePet.createPet(cItem.getId(), uniqueid)) != null) {
                item.setPet(pet);
            }
            ret = item.copy();
        }
        return ret;
    }

    public void addToInventory(IItem item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(IItem item) {
        this.inventory.remove(item);
    }

    public void gift(int recipient, String from, String message, int sn) {
        this.gift(recipient, from, message, sn, 0);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, uniqueid);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public List<Pair<IItem, String>> loadGifts() {
        ArrayList<Pair<IItem, String>> gifts = new ArrayList<Pair<IItem, String>>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"));
                IItem item = this.toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
                gifts.add(new Pair<IItem, String>(item, rs.getString("message")));
                this.uniqueids.add(item.getUniqueId());
                List<CashItemInfo> packages = CashItemFactory.getInstance().getPackageItems(cItem.getId());
                if (packages != null && packages.size() > 0) {
                    for (CashItemInfo packageItem : packages) {
                        this.addToInventory(this.toItem(packageItem, rs.getString("from")));
                    }
                    continue;
                }
                this.addToInventory(item);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ps.executeUpdate();
            ps.close();
            this.save();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return gifts;
    }

    public boolean canSendNote(int uniqueid) {
        return this.uniqueids.contains(uniqueid);
    }

    public void sendedNote(int uniqueid) {
        for (int i = 0; i < this.uniqueids.size(); ++i) {
            if (this.uniqueids.get(i) != uniqueid) continue;
            this.uniqueids.remove(i);
        }
    }

    public void save() throws SQLException {
        ArrayList<Pair<IItem, MapleInventoryType>> itemsWithType = new ArrayList<Pair<IItem, MapleInventoryType>>();
        for (IItem item : this.inventory) {
            itemsWithType.add(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        this.factory.saveItems(itemsWithType, this.accountId);
    }
}

