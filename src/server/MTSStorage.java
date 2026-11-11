/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.inventory.IItem;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.MTSCart;
import tools.Pair;
import tools.packet.MTSCSPacket;

public class MTSStorage {
    private static final long serialVersionUID = 231541893513228L;
    private long lastUpdate = System.currentTimeMillis();
    private final Map<Integer, MTSCart> idToCart;
    private final AtomicInteger packageId;
    private final Map<Integer, MTSItemInfo> buyNow;
    private static MTSStorage instance;
    private boolean end = false;
    private ReentrantReadWriteLock mutex;
    private ReentrantReadWriteLock cart_mutex;

    public MTSStorage() {
        System.out.println("Loading MTSStorage :::");
        this.idToCart = new LinkedHashMap<Integer, MTSCart>();
        this.buyNow = new LinkedHashMap<Integer, MTSItemInfo>();
        this.packageId = new AtomicInteger(1);
        this.mutex = new ReentrantReadWriteLock();
        this.cart_mutex = new ReentrantReadWriteLock();
    }

    public static final MTSStorage getInstance() {
        return instance;
    }

    public static final void load() {
        if (instance == null) {
            instance = new MTSStorage();
            instance.loadBuyNow();
        }
    }

    public final boolean check(int packageid) {
        return this.getSingleItem(packageid) != null;
    }

    public final boolean checkCart(int packageid, int charID) {
        MTSItemInfo item = this.getSingleItem(packageid);
        return item != null && item.getCharacterId() != charID;
    }

    public final MTSItemInfo getSingleItem(int packageid) {
        this.mutex.readLock().lock();
        try {
            MTSItemInfo mTSItemInfo = this.buyNow.get(packageid);
            return mTSItemInfo;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void addToBuyNow(MTSCart cart, IItem item, int price, int cid, String seller, long expiration) {
        int id;
        this.mutex.writeLock().lock();
        try {
            id = this.packageId.incrementAndGet();
            this.buyNow.put(id, new MTSItemInfo(price, item, seller, id, cid, expiration));
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        cart.addToNotYetSold(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean removeFromBuyNow(int id, int cidBought, boolean check) {
        IItem item = null;
        this.mutex.writeLock().lock();
        try {
            if (this.buyNow.containsKey(id)) {
                MTSItemInfo r = this.buyNow.get(id);
                if (!check || r.getCharacterId() == cidBought) {
                    item = r.getItem();
                    this.buyNow.remove(id);
                }
            }
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        if (item != null) {
            this.cart_mutex.readLock().lock();
            try {
                for (Map.Entry<Integer, MTSCart> c : this.idToCart.entrySet()) {
                    c.getValue().removeFromCart(id);
                    c.getValue().removeFromNotYetSold(id);
                    if (c.getKey() != cidBought) continue;
                    c.getValue().addToInventory(item);
                }
            }
            finally {
                this.cart_mutex.readLock().unlock();
            }
        }
        return item != null;
    }

    private final void loadBuyNow() {
        int lastPackage = 0;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mts_items WHERE tab = 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<Integer, Pair<IItem, MapleInventoryType>> items;
                lastPackage = rs.getInt("id");
                int cId = rs.getInt("characterid");
                if (!this.idToCart.containsKey(cId)) {
                    this.idToCart.put(cId, new MTSCart(cId));
                }
                if ((items = ItemLoader.MTS.loadItems(false, lastPackage)) == null || items.size() <= 0) continue;
                for (Pair<IItem, MapleInventoryType> i : items.values()) {
                    this.buyNow.put(lastPackage, new MTSItemInfo(rs.getInt("price"), i.getLeft(), rs.getString("seller"), lastPackage, cId, rs.getLong("expiration")));
                }
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.packageId.set(lastPackage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void saveBuyNow(boolean isShutDown) {
        if (this.end) {
            return;
        }
        this.end = isShutDown;
        if (isShutDown) {
            System.out.println("Saving MTS...");
        }
        HashMap expire = new HashMap();
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        long now = System.currentTimeMillis();
        HashMap<Integer, List<Pair<IItem, MapleInventoryType>>> items = new HashMap<Integer, List<Pair<IItem, MapleInventoryType>>>();
        Connection con = DatabaseConnection.getConnection();
        this.mutex.writeLock().lock();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM mts_items WHERE tab = 1");
            ps.execute();
            ps.close();
            ps = con.prepareStatement("INSERT INTO mts_items VALUES (?, ?, ?, ?, ?, ?)");
            for (MTSItemInfo m : this.buyNow.values()) {
                if (now > m.getEndingDate()) {
                    if (!expire.containsKey(m.getCharacterId())) {
                        expire.put(m.getCharacterId(), new ArrayList());
                    }
                    ((ArrayList)expire.get(m.getCharacterId())).add(m.getItem());
                    toRemove.add(m.getId());
                    items.put(m.getId(), null);
                    continue;
                }
                ps.setInt(1, m.getId());
                ps.setByte(2, (byte)1);
                ps.setInt(3, m.getPrice());
                ps.setInt(4, m.getCharacterId());
                ps.setString(5, m.getSeller());
                ps.setLong(6, m.getEndingDate());
                ps.executeUpdate();
                if (!items.containsKey(m.getId())) {
                    items.put(m.getId(), new ArrayList<Pair<IItem, MapleInventoryType>>());
                }
                ((ArrayList)items.get(m.getId())).add(new Pair<IItem, MapleInventoryType>(m.getItem(), GameConstants.getInventoryType(m.getItem().getItemId())));
            }
            Iterator iterator = toRemove.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                this.buyNow.remove(i);
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        if (isShutDown) {
            System.out.println("Saving MTS items...");
        }
        try {
            for (Map.Entry entry : items.entrySet()) {
                ItemLoader.MTS.saveItems((List<Pair<IItem, MapleInventoryType>>)((List)entry.getValue()), (Integer)entry.getKey());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (isShutDown) {
            System.out.println("Saving MTS carts...");
        }
        this.cart_mutex.writeLock().lock();
        try {
            for (Map.Entry<Integer, MTSCart> entry : this.idToCart.entrySet()) {
                Iterator iterator = toRemove.iterator();
                while (iterator.hasNext()) {
                    int i = (Integer)iterator.next();
                    ((MTSCart)entry.getValue()).removeFromCart(i);
                    ((MTSCart)entry.getValue()).removeFromNotYetSold(i);
                }
                if (expire.containsKey(entry.getKey())) {
                    for (IItem item : (ArrayList<IItem>)expire.get(entry.getKey())) {
                        ((MTSCart)entry.getValue()).addToInventory(item);
                    }
                }
                ((MTSCart)entry.getValue()).save();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            this.cart_mutex.writeLock().unlock();
        }
        this.lastUpdate = System.currentTimeMillis();
    }

    public final void checkExpirations() {
        if (System.currentTimeMillis() - this.lastUpdate > 3600000L) {
            this.saveBuyNow(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MTSCart getCart(int characterId) {
        MTSCart ret;
        this.cart_mutex.readLock().lock();
        try {
            ret = this.idToCart.get(characterId);
        }
        finally {
            this.cart_mutex.readLock().unlock();
        }
        if (ret == null) {
            this.cart_mutex.writeLock().lock();
            try {
                ret = new MTSCart(characterId);
                this.idToCart.put(characterId, ret);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                this.cart_mutex.writeLock().unlock();
            }
        }
        return ret;
    }

    public final MaplePacket getCurrentMTS(MTSCart cart) {
        this.mutex.readLock().lock();
        try {
            if (cart.getTab() == 1) {
                MaplePacket maplePacket = MTSCSPacket.sendMTS(this.getBuyNow(cart.getType(), cart.getPage()), cart.getTab(), cart.getType(), cart.getPage(), this.buyNow.size() / 16 + (this.buyNow.size() % 16 > 0 ? 1 : 0));
                return maplePacket;
            }
            if (cart.getTab() == 4) {
                MaplePacket maplePacket = MTSCSPacket.sendMTS(this.getCartItems(cart), cart.getTab(), cart.getType(), cart.getPage(), 0);
                return maplePacket;
            }
            MaplePacket maplePacket = MTSCSPacket.sendMTS(new ArrayList<MTSItemInfo>(), cart.getTab(), cart.getType(), cart.getPage(), 0);
            return maplePacket;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MaplePacket getCurrentNotYetSold(MTSCart cart) {
        this.mutex.readLock().lock();
        try {
            ArrayList<MTSItemInfo> nys = new ArrayList<MTSItemInfo>();
            ArrayList<Integer> nyss = new ArrayList<Integer>(cart.getNotYetSold());
            Iterator<Integer> it = nyss.iterator();
            while (it.hasNext()) {
                int i = it.next();
                MTSItemInfo r = this.buyNow.get(i);
                if (r == null) {
                    cart.removeFromNotYetSold(i);
                    continue;
                }
                nys.add(r);
            }
            return MTSCSPacket.getNotYetSoldInv(nys);
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    public final MaplePacket getCurrentTransfer(MTSCart cart, boolean changed) {
        return MTSCSPacket.getTransferInventory(cart.getInventory(), changed);
    }

    private final List<MTSItemInfo> getBuyNow(int type, int page) {
        int size = this.buyNow.size() / 16 + (this.buyNow.size() % 16 > 0 ? 1 : 0);
        ArrayList<MTSItemInfo> ret = new ArrayList<MTSItemInfo>();
        ArrayList<MTSItemInfo> rett = new ArrayList<MTSItemInfo>(this.buyNow.values());
        if (page > size) {
            page = 0;
        }
        for (int i = page * 16; i < page * 16 + 16 && this.buyNow.size() >= i + 1; ++i) {
            MTSItemInfo r = (MTSItemInfo)rett.get(i);
            if (r == null || type != 0 && GameConstants.getInventoryType(r.getItem().getItemId()).getType() != type) continue;
            ret.add(r);
        }
        return ret;
    }

    private final List<MTSItemInfo> getCartItems(MTSCart cart) {
        ArrayList<MTSItemInfo> ret = new ArrayList<MTSItemInfo>();
        ArrayList<Integer> cartt = new ArrayList<Integer>(cart.getCart());
        Iterator iterator = cartt.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            MTSItemInfo r = this.buyNow.get(i);
            if (r == null) {
                cart.removeFromCart(i);
                continue;
            }
            if (cart.getType() != 0 && GameConstants.getInventoryType(r.getItem().getItemId()).getType() != cart.getType()) continue;
            ret.add(r);
        }
        return ret;
    }

    public static class MTSItemInfo {
        private int price;
        private IItem item;
        private String seller;
        private int id;
        private int cid;
        private long date;

        public MTSItemInfo(int price, IItem item, String seller, int id, int cid, long date) {
            this.item = item;
            this.price = price;
            this.seller = seller;
            this.id = id;
            this.cid = cid;
            this.date = date;
        }

        public IItem getItem() {
            return this.item;
        }

        public int getPrice() {
            return this.price;
        }

        public int getRealPrice() {
            return this.price + this.getTaxes();
        }

        public int getTaxes() {
            return 100 + this.price * 10 / 100;
        }

        public int getId() {
            return this.id;
        }

        public int getCharacterId() {
            return this.cid;
        }

        public long getEndingDate() {
            return this.date;
        }

        public String getSeller() {
            return this.seller;
        }
    }
}

