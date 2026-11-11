/*
 * Decompiled with CFR 0.152.
 */
package server.shops;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import constants.GameConstants;
import handling.channel.ChannelServer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Timer;
import server.maps.MapleMapObjectType;
import server.shops.AbstractPlayerStore;
import server.shops.MaplePlayerShopItem;
import tools.MaplePacketCreator;
import tools.packet.PlayerShopPacket;

public class HiredMerchant
extends AbstractPlayerStore {
    public ScheduledFuture<?> schedule;
    private List<String> blacklist;
    private Map<String, Byte> MsgList;
    private int storeid;
    private long start;
    private ReentrantReadWriteLock merchantLock = new ReentrantReadWriteLock();

    public HiredMerchant(MapleCharacter owner, int itemId, String desc) {
        super(owner, itemId, desc, "", 3);
        this.start = System.currentTimeMillis();
        this.blacklist = new LinkedList<String>();
        this.MsgList = new HashMap<String, Byte>();
        this.schedule = Timer.EtcTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                HiredMerchant.this.closeShop(true, true);
            }
        }, 86400000L);
    }

    @Override
    public byte getShopType() {
        return 1;
    }

    public final void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public List<MaplePlayerShopItem> searchItem(int itemSearch) {
        LinkedList<MaplePlayerShopItem> itemz = new LinkedList<MaplePlayerShopItem>();
        for (MaplePlayerShopItem item : this.items) {
            if (item.item.getItemId() != itemSearch || item.bundles <= 0) continue;
            itemz.add(item);
        }
        return itemz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void buy(MapleClient c, int item, short quantity) {
        MaplePlayerShopItem pItem;
        MaplePlayerShopItem maplePlayerShopItem = pItem = (MaplePlayerShopItem)this.items.get(item);
        synchronized (maplePlayerShopItem) {
            IItem shopItem = pItem.item;
            IItem newItem = shopItem.copy();
            short perbundle = newItem.getQuantity();
            int theQuantity = pItem.price * quantity;
            newItem.setQuantity((short)(quantity * perbundle));
            byte flag = newItem.getFlag();
            if (ItemFlag.KARMA_EQ.check(flag)) {
                newItem.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                newItem.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
            }
            if (MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner())) {
                int gainmeso = this.getMeso() + theQuantity - GameConstants.EntrustedStoreTax(theQuantity);
                if (gainmeso > 0) {
                    if (MapleInventoryManipulator.addFromDrop(c, newItem, false)) {
                        this.setMeso(gainmeso);
                        pItem.bundles = (short)(pItem.bundles - quantity);
                        this.bought.add(new AbstractPlayerStore.BoughtItem(newItem.getItemId(), quantity, theQuantity, c.getPlayer().getName()));
                        c.getPlayer().gainMeso(-theQuantity, false);
                        MapleCharacter chr = this.getMCOwnerWorld();
                        if (chr != null) {
                            chr.dropMessage(5, "\u7269\u54c1 " + MapleItemInformationProvider.getInstance().getName(newItem.getItemId()) + " (" + perbundle + ") x " + quantity + " \u5df2\u5f9e\u7cbe\u9748\u5546\u5e97\u8ce3\u51fa. \u9084\u5269\u4e0b " + pItem.bundles + "\u500b");
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "\u60a8\u7684\u80cc\u5305\u6eff\u4e86.");
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                    }
                } else {
                    c.getPlayer().dropMessage(1, "\u62cd\u8ce3\u5bb6\u6709\u592a\u591a\u9322\u4e86.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                }
            } else {
                c.getPlayer().dropMessage(1, "\u60a8\u7684\u80cc\u5305\u6eff\u4e86.");
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
    }

    @Override
    public void closeShop(boolean saveItems, boolean remove) {
        this.merchantLock.writeLock().lock();
        try {
            if (this.schedule != null) {
                this.schedule.cancel(false);
            }
            if (saveItems) {
                this.saveItems();
                this.items.clear();
            }
            if (remove) {
                ChannelServer.getInstance(this.channel).removeMerchant(this);
                this.getMap().broadcastMessage(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
            }
            this.getMap().removeMapObject(this);
            this.schedule = null;
        }
        finally {
            this.merchantLock.writeLock().unlock();
        }
    }

    public int getTimeLeft() {
        return (int)((System.currentTimeMillis() - this.start) / 1000L);
    }

    public final int getStoreId() {
        return this.storeid;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.HIRED_MERCHANT;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write((Object)PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write((Object)PlayerShopPacket.spawnHiredMerchant(this));
        }
    }

    public final boolean isInBlackList(String bl) {
        return this.blacklist.contains(bl);
    }

    public final void addBlackList(String bl) {
        this.blacklist.add(bl);
    }

    public final void removeBlackList(String bl) {
        this.blacklist.remove(bl);
    }

    public final void sendBlackList(MapleClient c) {
        c.getSession().write((Object)PlayerShopPacket.MerchantBlackListView(this.blacklist));
    }

    public final void sendVisitor(MapleClient c) {
        c.getSession().write((Object)PlayerShopPacket.MerchantVisitorView(this.visitors));
    }

    public final void addMsg(String msg, byte slot) {
        this.MsgList.put(msg, slot);
    }

    public final void SendMsg(MapleClient c) {
        for (Map.Entry<String, Byte> s : this.MsgList.entrySet()) {
            c.getSession().write((Object)PlayerShopPacket.shopChat(s.getKey(), s.getValue().byteValue()));
        }
    }
}

