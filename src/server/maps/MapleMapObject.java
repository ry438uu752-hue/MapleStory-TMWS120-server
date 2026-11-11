/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleClient;
import java.awt.Point;
import server.maps.MapleMapObjectType;

public interface MapleMapObject {
    public int getObjectId();

    public void setObjectId(int var1);

    public MapleMapObjectType getType();

    public Point getPosition();

    public void setPosition(Point var1);

    public void sendSpawnData(MapleClient var1);

    public void sendDestroyData(MapleClient var1);
}

