/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.inventory.Equip;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;

public enum ItemLoader {
    INVENTORY("inventoryitems", "inventoryequipment", 0, "characterid"),
    STORAGE("inventoryitems", "inventoryequipment", 1, "accountid"),
    CASHSHOP_EXPLORER("csitems", "csequipment", 2, "accountid"),
    CASHSHOP_CYGNUS("csitems", "csequipment", 3, "accountid"),
    CASHSHOP_ARAN("csitems", "csequipment", 4, "accountid"),
    HIRED_MERCHANT("hiredmerchitems", "hiredmerchequipment", 5, "packageid", "accountid", "characterid"),
    DUEY("dueyitems", "dueyequipment", 6, "packageid"),
    CASHSHOP_EVAN("csitems", "csequipment", 7, "accountid"),
    MTS("mtsitems", "mtsequipment", 8, "packageid"),
    MTS_TRANSFER("mtstransfer", "mtstransferequipment", 9, "characterid"),
    CASHSHOP_DB("csitems", "csequipment", 10, "accountid"),
    CASHSHOP_RESIST("csitems", "csequipment", 11, "accountid");

    private int value;
    private String table;
    private String table_equip;
    private List<String> arg;

    private ItemLoader(String table, String table_equip, int value, String ... arg) {
        this.table = table;
        this.table_equip = table_equip;
        this.value = value;
        this.arg = Arrays.asList(arg);
    }

    public int getValue() {
        return this.value;
    }

    public Map<Integer, Pair<IItem, MapleInventoryType>> loadItems(boolean login, Integer ... id) throws SQLException {
        List<Integer> lulz = Arrays.asList(id);
        LinkedHashMap<Integer, Pair<IItem, MapleInventoryType>> items = new LinkedHashMap<Integer, Pair<IItem, MapleInventoryType>>();
        if (lulz.size() != this.arg.size()) {
            return items;
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM `");
        query.append(this.table);
        query.append("` LEFT JOIN `");
        query.append(this.table_equip);
        query.append("` USING(`inventoryitemid`) WHERE `type` = ?");
        for (String g : this.arg) {
            query.append(" AND `");
            query.append(g);
            query.append("` = ?");
        }
        if (login) {
            query.append(" AND `inventorytype` = ");
            query.append(MapleInventoryType.EQUIPPED.getType());
        }
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query.toString());
        ps.setInt(1, this.value);
        for (int i = 0; i < lulz.size(); ++i) {
            ps.setInt(i + 2, lulz.get(i));
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
            if (mit.equals((Object)MapleInventoryType.EQUIP) || mit.equals((Object)MapleInventoryType.EQUIPPED)) {
                Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getByte("flag"));
                if (!login) {
                    MapleRing ring;
                    equip.setQuantity((short)1);
                    equip.setInventoryId(rs.getLong("inventoryitemid"));
                    equip.setOwner(rs.getString("owner"));
                    equip.setExpiration(rs.getLong("expiredate"));
                    equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                    equip.setLevel(rs.getByte("level"));
                    equip.setStr(rs.getShort("str"));
                    equip.setDex(rs.getShort("dex"));
                    equip.setInt(rs.getShort("int"));
                    equip.setLuk(rs.getShort("luk"));
                    equip.setHp(rs.getShort("hp"));
                    equip.setMp(rs.getShort("mp"));
                    equip.setWatk(rs.getShort("watk"));
                    equip.setMatk(rs.getShort("matk"));
                    equip.setWdef(rs.getShort("wdef"));
                    equip.setMdef(rs.getShort("mdef"));
                    equip.setAcc(rs.getShort("acc"));
                    equip.setAvoid(rs.getShort("avoid"));
                    equip.setHands(rs.getShort("hands"));
                    equip.setSpeed(rs.getShort("speed"));
                    equip.setJump(rs.getShort("jump"));
                    equip.setViciousHammer(rs.getByte("ViciousHammer"));
                    equip.setItemEXP(rs.getInt("itemEXP"));
                    equip.setGMLog(rs.getString("GM_Log"));
                    equip.setDurability(rs.getInt("durability"));
                    equip.setEnhance(rs.getByte("enhance"));
                    equip.setPotential1(rs.getShort("potential1"));
                    equip.setPotential2(rs.getShort("potential2"));
                    equip.setPotential3(rs.getShort("potential3"));
                    equip.setHpR(rs.getShort("hpR"));
                    equip.setMpR(rs.getShort("mpR"));
                    equip.setGiftFrom(rs.getString("sender"));
                    if (equip.getUniqueId() > -1 && GameConstants.isEffectRing(rs.getInt("itemid")) && (ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals((Object)MapleInventoryType.EQUIPPED))) != null) {
                        equip.setRing(ring);
                    }
                }
                items.put(rs.getInt("inventoryitemid"), new Pair<IItem, MapleInventoryType>(equip.copy(), mit));
                continue;
            }
            Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getByte("flag"));
            item.setUniqueId(rs.getInt("uniqueid"));
            item.setOwner(rs.getString("owner"));
            item.setInventoryId(rs.getLong("inventoryitemid"));
            item.setExpiration(rs.getLong("expiredate"));
            item.setGMLog(rs.getString("GM_Log"));
            item.setGiftFrom(rs.getString("sender"));
            if (GameConstants.isPet(item.getItemId())) {
                if (item.getUniqueId() > -1) {
                    MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                    if (pet != null) {
                        item.setPet(pet);
                    }
                } else {
                    int new_unique = MapleInventoryIdentifier.getInstance();
                    item.setUniqueId(new_unique);
                    item.setPet(MaplePet.createPet(item.getItemId(), new_unique));
                }
            }
            items.put(rs.getInt("inventoryitemid"), new Pair<IItem, MapleInventoryType>(item.copy(), mit));
        }
        rs.close();
        ps.close();
        return items;
    }

    public void saveItems(List<Pair<IItem, MapleInventoryType>> items, Integer ... id) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        this.saveItems(items, con, id);
    }

    public void saveItems(List<Pair<IItem, MapleInventoryType>> items, Connection con, Integer ... id) throws SQLException {
        List<Integer> lulz = Arrays.asList(id);
        if (lulz.size() != this.arg.size()) {
            return;
        }
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM `");
        query.append(this.table);
        query.append("` WHERE `type` = ? AND (`");
        query.append(this.arg.get(0));
        query.append("` = ?");
        if (this.arg.size() > 1) {
            for (int i = 1; i < this.arg.size(); ++i) {
                query.append(" OR `");
                query.append(this.arg.get(i));
                query.append("` = ?");
            }
        }
        query.append(")");
        PreparedStatement ps = con.prepareStatement(query.toString());
        ps.setInt(1, this.value);
        for (int i = 0; i < lulz.size(); ++i) {
            ps.setInt(i + 2, lulz.get(i));
        }
        ps.executeUpdate();
        ps.close();
        if (items == null) {
            return;
        }
        StringBuilder query_2 = new StringBuilder("INSERT INTO `");
        query_2.append(this.table);
        query_2.append("` (");
        for (String g : this.arg) {
            query_2.append(g);
            query_2.append(", ");
        }
        query_2.append("itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        for (String g : this.arg) {
            query_2.append(", ?");
        }
        query_2.append(")");
        ps = con.prepareStatement(query_2.toString(), 1);
        PreparedStatement pse = con.prepareStatement("INSERT INTO " + this.table_equip + " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (Pair<IItem, MapleInventoryType> pair : items) {
            IItem item = pair.getLeft();
            MapleInventoryType mit = pair.getRight();
            int i = 1;
            for (int x = 0; x < lulz.size(); ++x) {
                ps.setInt(i, lulz.get(x));
                ++i;
            }
            ps.setInt(i, item.getItemId());
            ps.setInt(i + 1, mit.getType());
            ps.setInt(i + 2, item.getPosition());
            ps.setInt(i + 3, item.getQuantity());
            ps.setString(i + 4, item.getOwner());
            ps.setString(i + 5, item.getGMLog());
            ps.setInt(i + 6, item.getUniqueId());
            ps.setLong(i + 7, item.getExpiration());
            ps.setByte(i + 8, item.getFlag());
            ps.setByte(i + 9, (byte)this.value);
            ps.setString(i + 10, item.getGiftFrom());
            ps.executeUpdate();
            if (!mit.equals((Object)MapleInventoryType.EQUIP) && !mit.equals((Object)MapleInventoryType.EQUIPPED)) continue;
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new RuntimeException("Inserting item failed.");
            }
            long iid = rs.getLong(1);
            rs.close();
            item.setInventoryId(iid);
            IEquip equip = (IEquip)item;
            pse.setLong(1, iid);
            pse.setInt(2, equip.getUpgradeSlots());
            pse.setInt(3, equip.getLevel());
            pse.setInt(4, equip.getStr());
            pse.setInt(5, equip.getDex());
            pse.setInt(6, equip.getInt());
            pse.setInt(7, equip.getLuk());
            pse.setInt(8, equip.getHp());
            pse.setInt(9, equip.getMp());
            pse.setInt(10, equip.getWatk());
            pse.setInt(11, equip.getMatk());
            pse.setInt(12, equip.getWdef());
            pse.setInt(13, equip.getMdef());
            pse.setInt(14, equip.getAcc());
            pse.setInt(15, equip.getAvoid());
            pse.setInt(16, equip.getHands());
            pse.setInt(17, equip.getSpeed());
            pse.setInt(18, equip.getJump());
            pse.setInt(19, equip.getViciousHammer());
            pse.setInt(20, equip.getItemEXP());
            pse.setInt(21, equip.getDurability());
            pse.setByte(22, equip.getEnhance());
            pse.setInt(23, equip.getPotential1());
            pse.setInt(24, equip.getPotential2());
            pse.setInt(25, equip.getPotential3());
            pse.setInt(26, equip.getHpR());
            pse.setInt(27, equip.getMpR());
            pse.executeUpdate();
        }
        pse.close();
        ps.close();
    }
}

