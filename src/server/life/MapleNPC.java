/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import client.MapleClient;
import server.MapleShopFactory;
import server.life.AbstractLoadedMapleLife;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

public class MapleNPC
extends AbstractLoadedMapleLife {
    private String name = "MISSINGNO";
    private boolean custom = false;

    public MapleNPC(int id, String name) {
        super(id);
        this.name = name;
    }

    public final boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(this.getId()) != null;
    }

    public final void sendShop(MapleClient c) {
        MapleShopFactory.getInstance().getShopForNPC(this.getId()).sendShop(c);
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (this.getId() >= 9901000) {
            return;
        }
        client.getSession().write((Object)MaplePacketCreator.spawnNPC(this, true));
        client.getSession().write((Object)MaplePacketCreator.spawnNPCRequestController(this, true));
    }

    @Override
    public final void sendDestroyData(MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeNPC(this.getObjectId()));
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.NPC;
    }

    public final String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public final boolean isCustom() {
        return this.custom;
    }

    public final void setCustom(boolean custom) {
        this.custom = custom;
    }
}

