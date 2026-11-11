/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.MapleClient;
import java.awt.Point;

public interface MaplePortal {
    public static final int MAP_PORTAL = 2;
    public static final int DOOR_PORTAL = 6;

    public int getType();

    public int getId();

    public Point getPosition();

    public String getName();

    public String getTarget();

    public String getScriptName();

    public void setScriptName(String var1);

    public int getTargetMapId();

    public void enterPortal(MapleClient var1);

    public void setPortalState(boolean var1);

    public boolean getPortalState();
}

