/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import java.awt.Point;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public abstract class AbstractMapleMapObject
implements MapleMapObject {
    private Point position = new Point();
    private int objectId;

    @Override
    public abstract MapleMapObjectType getType();

    @Override
    public Point getPosition() {
        return new Point(this.position);
    }

    @Override
    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    @Override
    public int getObjectId() {
        return this.objectId;
    }

    @Override
    public void setObjectId(int id) {
        this.objectId = id;
    }
}

