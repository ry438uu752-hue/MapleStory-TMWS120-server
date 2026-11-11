/*
 * Decompiled with CFR 0.152.
 */
package server.movement;

import java.awt.Point;
import tools.data.output.MaplePacketLittleEndianWriter;

public interface LifeMovementFragment {
    public void serialize(MaplePacketLittleEndianWriter var1);

    public Point getPosition();
}

