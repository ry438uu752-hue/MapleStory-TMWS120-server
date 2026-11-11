/*
 * Decompiled with CFR 0.152.
 */
package server;

import java.util.HashMap;
import java.util.Map;
import server.MapleShop;

public class MapleShopFactory {
    private Map<Integer, MapleShop> shops = new HashMap<Integer, MapleShop>();
    private Map<Integer, MapleShop> npcShops = new HashMap<Integer, MapleShop>();
    private static MapleShopFactory instance = new MapleShopFactory();

    public static MapleShopFactory getInstance() {
        return instance;
    }

    public void clear() {
        this.shops.clear();
        this.npcShops.clear();
    }

    public MapleShop getShop(int shopId) {
        if (this.shops.containsKey(shopId)) {
            return this.shops.get(shopId);
        }
        return this.loadShop(shopId, true);
    }

    public MapleShop getShopForNPC(int npcId) {
        if (this.npcShops.containsKey(npcId)) {
            return this.npcShops.get(npcId);
        }
        return this.loadShop(npcId, false);
    }

    private MapleShop loadShop(int id, boolean isShopId) {
        MapleShop ret = MapleShop.createFromDB(id, isShopId);
        if (ret != null) {
            this.shops.put(ret.getId(), ret);
            this.npcShops.put(ret.getNpcId(), ret);
        } else if (isShopId) {
            this.shops.put(id, null);
        } else {
            this.npcShops.put(id, null);
        }
        return ret;
    }
}

