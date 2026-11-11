/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.inventory.IItem;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.MTSStorage;
import tools.Pair;

public class MTSCart
implements Serializable {
    private static final long serialVersionUID = 231541893513373578L;
    private int characterId;
    private int tab = 1;
    private int type = 0;
    private int page = 0;
    private List<IItem> transfer = new ArrayList<IItem>();
    private List<Integer> cart = new ArrayList<Integer>();
    private List<Integer> notYetSold = new ArrayList<Integer>(10);
    private int owedNX = 0;

    public MTSCart(int characterId) throws SQLException {
        this.characterId = characterId;
        for (Pair<IItem, MapleInventoryType> item : ItemLoader.MTS_TRANSFER.loadItems(false, characterId).values()) {
            this.transfer.add(item.getLeft());
        }
        this.loadCart();
        this.loadNotYetSold();
    }

    public List<IItem> getInventory() {
        return this.transfer;
    }

    public void addToInventory(IItem item) {
        this.transfer.add(item);
    }

    public void removeFromInventory(IItem item) {
        this.transfer.remove(item);
    }

    public List<Integer> getCart() {
        return this.cart;
    }

    public boolean addToCart(int car) {
        if (!this.cart.contains(car)) {
            this.cart.add(car);
            return true;
        }
        return false;
    }

    public void removeFromCart(int car) {
        for (int i = 0; i < this.cart.size(); ++i) {
            if (this.cart.get(i) != car) continue;
            this.cart.remove(i);
        }
    }

    public List<Integer> getNotYetSold() {
        return this.notYetSold;
    }

    public void addToNotYetSold(int car) {
        this.notYetSold.add(car);
    }

    public void removeFromNotYetSold(int car) {
        for (int i = 0; i < this.notYetSold.size(); ++i) {
            if (this.notYetSold.get(i) != car) continue;
            this.notYetSold.remove(i);
        }
    }

    public final int getSetOwedNX() {
        int on = this.owedNX;
        this.owedNX = 0;
        return on;
    }

    public void increaseOwedNX(int newNX) {
        this.owedNX += newNX;
    }

    public void save() throws SQLException {
        ArrayList<Pair<IItem, MapleInventoryType>> itemsWithType = new ArrayList<Pair<IItem, MapleInventoryType>>();
        for (IItem item : this.getInventory()) {
            itemsWithType.add(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        ItemLoader.MTS_TRANSFER.saveItems(itemsWithType, this.characterId);
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM mts_cart WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        ps.execute();
        ps.close();
        ps = con.prepareStatement("INSERT INTO mts_cart VALUES(DEFAULT, ?, ?)");
        ps.setInt(1, this.characterId);
        for (int i : this.cart) {
            ps.setInt(2, i);
            ps.executeUpdate();
        }
        if (this.owedNX > 0) {
            ps.setInt(2, -this.owedNX);
            ps.executeUpdate();
        }
        ps.close();
    }

    public void loadCart() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM mts_cart WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int iId = rs.getInt("itemid");
            if (iId < 0) {
                this.owedNX -= iId;
                continue;
            }
            if (!MTSStorage.getInstance().check(iId)) continue;
            this.cart.add(iId);
        }
        rs.close();
        ps.close();
    }

    public void loadNotYetSold() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM mts_items WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int pId = rs.getInt("id");
            if (!MTSStorage.getInstance().check(pId)) continue;
            this.notYetSold.add(pId);
        }
        rs.close();
        ps.close();
    }

    public void changeInfo(int tab, int type, int page) {
        this.tab = tab;
        this.type = type;
        this.page = page;
    }

    public int getTab() {
        return this.tab;
    }

    public int getType() {
        return this.type;
    }

    public int getPage() {
        return this.page;
    }
}

