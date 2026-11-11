/*
 * Decompiled with CFR 0.152.
 */
package server.shops;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShopItem;
import tools.Pair;
import tools.packet.PlayerShopPacket;

public abstract class AbstractPlayerStore
extends AbstractMapleMapObject
implements IMaplePlayerShop {
    protected boolean open = false;
    protected boolean available = false;
    protected String ownerName;
    protected String des;
    protected String pass;
    protected int ownerId;
    protected int owneraccount;
    protected int itemId;
    protected int channel;
    protected int map;
    protected AtomicInteger meso = new AtomicInteger(0);
    protected WeakReference<MapleCharacter>[] chrs;
    protected List<String> visitors = new LinkedList<String>();
    protected List<BoughtItem> bought = new LinkedList<BoughtItem>();
    protected List<MaplePlayerShopItem> items = new LinkedList<MaplePlayerShopItem>();

    public AbstractPlayerStore(MapleCharacter owner, int itemId, String desc, String pass, int slots) {
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.owneraccount = owner.getAccountID();
        this.itemId = itemId;
        this.des = desc;
        this.pass = pass;
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannel();
        this.chrs = new WeakReference[slots];
        for (int i = 0; i < this.chrs.length; ++i) {
            this.chrs[i] = new WeakReference<MapleCharacter>(null);
        }
    }

    @Override
    public int getMaxSize() {
        return this.chrs.length + 1;
    }

    @Override
    public int getSize() {
        return this.getFreeSlot() == -1 ? this.getMaxSize() : (int)this.getFreeSlot();
    }

    @Override
    public void broadcastToVisitors(MaplePacket packet) {
        this.broadcastToVisitors(packet, true);
    }

    public void broadcastToVisitors(MaplePacket packet, boolean owner) {
        for (WeakReference<MapleCharacter> chr : this.chrs) {
            if (chr == null || chr.get() == null) continue;
            ((MapleCharacter)chr.get()).getClient().getSession().write((Object)packet);
        }
        if (this.getShopType() != 1 && owner && this.getMCOwner() != null) {
            this.getMCOwner().getClient().getSession().write((Object)packet);
        }
    }

    public void broadcastToVisitors(MaplePacket packet, int exception) {
        for (WeakReference<MapleCharacter> chr : this.chrs) {
            if (chr == null || chr.get() == null || this.getVisitorSlot((MapleCharacter)chr.get()) == exception) continue;
            ((MapleCharacter)chr.get()).getClient().getSession().write((Object)packet);
        }
        if (this.getShopType() != 1 && this.getMCOwner() != null && exception != this.ownerId) {
            this.getMCOwner().getClient().getSession().write((Object)packet);
        }
    }

    @Override
    public int getMeso() {
        return this.meso.get();
    }

    @Override
    public void setMeso(int meso) {
        this.meso.set(meso);
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    public boolean saveItems() {
        if (this.getShopType() != 1) {
            return false;
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM hiredmerch WHERE accountid = ? OR characterid = ?");
            ps.setInt(1, this.owneraccount);
            ps.setInt(2, this.ownerId);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("INSERT INTO hiredmerch (characterid, accountid, Mesos, time) VALUES (?, ?, ?, ?)", 1);
            ps.setInt(1, this.ownerId);
            ps.setInt(2, this.owneraccount);
            ps.setInt(3, this.meso.get());
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                rs.close();
                ps.close();
                throw new RuntimeException("Error, adding merchant to DB");
            }
            int packageid = rs.getInt(1);
            rs.close();
            ps.close();
            ArrayList<Pair<IItem, MapleInventoryType>> iters = new ArrayList<Pair<IItem, MapleInventoryType>>();
            for (MaplePlayerShopItem pItems : this.items) {
                if (pItems.item == null || pItems.bundles <= 0 || pItems.item.getQuantity() <= 0 && !GameConstants.isRechargable(pItems.item.getItemId())) continue;
                IItem item = pItems.item.copy();
                item.setQuantity((short)(item.getQuantity() * pItems.bundles));
                iters.add(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
            }
            ItemLoader.HIRED_MERCHANT.saveItems(iters, packageid, this.owneraccount, this.ownerId);
            return true;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public MapleCharacter getVisitor(int num) {
        return (MapleCharacter)this.chrs[num].get();
    }

    @Override
    public void update() {
        if (this.isAvailable()) {
            if (this.getShopType() == 1) {
                this.getMap().broadcastMessage(PlayerShopPacket.updateHiredMerchant((HiredMerchant)this));
            } else if (this.getMCOwner() != null) {
                this.getMap().broadcastMessage(PlayerShopPacket.sendPlayerShopBox(this.getMCOwner()));
            }
        }
    }

    @Override
    public void addVisitor(MapleCharacter visitor) {
        byte i = this.getFreeSlot();
        if (i > 0) {
            if (this.getShopType() >= 3) {
                this.broadcastToVisitors(PlayerShopPacket.getMiniGameNewVisitor(visitor, i, (MapleMiniGame)this));
            } else {
                this.broadcastToVisitors(PlayerShopPacket.shopVisitorAdd(visitor, i));
            }
            this.chrs[i - 1] = new WeakReference<MapleCharacter>(visitor);
            if (!this.isOwner(visitor)) {
                this.visitors.add(visitor.getName());
            }
            if (i == 3) {
                this.update();
            }
        }
    }

    @Override
    public void removeVisitor(MapleCharacter visitor) {
        boolean shouldUpdate;
        byte slot = this.getVisitorSlot(visitor);
        boolean bl = shouldUpdate = this.getFreeSlot() == -1;
        if (slot > 0) {
            this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(slot), slot);
            this.chrs[slot - 1] = new WeakReference<MapleCharacter>(null);
            if (shouldUpdate) {
                this.update();
            }
        }
    }

    @Override
    public byte getVisitorSlot(MapleCharacter visitor) {
        for (int i = 0; i < this.chrs.length; i = (int)((byte)(i + 1))) {
            if (this.chrs[i] == null || this.chrs[i].get() == null || ((MapleCharacter)this.chrs[i].get()).getId() != visitor.getId()) continue;
            return (byte)(i + 1);
        }
        if (visitor.getId() == this.ownerId) {
            return 0;
        }
        return -1;
    }

    @Override
    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < this.chrs.length; ++i) {
            MapleCharacter visitor = this.getVisitor(i);
            if (visitor == null) continue;
            if (type != -1) {
                visitor.getClient().getSession().write((Object)PlayerShopPacket.shopErrorMessage(error, type));
            }
            this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(this.getVisitorSlot(visitor)), this.getVisitorSlot(visitor));
            visitor.setPlayerShop(null);
            this.chrs[i] = new WeakReference<MapleCharacter>(null);
        }
        this.update();
    }

    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    @Override
    public int getOwnerId() {
        return this.ownerId;
    }

    @Override
    public int getOwnerAccId() {
        return this.owneraccount;
    }

    @Override
    public String getDescription() {
        if (this.des == null) {
            return "";
        }
        return this.des;
    }

    @Override
    public List<Pair<Byte, MapleCharacter>> getVisitors() {
        LinkedList<Pair<Byte, MapleCharacter>> chrz = new LinkedList<Pair<Byte, MapleCharacter>>();
        for (int i = 0; i < this.chrs.length; i = (int)((byte)(i + 1))) {
            if (this.chrs[i] == null || this.chrs[i].get() == null) continue;
            chrz.add(new Pair((byte)(i + 1), this.chrs[i].get()));
        }
        return chrz;
    }

    @Override
    public List<MaplePlayerShopItem> getItems() {
        return this.items;
    }

    @Override
    public void addItem(MaplePlayerShopItem item) {
        this.items.add(item);
    }

    @Override
    public boolean removeItem(int item) {
        return false;
    }

    @Override
    public void removeFromSlot(int slot) {
        this.items.remove(slot);
    }

    @Override
    public byte getFreeSlot() {
        for (int i = 0; i < this.chrs.length; i = (int)((byte)(i + 1))) {
            if (this.chrs[i] != null && this.chrs[i].get() != null) continue;
            return (byte)(i + 1);
        }
        return -1;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }

    @Override
    public boolean isOwner(MapleCharacter chr) {
        return chr.getId() == this.ownerId && chr.getName().equals(this.ownerName);
    }

    @Override
    public String getPassword() {
        if (this.pass == null) {
            return "";
        }
        return this.pass;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
    }

    @Override
    public void sendSpawnData(MapleClient client) {
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SHOP;
    }

    public MapleCharacter getMCOwner() {
        return this.getMap().getCharacterById(this.ownerId);
    }

    public MapleCharacter getMCOwnerWorld() {
        int ourChannel = World.Find.findChannel(this.ownerId);
        if (ourChannel <= 0) {
            return null;
        }
        return ChannelServer.getInstance(ourChannel).getPlayerStorage().getCharacterById(this.ownerId);
    }

    public MapleMap getMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.map);
    }

    @Override
    public int getGameType() {
        if (this.getShopType() == 1) {
            return 5;
        }
        if (this.getShopType() == 2) {
            return 4;
        }
        if (this.getShopType() == 3) {
            return 1;
        }
        if (this.getShopType() == 4) {
            return 2;
        }
        return 0;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public void setAvailable(boolean b) {
        this.available = b;
    }

    @Override
    public List<BoughtItem> getBoughtItems() {
        return this.bought;
    }

    public static final class BoughtItem {
        public int id;
        public int quantity;
        public int totalPrice;
        public String buyer;

        public BoughtItem(int id, int quantity, int totalPrice, String buyer) {
            this.id = id;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.buyer = buyer;
        }
    }
}

