/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import server.MaplePortal;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

public class MapleDoor
extends AbstractMapleMapObject {
    private WeakReference<MapleCharacter> owner;
    private MapleMap town;
    private MaplePortal townPortal;
    private MapleMap target;
    private int skillId;
    private int ownerId;
    private Point targetPosition;

    public MapleDoor(MapleCharacter owner, Point targetPosition, int skillId) {
        this.owner = new WeakReference<MapleCharacter>(owner);
        this.ownerId = owner.getId();
        this.target = owner.getMap();
        this.targetPosition = targetPosition;
        this.setPosition(this.targetPosition);
        this.town = this.target.getReturnMap();
        this.townPortal = this.getFreePortal();
        this.skillId = skillId;
    }

    public MapleDoor(MapleDoor origDoor) {
        this.owner = new WeakReference(origDoor.owner.get());
        this.town = origDoor.town;
        this.townPortal = origDoor.townPortal;
        this.target = origDoor.target;
        this.targetPosition = origDoor.targetPosition;
        this.townPortal = origDoor.townPortal;
        this.skillId = origDoor.skillId;
        this.ownerId = origDoor.ownerId;
        this.setPosition(this.townPortal.getPosition());
    }

    public final int getSkill() {
        return this.skillId;
    }

    public final int getOwnerId() {
        return this.ownerId;
    }

    private final MaplePortal getFreePortal() {
        ArrayList<MaplePortal> freePortals = new ArrayList<MaplePortal>();
        for (MaplePortal port : this.town.getPortals()) {
            if (port.getType() != 6) continue;
            freePortals.add(port);
        }
        Collections.sort(freePortals, new Comparator<MaplePortal>(){

            @Override
            public final int compare(MaplePortal o1, MaplePortal o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                }
                if (o1.getId() == o2.getId()) {
                    return 0;
                }
                return 1;
            }
        });
        for (MapleMapObject obj : this.town.getAllDoorsThreadsafe()) {
            MapleDoor door = (MapleDoor)obj;
            if (door.getOwner() == null || door.getOwner().getParty() == null || this.getOwner() == null || this.getOwner().getParty() == null || this.getOwner().getParty().getMemberById(door.getOwnerId()) == null) continue;
            freePortals.remove(door.getTownPortal());
        }
        if (freePortals.size() <= 0) {
            return null;
        }
        return (MaplePortal)freePortals.iterator().next();
    }

    @Override
    public final void sendSpawnData(MapleClient client) {
        if (this.getOwner() == null) {
            return;
        }
        if (this.target.getId() == client.getPlayer().getMapId() || this.getOwnerId() == client.getPlayer().getId() || this.getOwner() != null && this.getOwner().getParty() != null && this.getOwner().getParty().getMemberById(client.getPlayer().getId()) != null) {
            client.getSession().write((Object)MaplePacketCreator.spawnDoor(this.getOwnerId(), this.town.getId() == client.getPlayer().getMapId() ? this.townPortal.getPosition() : this.targetPosition, true));
            if (this.getOwner() != null && this.getOwner().getParty() != null && (this.getOwnerId() == client.getPlayer().getId() || this.getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
                client.getSession().write((Object)MaplePacketCreator.partyPortal(this.town.getId(), this.target.getId(), this.skillId, this.targetPosition));
            }
            client.getSession().write((Object)MaplePacketCreator.spawnPortal(this.town.getId(), this.target.getId(), this.skillId, this.targetPosition));
        }
    }

    @Override
    public final void sendDestroyData(MapleClient client) {
        if (this.getOwner() == null) {
            return;
        }
        if (this.target.getId() == client.getPlayer().getMapId() || this.getOwnerId() == client.getPlayer().getId() || this.getOwner() != null && this.getOwner().getParty() != null && this.getOwner().getParty().getMemberById(client.getPlayer().getId()) != null) {
            if (this.getOwner().getParty() != null && (this.getOwnerId() == client.getPlayer().getId() || this.getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
                client.getSession().write((Object)MaplePacketCreator.partyPortal(999999999, 999999999, 0, new Point(-1, -1)));
            }
            client.getSession().write((Object)MaplePacketCreator.removeDoor(this.getOwnerId(), false));
            client.getSession().write((Object)MaplePacketCreator.removeDoor(this.getOwnerId(), true));
        }
    }

    public final void warp(MapleCharacter chr, boolean toTown) {
        if (chr.getId() == this.getOwnerId() || this.getOwner() != null && this.getOwner().getParty() != null && this.getOwner().getParty().getMemberById(chr.getId()) != null) {
            if (!toTown) {
                chr.changeMap(this.target, this.targetPosition);
            } else {
                chr.changeMap(this.town, this.townPortal);
            }
        } else {
            chr.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }

    public final MapleCharacter getOwner() {
        return (MapleCharacter)this.owner.get();
    }

    public final MapleMap getTown() {
        return this.town;
    }

    public final MaplePortal getTownPortal() {
        return this.townPortal;
    }

    public final MapleMap getTarget() {
        return this.target;
    }

    public final Point getTargetPosition() {
        return this.targetPosition;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.DOOR;
    }
}

