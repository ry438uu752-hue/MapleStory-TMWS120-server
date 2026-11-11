/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import handling.channel.ChannelServer;
import java.awt.Point;
import scripting.PortalScriptManager;
import server.MaplePortal;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class MapleGenericPortal
implements MaplePortal {
    private String name;
    private String target;
    private String scriptName;
    private Point position;
    private int targetmap;
    private int type;
    private int id;
    private boolean portalState = true;

    public MapleGenericPortal(int type) {
        this.type = type;
    }

    @Override
    public final int getId() {
        return this.id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Point getPosition() {
        return this.position;
    }

    @Override
    public final String getTarget() {
        return this.target;
    }

    @Override
    public final int getTargetMapId() {
        return this.targetmap;
    }

    @Override
    public final int getType() {
        return this.type;
    }

    @Override
    public final String getScriptName() {
        return this.scriptName;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setPosition(Point position) {
        this.position = position;
    }

    public final void setTarget(String target) {
        this.target = target;
    }

    public final void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    @Override
    public final void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public final void enterPortal(MapleClient c) {
        if (this.getPosition().distanceSq(c.getPlayer().getPosition()) > 22500.0) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
        }
        MapleMap currentmap = c.getPlayer().getMap();
        if (this.portalState || c.getPlayer().isGM()) {
            if (this.getScriptName() != null) {
                c.getPlayer().checkFollow();
                try {
                    PortalScriptManager.getInstance().executePortalScript(this, c);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (this.getTargetMapId() != 999999999) {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(this.getTargetMapId());
                if (!c.getPlayer().isGM() && to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
                    c.getPlayer().dropMessage(-1, "You are too low of a level to enter this place.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                c.getPlayer().changeMapPortal(to, to.getPortal(this.getTarget()) == null ? to.getPortal(0) : to.getPortal(this.getTarget()));
            }
        }
        if (c != null && c.getPlayer() != null && c.getPlayer().getMap() == currentmap) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }

    @Override
    public boolean getPortalState() {
        return this.portalState;
    }

    @Override
    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}

