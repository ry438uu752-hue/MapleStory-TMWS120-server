/*
 * Decompiled with CFR 0.152.
 */
package server;

import java.awt.Point;
import provider.MapleData;
import provider.MapleDataTool;
import server.MaplePortal;
import server.maps.MapleGenericPortal;
import server.maps.MapleMapPortal;

public class PortalFactory {
    private int nextDoorPortal = 128;

    public MaplePortal makePortal(int type, MapleData portal) {
        MapleGenericPortal ret = null;
        ret = type == 2 ? new MapleMapPortal() : new MapleGenericPortal(type);
        this.loadPortal(ret, portal);
        return ret;
    }

    private void loadPortal(MapleGenericPortal myPortal, MapleData portal) {
        myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
        myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
        myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
        myPortal.setPosition(new Point(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y"))));
        String script = MapleDataTool.getString("script", portal, null);
        if (script != null && script.equals("")) {
            script = null;
        }
        myPortal.setScriptName(script);
        if (myPortal.getType() == 6) {
            myPortal.setId(this.nextDoorPortal);
            ++this.nextDoorPortal;
        } else {
            myPortal.setId(Integer.parseInt(portal.getName()));
        }
    }
}

