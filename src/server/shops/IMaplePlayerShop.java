/*
 * Decompiled with CFR 0.152.
 */
package server.shops;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import java.util.List;
import server.shops.AbstractPlayerStore;
import server.shops.MaplePlayerShopItem;
import tools.Pair;

public interface IMaplePlayerShop {
    public static final byte HIRED_MERCHANT = 1;
    public static final byte PLAYER_SHOP = 2;
    public static final byte OMOK = 3;
    public static final byte MATCH_CARD = 4;

    public String getOwnerName();

    public String getDescription();

    public List<Pair<Byte, MapleCharacter>> getVisitors();

    public List<MaplePlayerShopItem> getItems();

    public boolean isOpen();

    public boolean removeItem(int var1);

    public boolean isOwner(MapleCharacter var1);

    public byte getShopType();

    public byte getVisitorSlot(MapleCharacter var1);

    public byte getFreeSlot();

    public int getItemId();

    public int getMeso();

    public int getOwnerId();

    public int getOwnerAccId();

    public void setOpen(boolean var1);

    public void setMeso(int var1);

    public void addItem(MaplePlayerShopItem var1);

    public void removeFromSlot(int var1);

    public void broadcastToVisitors(MaplePacket var1);

    public void addVisitor(MapleCharacter var1);

    public void removeVisitor(MapleCharacter var1);

    public void removeAllVisitors(int var1, int var2);

    public void buy(MapleClient var1, int var2, short var3);

    public void closeShop(boolean var1, boolean var2);

    public String getPassword();

    public int getMaxSize();

    public int getSize();

    public int getGameType();

    public void update();

    public void setAvailable(boolean var1);

    public boolean isAvailable();

    public List<AbstractPlayerStore.BoughtItem> getBoughtItems();
}

