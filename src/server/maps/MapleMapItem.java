/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

public class MapleMapItem
extends AbstractMapleMapObject {
    protected IItem item;
    protected MapleMapObject dropper;
    protected int character_ownerid;
    protected int meso = 0;
    protected int questid = -1;
    protected byte type;
    protected boolean pickedUp = false;
    protected boolean playerDrop;
    protected boolean randDrop = false;
    protected long nextExpiry = 0L;
    protected long nextFFA = 0L;
    private ReentrantLock lock = new ReentrantLock();

    public MapleMapItem(IItem item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
        this.setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.type = type;
        this.playerDrop = playerDrop;
    }

    public MapleMapItem(IItem item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop, int questid) {
        this.setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.type = type;
        this.playerDrop = playerDrop;
        this.questid = questid;
    }

    public MapleMapItem(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
        this.setPosition(position);
        this.item = null;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.meso = meso;
        this.type = type;
        this.playerDrop = playerDrop;
    }

    public MapleMapItem(Point position, IItem item) {
        this.setPosition(position);
        this.item = item;
        this.character_ownerid = 0;
        this.type = (byte)2;
        this.playerDrop = false;
        this.randDrop = true;
    }

    public final IItem getItem() {
        return this.item;
    }

    public void setItem(IItem z) {
        this.item = z;
    }

    public final int getQuest() {
        return this.questid;
    }

    public final int getItemId() {
        if (this.getMeso() > 0) {
            return this.meso;
        }
        return this.item.getItemId();
    }

    public final MapleMapObject getDropper() {
        return this.dropper;
    }

    public final int getOwner() {
        return this.character_ownerid;
    }

    public final int getMeso() {
        return this.meso;
    }

    public final boolean isPlayerDrop() {
        return this.playerDrop;
    }

    public final boolean isPickedUp() {
        return this.pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public byte getDropType() {
        return this.type;
    }

    public void setDropType(byte z) {
        this.type = z;
    }

    public final boolean isRandDrop() {
        return this.randDrop;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.ITEM;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (this.questid <= 0 || client.getPlayer().getQuestStatus(this.questid) == 1) {
            client.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(this, null, this.getPosition(), (byte)2));
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeItemFromMap(this.getObjectId(), 1, 0));
    }

    public Lock getLock() {
        return this.lock;
    }

    public void registerExpire(long time) {
        this.nextExpiry = System.currentTimeMillis() + time;
    }

    public void registerFFA(long time) {
        this.nextFFA = System.currentTimeMillis() + time;
    }

    public boolean shouldExpire() {
        return !this.pickedUp && this.nextExpiry > 0L && this.nextExpiry < System.currentTimeMillis();
    }

    public boolean shouldFFA() {
        return !this.pickedUp && this.type < 2 && this.nextFFA > 0L && this.nextFFA < System.currentTimeMillis();
    }

    public void expire(MapleMap map) {
        this.pickedUp = true;
        map.broadcastMessage(MaplePacketCreator.removeItemFromMap(this.getObjectId(), 0, 0));
        map.removeMapObject(this);
        if (this.randDrop) {
            map.spawnRandDrop();
        }
    }
}

